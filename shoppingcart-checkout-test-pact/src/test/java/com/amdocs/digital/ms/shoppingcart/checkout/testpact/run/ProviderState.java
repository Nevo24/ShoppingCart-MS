package com.amdocs.digital.ms.shoppingcart.checkout.testpact.run;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.client.java.document.RawJsonDocument;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.consistency.ScanConsistency;

public class ProviderState {
	private static final String ID = "id";
	private static final String CONTENT = "content";

	private File basePath;
	private boolean didCreateIndex;
	private Bucket bucket;

	
	/**
	 * @return a property for a given name.  Exception if property value is null or empty.
	 */
	static String requireProperty( String propName) {
	    String retval = System.getProperty(propName);
	    if ( retval == null || retval.isEmpty()) {
	        throw new IllegalStateException( "The system property " + propName + " has no value");
	    }
	    return retval;
	}
	
	public ProviderState() {
	    String clusterUrl = requireProperty( "couchbase.providers[0].env.clusterUrl");
	    String bucketName = requireProperty( "couchbase.providers[0].env.bucketName");
	    Cluster cluster = CouchbaseCluster.create(clusterUrl);
        bucket = cluster.openBucket( bucketName, 1, TimeUnit.MINUTES);
        createPrimaryIndex();
        
        String baseFilePath = "providerStateBaseFilePath";
        String basePathStr = requireProperty(baseFilePath);
		basePath = new File( basePathStr);
		if ( !basePath.exists()) {
			throw new IllegalStateException(
                "The base path directory for the provider state files does not exist.  It is given by the property "
                + baseFilePath + "=" + basePathStr);
		}
	}
	
	// This only exists to support the n1ql delete as the implementation of flush.
	private void createPrimaryIndex() {
		N1qlQueryResult queryResult = runN1qlQueryWithConsistencyReq("CREATE PRIMARY INDEX ON " + bucket.name() + ";");
		if ( !queryResult.finalSuccess()) {
			StringBuilder buf = new StringBuilder(
			        "Failed to create a Couchbase primary index so can query for or delete all documents");
			for (JsonObject errJson : queryResult.errors()) {
				String errMsg = errJson.toString();
				if ( errMsg.contains("Index #primary already exists")) {// Could not find list of error codes
					return;
				}
				buf.append( errJson);
			}
			throw new IllegalStateException( buf.toString());
		}
		didCreateIndex = true;
	}
	
	// Either add some way to invoke this when the service is shutdown, or switch back to using flush().  It seems
	// http://stackoverflow.com/questions/26547532/how-to-shutdown-a-spring-boot-application-in-a-correct-way
	// may be the best way.  The question is how to get maven to invoke the shutdown URL.
	// Also how to get a call back when shutdown occurs.
	public void after() {
		if ( didCreateIndex) {
			N1qlQueryResult queryResult = runN1qlQueryWithConsistencyReq("DROP PRIMARY INDEX ON " + bucket.name() + ";");
			if ( !queryResult.finalSuccess()) {
				StringBuilder buf = new StringBuilder("Failed to drop Couchbase primary index");
				for (JsonObject errJson : queryResult.errors()) {
					String errMsg = errJson.toString();
					if ( errMsg.contains("Index #primary already exists")) {// Could not find list of error codes
						return;
					}
					buf.append( errJson);
				}
				throw new IllegalStateException( buf.toString());
			}
		}
	}

	private N1qlQueryResult runN1qlQueryWithConsistencyReq(String query) {
		N1qlParams params = N1qlParams.build().consistency(ScanConsistency.STATEMENT_PLUS);
		N1qlQueryResult queryResult = bucket.query(N1qlQuery.simple(query, params));
		return queryResult;
	}


	public void flush() {
		// This was timing out on my PC:   bucket.bucketManager().flush();
		// So am doing a n1ql instead
		N1qlQueryResult queryResult = runN1qlQueryWithConsistencyReq("DELETE FROM " + bucket.name() + ";");
		if ( !queryResult.finalSuccess()) {
			StringBuilder buf = new StringBuilder("Failed to delete documents from " + bucket.name());
			for (JsonObject errJson : queryResult.errors()) {
				String errMsg = errJson.toString();
				if ( errMsg.contains("Index #primary already exists")) {// Could not find list of error codes
					return;
				}
				buf.append( errJson);
			}
			throw new IllegalStateException( buf.toString());
		}
	}

	
	public void establishState( String given) throws Exception {
	    if ( "emptyDb".equals(given)) {
            flush();
            return;
	    }
        String relFilesStr = given;

        String[] relFiles = relFilesStr.split(" ");
        List<File> absFiles = new ArrayList<>(relFiles.length);
        for (String relFile : relFiles) {
            absFiles.add( new File( basePath, relFile));
        }
        List<RawJsonDocument> rawDocs = readJsons(absFiles);
		uploadDocs( rawDocs);
	}

	/**
	 * Read in the files the specified files.
	 * @param files each file should normally be a directory.  It is allowed for <i>file</i>.json to be a file.
	 * @return a list of RawJsonDocument obtained by traversing the directories and files.
	 * @throws Exception
	 */
	private List<RawJsonDocument> readJsons(List<File> files) throws Exception {
		List<RawJsonDocument> retval = new ArrayList<>( files.size() * 2);
		for (File file : files) {
			if ( !file.exists()) {
				File newFile = new File( file.getCanonicalPath() + ".json");
				if ( !file.exists()) {
					throw new IllegalStateException( "Neither " + file.getCanonicalPath()
					                                  + " nor " + file.getCanonicalPath() + ".json exists");
				}
				file = newFile;
			}
			try (Stream<Path> paths = Files.walk(Paths.get(file.toString()))) {
				paths.forEach(path -> {
					try {
						if ( !Files.isDirectory(path)) { 
							String docAndMetaAsJsonStr = new String( Files.readAllBytes(path));
							// Could be optimized by using some other facility that doesn't fully parse the
							// doc into Maps.
							JsonObject jObj = JsonObject.fromJson(docAndMetaAsJsonStr);
						    
							RawJsonDocument rawDoc = RawJsonDocument.create(
									jObj.getString(ID),
									jObj.get( CONTENT).toString(), // Note: generated id doc has Integer here not JsonObject
									17); // requests can always use 17 for each doc in the etag.
							retval.add( rawDoc);
						}
					} catch (Exception e) {
						throw new IllegalStateException( e);
					}
				});
			}
		}
		return retval;
	}
			
	/**
	 * Upload the specified documents.
	 * @param docs the documents to upload.
	 * @throws Exception
	 */
	private void uploadDocs(List<RawJsonDocument> docs) throws Exception {
		flush();
		for (RawJsonDocument doc : docs) {
			bucket.insert( doc);
		}
	}

}
