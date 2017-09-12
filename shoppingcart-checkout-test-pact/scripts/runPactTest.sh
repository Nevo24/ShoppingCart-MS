#!/usr/bin/env bash
DIR_SCRIPT=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd -P)
if [[ $(uname -o) == Cygwin ]] ; then
	DIR_SCRIPT=$(cygpath -m $DIR_SCRIPT)
fi
echo Directory of this script = $DIR_SCRIPT

cleanup()
{
	lastExitCode=$?
	echo "===== Last Command Exit Code = $lastExitCode =====" 
	echo ==============================================================================================================
	echo Clean up background processes if any
	echo ==============================================================================================================
	if [[ -n $TASK_KILL ]] ; then
		set -x
		taskkill /f /t ${TASK_KILL[@]}
		set +x
	fi
	if [[ -n $KILL ]] ; then
		set -x
		kill ${KILL[@]}
		set +x
	fi
	if [[ -n MOCKS_TO_DELETE ]]; then
		for mockport in "${MOCKS_TO_DELETE[@]}" ; do
			echo delete mock service at port $mockport
			curl -s -X DELETE http://${URL_PACT_MOCK_SERVER}/mockserver/${mockport}
		done
	fi
	if [[ -n $CI ]] ; then
		set -x
		kubectl delete ns "$TEMP_k8s_NAMESPACE"
		set +x
	fi 
	
	if [[ -d $DIR_TEMP_K8S ]] ; then
		set -x
		rm -rf $DIR_TEMP_K8S
		set +x
	fi

	endAll=$(date +%s)
	echo Total time = $((endAll-startAll)) seconds!
	
	# testExitCode trumps the lastExitCode
	if [[ testExitCode != 0 ]] ; then
		exitCode=$testExitCode
	else
		exitCode=$lastExitCode
	fi
	echo "===== Exit Code = $exitCode =====" 
	exit $exitCode
}

trap cleanup EXIT 

build()
{
	if [[ -z $BUILD_SERVICE ]]; then
		return
	fi
	echo
	echo ==============================================================================================================
	echo Build $DIR_SERVICE, which will also generate the pact files.
	echo ==============================================================================================================
	echo mvn clean $build
	cd "$DIR_SERVICE"
	if [[ -z $URL_PACT_BROKER ]] ; then
	   mvn clean $build
	else
	   mvn clean $build -DpactBrokerUrl=${pactBrokerUrl} -DpactBrokerPort=${pactBrokerPort}
	fi
	
	if [[ $? != 0 ]]; then
		echo Build of $DIR_SERVICE failed
		exit 1
	fi 
	echo ============ The following pact files should have been published to the pact broker. 
	ls -l "${DIR_TEST}/target/pacts"
	pause
}

startMasterMockServerIfNecessary()
{
	if [[ -n $URL_PACT_MOCK_SERVER ]]; then
		return
	fi
	echo
	echo ==============================================================================================================
	echo Start a master pact mock server, Log="${DIR_TEST}/pact_mock_server.log"
	echo ==============================================================================================================
	if [[ $OS == Windows_NT ]] ; then
		cli=$DIR_TEST/target/pact-mock-server-cli/windows/pact_mock_server_cli.exe
	else
		cli=$DIR_TEST/target/pact-mock-server-cli/linux/pact_mock_server_cli
	fi		
	chmod a+x "$cli"
	echo $cli start -p 9000 | tee ${DIR_TEST}/pact_mock_server.log
	$cli start -p 9000 >> ${DIR_TEST}/pact_mock_server.log &
	KILL+=($!)
	sleep 1
}

setMockPortInConf()
{
	echo
	echo ==============================================================================================================
	echo Configure port $1 for the mock of $2.
	echo ==============================================================================================================
	echo Before: ${JAVA_OPTS_MOCK[@]} 
	JAVA_OPTS_MOCK[$3]=${JAVA_OPTS_MOCK[$3]/__mockPort__/$1}
	echo After : ${JAVA_OPTS_MOCK[@]}
	if [[ -n $CI ]] ; then
	   echo "Creating kubernetes service ${MOCK_SERVICES_CI[$3]} with port=8080 & target-port=$1"
	   cat ${DIR_TEST}/src/test/fabric8/external-service.yaml | sed "s/PORT_NUM/${1}/g" |sed "s/MS_NAME/${MOCK_SERVICES_CI[$3]}/g" | sed "s/IP_ADDR/`hostname -i`/g" | kubectl apply -n $TEMP_k8s_NAMESPACE -f - 
	fi 
}

startAMockWithCentralServer()
{
	set -x
	output=$(curl -s -X POST  -H "Content-Type: application/json" -d "@$1" ${URL_PACT_MOCK_SERVER})
	set +x
	mockport=`echo $output | awk -F":" '{print $NF}' | awk -F"}" '{print $1}'`
	setMockPortInConf $mockport $1 $2
	MOCKS_TO_DELETE+=(mockport)
}

startAMockLocally()
{
	echo $cli create -p 9000 -f $1
	output=$($cli create -p 9000 -f $1)
	echo $output
	mockport=${output*Dependencies classpath:}
	lookup=${lookup%%\[INFO\]*}
	echo $lookup | tr ";" "\n"
}

service()
{
	echo
	echo ==============================================================================================================
	echo Deploy service jars to $MSNEXT_SERVICE
	echo ==============================================================================================================
	echo cp -r ${DIR_SERVICE}/*-exe/target/*-exe*.jar "${MSNEXT_SERVICE}/deploy"
	     cp -r ${DIR_SERVICE}/*-exe/target/*-exe*.jar "${MSNEXT_SERVICE}/deploy"
        echo cp -r ${DIR_SERVICE}/*-exe/target/conf.d/* "${MSNEXT_SERVICE}/etc/conf.d"
             cp -r ${DIR_SERVICE}/*-exe/target/conf.d/* "${MSNEXT_SERVICE}/etc/conf.d"
	result=$?
	echo cp `ls ${DIR_SERVICE}/*-gen-module/target/*-gen-module*.jar` "${MSNEXT_SERVICE}/deploy"
	cp `ls ${DIR_SERVICE}/*-gen-module/target/*-gen-module*.jar` "${MSNEXT_SERVICE}/deploy"
	((result+=$?))
	if [[ $result != 0 ]]; then
		echo  Unable to deploy the service jars to $MSNEXT_SERVICE
		exit 1
	fi
	pause

	echo
	echo ==============================================================================================================
	echo Start up the service. Log="${DIR_TEST}/service.log"
	echo ==============================================================================================================
	jacocoAgentJar=$(ls $DIR_MSNEXT/*/javaagents/jacocoagent.jar)
	conf=("-javaagent:${jacocoAgentJar}=output=tcpserver,address=*,port=6300")
	conf+=(${JAVA_OPTS_SERVICE[@]})
	conf+=(${JAVA_OPTS_LOCAL[@]})
	conf+=(${JAVA_OPTS_COMMON[@]})
	conf+=(${JAVA_OPTS_MOCK[@]})
	conf+=(-Dserver.port=8080)
        if [ -n "$doEvents" ] ; then
            echo Enabling events
            conf+=" -Dspring.cloud.stream.enabled=true"
        fi
	export DOX_JAVA_OPTS=${conf[@]}
	export DOX_EXCLUDED_MODULES=msnext-customization 
	log="${DIR_TEST}/service.log"
	echo DOX_JAVA_OPTS=${conf[@]} |& tee "$log" 
	if [[ $OS == Windows_NT ]] ; then
		chmod a+x "${MSNEXT_SERVICE}/bin/debug-windows.cmd"
		"${MSNEXT_SERVICE}/bin/debug-windows.cmd"  >> "$log" 2>&1 &
		TASK_KILL+=(/pid $!)
	else
		"${MSNEXT_SERVICE}/bin/start.sh"  2>&1 >> "$log" 2>&1 &
		KILL+=($!)
	fi		
	waitFor "http://localhost:8081/health" "The service"
	tail "$log"
	echo
	pause $PAUSE_AT_service
}
	
serviceCI()
{
	echo
	echo ==============================================================================================================
	echo Start up the service  in $TEMP_k8s_NAMESPACE using $ServiceYml
	echo ==============================================================================================================
	set -x
	kubectl apply -n $TEMP_k8s_NAMESPACE -f $ServiceYml
	result=$?
	set +x
	if [[ $? -ne 0 ]]; then
		echo "Error in applying Kubernetes template: $ServiceYml"
		exit 1
	fi
	sleep 20

	echo Get Service URLs/Ports running on K8s
	log="${DIR_TEST}/service.log"
	exe="`ls -d ${DIR_SERVICE}/*-exe`"
    service_exe=$(basename $exe)
    service=${service_exe/%-exe/-service}
	echo service=$service
	echo === Service Spec ====
	kubectl get svc $service -n $TEMP_k8s_NAMESPACE -o json
	# The ports are listed alphabetically!
	export svc_k8s_port=`kubectl get svc $service -n $TEMP_k8s_NAMESPACE -o jsonpath='{.spec.ports[?(@.name == "http")].nodePort}'`
	echo svc_k8s_port=$svc_k8s_port
	export jacoco_k8s_port=`kubectl get svc $service -n $TEMP_k8s_NAMESPACE -o jsonpath='{.spec.ports[?(@.name == "jacoco")].nodePort}'`
	echo jacoco_k8s_port=$jacoco_k8s_port
	export management_k8s_port=`kubectl get svc $service -n $TEMP_k8s_NAMESPACE -o jsonpath='{.spec.ports[?(@.name == "management")].nodePort}'`
	echo management_k8s_port=$management_k8s_port
	export svc_pod_name=`kubectl get pods -n $TEMP_k8s_NAMESPACE --selector="project=$service" -o jsonpath='{.items[0].metadata.name}'`
	echo svc_pod_name=$svc_pod_name
	export svc_pod_hostIP=`kubectl get pod $svc_pod_name -n $TEMP_k8s_NAMESPACE -o jsonpath='{.status.hostIP}'`
	echo svc_pod_hostIP=$svc_pod_hostIP
    
    couchbaseCI
    logCI "$svc_pod_name" "$log"
	echo Waiting until atleast 1 pod is in Running State
	waitFor "http://${svc_pod_hostIP}:${management_k8s_port}/health" "The service"
	logCI "$svc_pod_name" "$log"
	pause $PAUSE_AT_service
}
	
couchbaseCI()
{
    echo "Finding couchbase service..."
    export cb_pod_name=`kubectl get pods -n $TEMP_k8s_NAMESPACE --selector="type=couchbase-server" -o jsonpath='{.items[0].metadata.name}'`
    echo cb_pod_name=$cb_pod_name
    export cb_svc_clusterIP=`kubectl get svc couchbase-service -n $TEMP_k8s_NAMESPACE -o jsonpath='{.spec.clusterIP}'`
    echo cb_svc_clusterIP=$cb_svc_clusterIP
}

test()
{
	echo ==============================================================================================================
	echo Run the test. Log="${DIR_TEST}/test.log"
	echo ==============================================================================================================
	cd $DIR_TEST
	conf=()
	if [[ -n $CI ]] ; then
		conf+=(-gs $DIR_SERVICE/cfg/maven/ci/settings.xml)
		conf+=(-Dprovider.host=${svc_pod_hostIP})
		conf+=(-Dprovider.port=${svc_k8s_port})
		conf+=(-Dcouchbase.providers[0].env.clusterUrl=${cb_svc_clusterIP}) 
        conf+=(${JAVA_OPTS_CI[@]})
		confDump+=(-gs $DIR_SERVICE/cfg/maven/ci/settings.xml)
		confDump+=(-Djacoco.address=${svc_pod_hostIP})
		confDump+=(-Djacoco.port=${jacoco_k8s_port})
    else 
		conf+=(-Dprovider.host=127.0.0.1)
		conf+=(-Dprovider.port=8080)
		conf+=(${JAVA_OPTS_LOCAL[@]})
	fi
	if [[ -n $URL_PACT_BROKER ]] ; then
		conf+=(-DpactBrokerHost=${pactBrokerHost})
		conf+=(-DpactBrokerPort=${pactBrokerPort})
	fi
	conf+=(${JAVA_OPTS_PROVIDER_STATE[@]})
	conf+=(-DproviderStateBaseFilePath=$DIR_PROVIDER_STATE)
	conf+=(${JAVA_OPTS_COMMON[@]})
	conf=${conf[@]}
	log="${DIR_TEST}/test.log"

	# Do not quote $conf, or else the command line options will be passed in as one option.
	echo mvn test -Dtest=RunPactTest $conf |& tee "$log"
	mvn test -Dtest=RunPactTest $conf >> "$log" 2>&1
	testExitCode=$?
	echo "===== Test Exit Code = $testExitCode =====" 
	tail --lines=20 "$log"
	echo

    if [[ -n $CI ]] ; then
       logCI "$svc_pod_name" "service.log"
    fi
    
    if [ -n "$doEvents" ] ; then
        # TODO see if there is a way to get mvn to tell the fat jar rather than the plain jar
        testKafkaJar=$(getMsbInstalledJars msb-testing-kafka | sed -e 's:\\:/:g' -e 's:\.jar:-jar-with-dependencies.jar:')
        mkdir -p $(dirname $KAFKA_APPROVED_EVENTS_FILE) $(dirname $KAFKA_NEW_EVENTS_FILE)
        java -jar $testKafkaJar --topic $KAFKA_TOPIC --propertiesFile $KAFKA_PROPERTIES_FILE --outputFile $KAFKA_NEW_EVENTS_FILE
        echo diff $KAFKA_NEW_EVENTS_FILE $KAFKA_APPROVED_EVENTS_FILE &>> "$log"
        if diff $KAFKA_NEW_EVENTS_FILE $KAFKA_APPROVED_EVENTS_FILE &>> "$log" ; then
            echo "Events produced: SUCCESS"
        else
            echo "Events produced: FAILURE see $log"
        fi

    fi

	echo ==============================================================================================================
	echo Test finished. Please find the results in "${DIR_TEST}/test.log"
	echo ==============================================================================================================
	echo
	pause $PAUSE_AT_test

	echo ==============================================================================================================
	echo Dump Jacoco execution data from the service. exec="${DIR_SERVICE}/target/jacoco-unit.exec"
	echo ==============================================================================================================
#	echo mvn jacoco:dump ${confDump[@]} -Djacoco.port=6300
#	mvn jacoco:dump ${confDump[@]} -Djacoco.port=6300
	echo mvn jacoco:dump ${confDump[@]}
	mvn jacoco:dump ${confDump[@]}
	echo
	pause $PAUSE_AT_jacoco

}

pause()
{
	if [[ $interactive == true || $1 == true ]]; then
		read -p "Push Enter to proceed (or CTRL-C to exit)..." answer
	fi
}

logCI()
{
    #kubectl logs will always return the complete log, so don't append to the file.
    kubectl logs $1 -n $TEMP_k8s_NAMESPACE > $2
}

checkMsnext()
{
	if [[ -n $CI ]]; then
		# CI run does not do MSNEXT!
		return
	fi
	echo ============ Check $1 = $2
	if [[ -z $2 ]]; then
		echo $1 is not set. Please create an MSNEXT runtime environment and point $1 to it.
		exit 1
	fi
	if [[ ! -d $2 ]]; then
		echo Not a directory: $1 = $2
		exit 1
	fi 
	if [[ ! -d $2/deploy ]]; then
		echo Not a directory: $1/deploy=$2/deploy
		exit 1
	fi 
	if [[ ! -f $2/bin/start.sh ]]; then
		echo Unable to find : $1/bin/start.sh=$2/bin/start.sh
		exit 1
	fi
	if [[ ! -f $2/bin/debug-windows.cmd ]]; then
		echo Unable to find : $1/bin/debug-windows.cmd=$2/bin/debug-windows.cmd
		exit 1
	fi
	echo Appears to be valid: $1=$2 
}

checkPactBroker()
{
	echo curl -s -X GET "$1"
	output=$(curl -s -X GET "$1")
	if [[ "$output" =~ "$1" ]]; then
		echo Pact Broker is up and running at $1
	else
		echo $output
		echo Pact broker URL is invalid : $1
		exit 1
	fi
}

check()
{
	echo ==============================================================================================================
	echo Check Configurations
	echo ==============================================================================================================
	echo This script is in $DIR_SCRIPT
	echo ============ Check micro service $DIR_SERVICE
	if [[ -z $DIR_SERVICE ]]; then
		DIR_SERVICE=$(realpath $DIR_SCRIPT/../..)
		echo DIR_SERVICE is not set. Look for microservice under $DIR_SERVICE
	fi
	if [[ ! -d $DIR_SERVICE ]]; then
		echo Not a directory: DIR_SERVICE=$DIR_SERVICE
		exit 1
	fi 
	dir="`ls -d ${DIR_SERVICE}/*-exe`"
	if [[ ! -d "$dir" ]]; then
		echo  ${DIR_SERVICE} is missing have an exe sub-project.
		exit 1
	fi 
	dir="`ls -d ${DIR_SERVICE}/*-gen-module`"
	if [[ ! -d "$dir" ]]; then
		echo  ${DIR_SERVICE} is missing have a gen-module sub-project.
		exit 1
	fi 
	if [[ ! -f ${DIR_SERVICE}/pom.xml ]]; then
		echo Unable to find pom.xml: ${DIR_SERVICE}/pom.xml
		exit 1
	fi
	echo Micro service = $DIR_SERVICE
	
	echo ============ Check micro service test $DIR_TEST
	if [[ -z $DIR_TEST ]]; then
		DIR_TEST=$(realpath $DIR_SCRIPT/..)
		echo DIR_TEST is not set. Look for microservice test under $DIR_TEST
	fi
	if [[ ! -d $DIR_TEST ]]; then
		echo Not a directory: DIR_TEST = $DIR_TEST
		exit 1
	fi 
	if [[ ! -f ${DIR_TEST}/pom.xml ]]; then
		echo Unable to find pom.xml: ${DIR_TEST}/pom.xml
		exit 1
	fi
	echo Micro service test = $DIR_TEST
	
	echo ============ Check pact broker
	if [[ -z $URL_PACT_BROKER ]]; then
		echo URL_PACT_BROKER is not set.
		if [[ $devMode == true ]]; then
			echo -d \(Development mode\) is present. Try use http://${USER}01:31000 for pact broker
			echo If http://${USER}01:31000 does not exist. Please set URL_PACT_BROKER explicitly in the configuration.
			URL_PACT_BROKER=http://${USER}01:31000
			checkPactBroker "$URL_PACT_BROKER"
		else
			echo Use default pact broker from $DIR_TEST/pom.xml
			cd "$DIR_TEST"
			URL_PACT_BROKER_DEFAULT=$(mvn org.apache.maven.plugins:maven-help-plugin:2.1.1:evaluate  -Dexpression=pactBrokerUrl | grep 31000)
			if [[ -z $URL_PACT_BROKER_DEFAULT ]]; then
				echo $DIR_TEST/pom.xml does not set a pactBrokerUrl property
				echo Unable to check if the pack broker is up and running 
			else
				checkPactBroker "$URL_PACT_BROKER_DEFAULT"
			fi
		fi
	else
		echo URL_PACT_BROKER=$URL_PACT_BROKER
		checkPactBroker "$URL_PACT_BROKER"
	fi
	
	# parse the HOST and PORT out of $URL_PACT_BROKER so that they can be passed into
	# pact testing individually. This change is required by the change to pact-jvm-junit-provider
	# which does takes pact broker in host:port instead of url.
	if [[ -n $URL_PACT_BROKER ]] ; then
		pactBrokerHostPort=${URL_PACT_BROKER#http://} # cut http://
		pactBrokerHostPort=${pactBrokerHostPort%%/*} # cut the tail starting with /
		pactBrokerHost=${pactBrokerHostPort%:*} # cut the port part
		pactBrokerPort=${pactBrokerHostPort##*:} # cut the host part
		echo pactBrokerHost=${pactBrokerHost}
		echo pactBrokerPort=${pactBrokerPort}
	fi	
	
	echo ============ Check the centralized pact mock server URL $URL_PACT_MOCK_SERVER
	if [[ -z $URL_PACT_MOCK_SERVER ]]; then
		echo Centralized pact mock server URL is not given. Run the native one.
	else
		echo curl -s -X GET "$URL_PACT_MOCK_SERVER"
		output=$(curl -s -X GET "$URL_PACT_MOCK_SERVER")
		if [[ "$output" =~ "mockServers" ]]; then
			echo Master pact mocker server is up and running at $URL_PACT_MOCK_SERVER
		else
			echo $output
			echo Master pact mocker server is invalid : $URL_PACT_MOCK_SERVER
			invalidMockServer=true
		fi
	fi
	
	echo "============ Check pact(s) to mock"
	echo ${PACTS[*]}

	echo ============ Check if doing CI
	if [[ -n $BRANCH_NAME ]]; then
		echo Doing CI. BRANCH_NAME=$BRANCH_NAME
		CI=CI
		 
		exe="`ls -d ${DIR_SERVICE}/*-exe`"
		ServiceYml=$exe/target/classes/META-INF/fabric8/kubernetes.yml
		if [[ ! -f $ServiceYml ]]; then
			echo Service yml not found: $ServiceYml
			exit 1
		else
			echo Service yml is found: $ServiceYml
		fi
			
		echo
		echo ==============================================================================================================
		echo For test only: Create a temp kubernetes.yml with jacoco turned-on.
		echo ==============================================================================================================
		DIR_TEMP_K8S=$(mktemp -t -d k8s.XXX)
		cp $ServiceYml $DIR_TEMP_K8S
		ServiceYml=${DIR_TEMP_K8S}/kubernetes.yml
		jacocoOff="-Dloader.main=com.amdocs.msbase.app.Application"
		jacocoOn="${jacocoOff} -javaagent:javaagents\\/jacocoagent.jar=output=tcpserver,address=*,port=6300"
		sed -i -e "s/${jacocoOff}/${jacocoOn}/" $ServiceYml
		check=$(grep jacocoagent $ServiceYml)
		if [[ -n $check ]] ; then
			echo Effective kubernetes.yml: $ServiceYml
			echo Jacoco option: $check
		else
			echo Failed to turn on jacoco
			exit 1
		fi 
		
		# Note: This is not the best place for this. But wee need create the name space ahead of the mocks 
		echo
		echo ==============================================================================================================
		echo Create a temporary k8s name space for testing
		echo ==============================================================================================================
		export TEMP_k8s_NAMESPACE=pact-testns-`date +%s`
		echo Create TEMP_k8s_NAMESPACE=$TEMP_k8s_NAMESPACE
		kubectl create ns $TEMP_k8s_NAMESPACE
		
		echo
		echo ==============================================================================================================
		echo Deploy Couchbase in $TEMP_k8s_NAMESPACE
		echo ==============================================================================================================
		kubectl apply -n ${TEMP_k8s_NAMESPACE} -f $DIR_TEST/src/test/resources/couchbase-deployment.yaml 
		kubectl apply -n ${TEMP_k8s_NAMESPACE} -f $DIR_TEST/src/test/resources/couchbase-service.yaml 		
		sleep 30

		
		
	else
		echo You are not doing CI.
		CI=
	fi
	
	checkMsnext MSNEXT_SERVICE $MSNEXT_SERVICE
	
	pause $PAUSE_AT_check
}


function usage ()
{
cat << EOF
usage: $0 options

This script runs pact testing.

OPTIONS:
   -h	Optional. Show the help message.
   -c	Optional. Pass a configuration bash file to be sourced, e.g. -c conf.sh default: no configuration file
   -p	Optional. Pause after the named step, i.e -p <step>. default: non-stop
 		Here are the available steps:
 			check : sanity-check the configurations
 			mock : create the mocks
 			service : launch the service
 			test : run the pact test
 			jacoco: run jacoco
		Note:
			When the test is paused, press Enter to proceed or Ctrl-C to exit.
 		Examples:
   		-p mock: Pause after the mock(s) are created (if you need to do something against the mock(s)).
   		-p service: Pause after the service is launched (if you need a fully functional service to work with).
   		-p mock -p service: Pause after mock creation and then pause again after service launch.
   -i	Optional. Run in interactive/troubleshooting mode, i.e. pause at every step. default: non-stop.
   		Note:
   			-i == -p check -p mock -p service -p test
   -d	Optional. Run in development mode. Currently this means:
   		Deafult Pact Broker = ${User}01, as opposed to the one specified in the pom.xml
EOF
}

checkArgIn()
{
	valid=$2
	for arg in ${valid[@]} ; do
		if [[ $1 == $arg ]] ; then
			return
		fi
	done
	echo Invalid argument $1. Valid ones are: ${valid[@]}
	usage
	exit	
}

startAll=$(date +%s)

while getopts "hc:ip:ed" OPTION
do
     case $OPTION in
         h)
         	usage
         	exit
         	;;
         c)
         	source $OPTARG
         	;;
         i)
         	interactive=true
         	;;
         p)
         	valid=(check mock service test jacoco)
         	checkArgIn $OPTARG $valid
			eval "PAUSE_AT_$OPTARG=true"
			;;
         e)
             doEvents=true
             ;;
         d)
             devMode=true
             ;;
         ?)
         	usage
         	exit
         	;;
     esac
done

check
build
mock
service${CI}
test
