#!/usr/bin/env groovy

node("${env.JENKINS_NODE}") {

    try {
        stage('Clone sources')
                { checkout scm }

        load "cfg/pipeline.groovy"

        sendNotification 'STARTED'

        stage('Build and unit tests ')
                { sh "mvn -s ${mvnCfg} -P test-docker-host clean install -Pjacoco" }

        stage('Docker Build')
                { sh "mvn ${mvnParams} -pl shoppingcart-checkout-exe,shoppingcart-checkout-test-pact -P test-docker-host fabric8:build" }

        stage('Upload image to test docker registry')
                { sh "mvn ${mvnParams} -pl shoppingcart-checkout-exe,shoppingcart-checkout-test-pact -P test-docker-host fabric8:push" }

                
	stage('Deploy MS in Test NameSpace')
           { 
                k8s_namespace('CREATE', "${env.k8s_test_namespace}")
                sh "export KUBECONFIG='/etc/kubernetes/kubeconfig'; bash cfg/podDeploy.sh -n ${env.k8s_test_namespace} -s 150 -f *exe/target/classes/META-INF/fabric8/kubernetes.yml"
                k8s_namespace('DELETE', "${env.k8s_test_namespace}") 
           }

        stage('Component tests') {
            k8s_namespace('CREATE', "${env.k8s_test_namespace}")
            sh "mvn ${mvnParams} test -P test-docker-host -pl shoppingcart-checkout-exe,shoppingcart-checkout-test-pact ${env.BuildUnitTestParams}"
            k8s_namespace('DELETE', "${env.k8s_test_namespace}")
        }

        stage('Sonar')
                { sh "mvn ${mvnParams} sonar:sonar" }

        if (params.BRANCH_NAME == "master") {
            stage ('Deploy to Nexus') {
				sh "mvn ${mvnParams} deploy"
			}

/*			stage('Deployment Yaml & Helm') {
                parallel(
                        resource: {
                            sh "mvn ${mvnParams} -pl shoppingcart-checkout-exe,shoppingcart-checkout-test-pact fabric8:resource -Djacoco.enabled=false -Djacoco.port=0"
                        },
                        helm: { sh "mvn ${mvnParams} -pl shoppingcart-checkout-exe,shoppingcart-checkout-test-pact fabric8:helm" }
                )
            }
*/
            stage('Upload image to production docker registry') {
                sh "echo 'Upload image to production docker registry'"
                sh "mvn ${mvnParams} -pl shoppingcart-checkout-exe,shoppingcart-checkout-test-pact fabric8:build fabric8:push"
            }
/*
            stage('Confirm Production Deployment')
                    { input 'Approve?' }

            stage('Update Production') {
                parallel(
                        "Deploy to Open source Kubernetes": {
                            withEnv(["KUBECONFIG=${env.DeploymentKubernetesFile}"]) {
                                sh "mvn ${mvnParams} -pl shoppingcart-checkout-exe,shoppingcart-checkout-test-pact fabric8:apply -Dfabric8.rolling=true"
                            }
                        }
                )
            }
             */
        }
        currentBuild.result = 'SUCCESS'
        sendNotification 'SUCCESSFUL'
	    currentBuild.description = "${USER}_${BRANCH_NAME}@${NODE_NAME}"
    }
    catch (err) {
        k8s_namespace('DELETE', "${env.k8s_test_namespace}")
        currentBuild.result = 'FAILED'
        sendNotification 'FAILED'
	    currentBuild.description = "${USER}_${BRANCH_NAME}@${NODE_NAME}"
        throw err
    }
}
