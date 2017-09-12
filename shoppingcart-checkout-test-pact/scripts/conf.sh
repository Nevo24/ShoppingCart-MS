#!/usr/bin/env bash

echo ===================================================================
echo This script contains items that are configurable.
echo The settings within this script are for demo purpose.
echo Please feel free to configure your test to fit your environment.
echo ===================================================================

set -x
#===================================================================
# DIR_XXX, Diectory settings.
#===================================================================
DIR_SCRIPT=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd -P)
if [[ $(uname -o) == Cygwin ]] ; then
	DIR_SCRIPT=$(cygpath -m $DIR_SCRIPT)
fi
echo Directory of this script = $DIR_SCRIPT
DIR_SERVICE=$(realpath $DIR_SCRIPT/../..)
DIR_MS=$(realpath $DIR_SERVICE/..)
DIR_TEST=`ls -d $DIR_SERVICE/*-test-pact`
DIR_PROVIDER_STATE=`ls -d $DIR_SERVICE/*-test-providerstate/approved`

#===================================================================
# Service specific setting. Currently there is only one
# COUCHBASE_PROVIDER, Some service uses a different default.
#===================================================================
COUCHBASE_PROVIDER=cbProvider

#===================================================================
# SERVICE_NAME_XXX, parsed from DIR_SERVICE
#===================================================================
SERVICE_NAME=$(basename $DIR_SERVICE)
SERVICE_NAME=${SERVICE_NAME%-ms} # shopping-cart-ms => shopping-cart
#  shopping-cart => ShoppingCart
SERVICE_NAME_CAMELCASE=$(echo $SERVICE_NAME | sed -r 's/(^|-)([a-z])/\U\2/g')

#===================================================================
# JAVA_OPTS_XXX, Java options for launching service/provider state.
#===================================================================
# For CI the clusterUrl can be found only after deploying couchbase in K8S
JAVA_OPTS_CI+=-Dcouchbase.providers[0].env.bucketName=default

JAVA_OPTS_LOCAL=-Dcouchbase.providers[0].env.clusterUrl=${USER}01 
JAVA_OPTS_LOCAL+=(-Dcouchbase.providers[0].env.bucketName=test)

JAVA_OPTS_COMMON+=(-Dcouchbase.providers[0].name=$COUCHBASE_PROVIDER)
JAVA_OPTS_COMMON+=(-Dlogging.level.org.springframework.web=TRACE)

JAVA_OPTS_SERVICE=-Dloader.main=com.amdocs.msbase.app.Application

JAVA_OPTS_PROVIDER_STATE=-Dloader.main=com.amdocs.msbase.testing.pact.providerstate.couchbase.Application
JAVA_OPTS_PROVIDER_STATE+=(-Dserver.port=8078)
JAVA_OPTS_PROVIDER_STATE+=(-Dsecurity.basic.enabled=false)

#===================================================================
# PACTS, An array of pact files to create mocks for.
#===================================================================
PACTS=(`ls ${DIR_TEST}/target/pacts`)

#===================================================================
# JAVA_OPTS_MOCK, An array of Java options to tell the service
# how to connect to the mocks as external services.
#===================================================================
for pact in ${PACTS[@]}; do
	provider=${pact##*-} # Checkout-ShoppingCart.json => ShoppingCart.json 
	provider=${provider%%.*} # ShoppingCart.json => ShoppingCart 
	# ${provider,} => shoppingCart, ${provider,,}=>shoppingcart
	JAVA_OPTS_MOCK+=(-Dservices.${provider,}.url=http://localhost:__mockPort__/${provider,,}-management)
	MOCK_SERVICES_CI+=(${provider,,}-${provider,,}-service)
done

#===================================================================
# MSNEXT_XXX, Create MSNext environments for the service/provider state
# MSNEXT setup is subject o change! Stay tuned
#===================================================================
DIR_MSNEXT="$DIR_TEST/target/msnext"
DIR_MSNEXT_RUNTIMES="$DIR_MSNEXT/runtimes"
rm -rf "$DIR_MSNEXT_RUNTIMES"
mkdir "$DIR_MSNEXT_RUNTIMES"
cd "$DIR_MSNEXT_RUNTIMES"
MSNEXT_DISTRO=`ls -d ${DIR_MSNEXT}/msnext-*`

MSNEXT_SERVICE="$DIR_MSNEXT_RUNTIMES/$(basename $DIR_SERVICE)"
cp -r $MSNEXT_DISTRO $MSNEXT_SERVICE

#===================================================================
# URL_XXX, URLs of various external servers
#===================================================================
#URL_PACT_BROKER=http://${USER}01:31000
#URL_PACT_MOCK_SERVER=http://${USER}01:9000

#===================================================================
# KAFKA_XXX info passed to kafka
#===================================================================
KAFKA_TOPIC=${SERVICE_NAME_CAMELCASE}_Resources
KAFKA_PROPERTIES_FILE=$DIR_SCRIPT/kafka.properties
KAFKA_APPROVED_EVENTS_FILE=$DIR_TEST/approvedEvents/allPacts.out
KAFKA_NEW_EVENTS_FILE=$DIR_TEST/newEvents/allPacts.out

set +x

# Deprecated
#===================================================================
# DOCKER_XXX settings
#===================================================================
#if [[ $(uname -o) == Cygwin ]] ; then
#    DOCKER_CERT_PATH=C:/Users/$USER/.minikube/certs
#    DOCKER_HOST=tcp://${USER}01:2376
#    DOCKER_TLS_VERIFY=1
#fi
#BUILD_SERVICE=false

