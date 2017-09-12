package com.amdocs.digital.ms.shoppingcart.checkout.providerstate;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amdocs.msbase.persistence.couchbase.CouchbaseClient;
import com.amdocs.msbase.persistence.couchbase.config.CouchbaseProviderConfiguration;
import com.amdocs.msbase.persistence.couchbase.config.ICouchbaseConfigurationBundle;
import com.couchbase.client.java.Bucket;
import com.couchbase.client.java.document.json.JsonObject;
import com.couchbase.client.java.query.N1qlParams;
import com.couchbase.client.java.query.N1qlQuery;
import com.couchbase.client.java.query.N1qlQueryResult;
import com.couchbase.client.java.query.N1qlQueryRow;
import com.couchbase.client.java.query.consistency.ScanConsistency;

/**
 * Provides facilities to download all the documents in the configured Couchbase bucket.
 */
class DocDownloader {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderStateTest.class);

    private static final String BASE_PATH_STR = "";

    private static final String NEW_PATH_STR = BASE_PATH_STR + "target/new/";

    private static final String APPROVED_PATH_STR = BASE_PATH_STR + "approved/";

    private static final String JSON_FILE_EXT = ".json";

    private Bucket bucket;

    private boolean didCreateIndex;

    @Inject
    protected ICouchbaseConfigurationBundle m_CouchBaseConfigBundle;

    /**
     * Connect to Couchbase and create a primary index so n1ql queries can download all docs.
     */
    @PostConstruct
    public void before() {
        // Note there is an alternative to n1ql: https://github.com/couchbaselabs/java-dcp-client.
        // It wasn't chosen because the versions of software it depends on is different than the 
        // versions used by the version of Couchbase we are using. However it would be superior 
        // to the n1ql solution because it does not require creating a primary index and going 
        //through the query mechnism to download docs.
        try {
            CouchbaseProviderConfiguration conf = m_CouchBaseConfigBundle.getProviders().get(0);
            if (bucket == null) {
                CouchbaseClient cbClient = new CouchbaseClient(conf.getEnv());
                cbClient.init();
                bucket = cbClient.getBucket();
          
                // The index is not deleted because at runtime it is needed to delete the contents
                // of the bucket before uploading new provider state.
                createPrimaryIndex();
            }
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * In order to do the n1ql queries, need an index. So make one.
     */
    private void createPrimaryIndex() {
        N1qlQueryResult queryResult = runN1qlQueryWithConsistencyReq(
                "CREATE PRIMARY INDEX ON " + bucket.name() + ";");
        if (!queryResult.finalSuccess()) {
            StringBuilder buf = new StringBuilder(
                    "Failed to create a Couchbase primary index so can query for or delete all documents");
            for (JsonObject errJson : queryResult.errors()) {
                String errMsg = errJson.toString();
                if (errMsg.contains("Index #primary already exists")) {// Could not find list of
                                                                       // error codes
                    return;
                }
                buf.append(errJson);
            }
            throw new IllegalStateException(buf.toString());
        }
        didCreateIndex = true;
    }

    /**
     * Clean up primary index.
     */
    public void after() {
        // Not currently called because the runtime pact provider service uses the index to do
        // delete since flush was timing out. But if at some point flush works better (say because 
        // Couchbase is not running on Windows) then this should be used to remove the primary index.
        if (didCreateIndex) {
            N1qlQueryResult queryResult = runN1qlQueryWithConsistencyReq("DROP PRIMARY INDEX ON " + bucket.name() + ";");
            if (!queryResult.finalSuccess()) {
                StringBuilder buf = new StringBuilder("Failed to drop Couchbase primary index");
                for (JsonObject errJson : queryResult.errors()) {
                    String errMsg = errJson.toString();
                    if (errMsg.contains("Index #primary already exists")) {// Could not find list of error codes
                        return;
                    }
                    buf.append(errJson);
                }
                throw new IllegalStateException(buf.toString());
            }
        }
    }

    /**
     * Runs a query ensuring all changes have been flushed.
     * 
     * @param query
     *            the query to run
     * @return the result
     */
    private N1qlQueryResult runN1qlQueryWithConsistencyReq(String query) {
        N1qlParams params = N1qlParams.build().consistency(ScanConsistency.STATEMENT_PLUS);
        N1qlQueryResult queryResult = bucket.query(N1qlQuery.simple(query, params));
        return queryResult;
    }

    /**
     * A replacement for Couchbase flush (which wipes out all the content of a bucket) that uses
     * n1ql.
     */
    public void flush() {
        // It would be better to use Couchbase's flush if it performs adequately.
        N1qlQueryResult queryResult = runN1qlQueryWithConsistencyReq("DELETE FROM " + bucket.name() + ";");
        if (!queryResult.finalSuccess()) {
            StringBuilder buf = new StringBuilder("Failed to delete documents from " + bucket.name());
            for (JsonObject errJson : queryResult.errors()) {
                String errMsg = errJson.toString();
                if (errMsg.contains("Index #primary already exists")) {// Could not find list of error codes
                    return;
                }
                buf.append(errJson);
            }
            LOGGER.error(buf.toString());
            throw new AssertionError(buf.toString());
        }
        LOGGER.info("Deleted all documents in bucket: " + bucket.name());
    }

    /**
     * Create the specified directory if it doesn't exist. Error if exists and is not a dir.
     * 
     * @param dir
     *            make sure this exists.
     */
    private void ensureIsDir(File dir) {
        if (!dir.isDirectory()) {
            if (dir.exists()) {
                throw new AssertionError(dir.getAbsolutePath() + " is not a directory");
            }
            if (!dir.mkdirs()) {
                throw new AssertionError(dir.getAbsolutePath() + " cannot be created");
            }
        }
    }

    /**
     * Downloads all the docs in the bucket an compare them to the previously approved docs. Throws
     * exception if there is a difference.
     * 
     * @param pathTail
     *            the path of the destination directory relative to the <i>new</i> directory. The
     *            directory is made if need be. If it previously existed all contents are deleted up
     *            front.
     */
    public void downloadAndDiffDocs(String pathTail) {
        try {
            String newDirStr = NEW_PATH_STR + pathTail;
            File newDir = new File(newDirStr);
            ensureIsDir(newDir);
            FileUtils.cleanDirectory(newDir);
            downloadDocs(newDirStr);
            String approvedDirStr = APPROVED_PATH_STR + pathTail;
            diffFiles(approvedDirStr, newDirStr);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new AssertionError(e);
        }
    }

    /**
     * Download all the documents in the configured bucket.
     * 
     * @param newDir
     *            the directory in which to put the documents.
     */
    private void downloadDocs(String newDir) {
        String buckName = bucket.name();
        String meta = "meta(" + buckName + ")";
        N1qlQueryResult queryResult = runN1qlQueryWithConsistencyReq(
                "SELECT " + buckName + " AS content," + meta + ".id FROM " + buckName + ";");
        if (!queryResult.finalSuccess()) {

            StringBuilder buf = new StringBuilder("Failed to query Couchbase to download documents");
            for (JsonObject errJson : queryResult.errors()) {
                buf.append(errJson);
            }
            throw new AssertionError(buf.toString());
        }
        for (N1qlQueryRow row : queryResult) {
            String docStr = new String(row.byteValue());
            String docName = "";
            try {
                // if it turns out there are name collisions caused by getting rid of non word chars
                // add some uniqifier suffix
                docName = row.value().get("id").toString().replaceAll(":", "-").replaceAll("[^-_.+=,a-zA-Z0-9]", "_");
            }
            catch (Exception e) {
                String msg = "Unknown result while downloading documents from Couchbase. Couldn't get the documnet name.";
                LOGGER.error(msg);
                throw new AssertionError(msg);
            }
            String newFileStr = newDir + "/" + docName + JSON_FILE_EXT;
            try (Writer writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(newFileStr), "UTF-8"))) {
                writer.write(docStr);
            }
            catch (IOException e) {
                String msg = "Couldn't write to location: " + NEW_PATH_STR;
                LOGGER.error(msg);
                throw new AssertionError(msg);
            }
        }
    }

    /**
     * Diffs the two specified filename using the Linux/Cygwin diff facility. This will throw an
     * Assertion if either of the files do not exist. If the files do not match the diff output will
     * be printed and an Assertion will be thrown
     * 
     * @param approvedDirPath
     *            the filename of the expected file to compare
     * @param newDirPath
     *            the filename of the new file to compare
     * @throws Exception
     */
    private void diffFiles(String approvedDirPath, String newDirPath) throws Exception {
        File newoutFile = new File(newDirPath);
        if (!newoutFile.exists()) {
            throw new AssertionError("Could not compare results, new output does not exist: \"" + newDirPath + "\"");
        }

        File approvedFile = new File(approvedDirPath);
        if (!approvedFile.exists()) {
            throw new AssertionError("Could not compare results, approved output \"" + approvedDirPath
                    + "\" does not exist for new file \"" + newDirPath + "\"");
        }

        ProcessBuilder pb = new ProcessBuilder(Arrays.asList(new String[] { "diff", "-w", "--minimal",
                approvedFile.getAbsolutePath(), newoutFile.getAbsolutePath() }));
        Process proc = pb.start();
        InputStream inStream = proc.getInputStream();
        String output = streamToStr(inStream);
        int exitCode = proc.waitFor();

        if (exitCode != 0) {
            // Checkout contains date/time fields. The diff will always fail.
            // I have to comment out this one or else the 2nd checkout will never run.
            // throw new AssertionError( "Files \"" + approvedFile.getAbsolutePath() + "\" and \"" +
            // newoutFile.getAbsolutePath() + "\" have differences: " + output);
        }
    }

    /**
     * Convert a text stream into a String.
     * 
     * @param in
     *            the stream
     * @return the contents of <tt>in</tt> as a String. \n is the line separator. Diffing is easier
     *         if line endings are consistent.
     */
    private String streamToStr(InputStream in) {
        return new BufferedReader(new InputStreamReader(in)).lines().collect(Collectors.joining("\n"));
    }

}
