// MicroServvice Specific params
env.microserviceName = "shoppingcart-checkout"
env.unitTestParams = "-Dtest=KubernetesIntegrationKT"
env.EmailList = 'someone@amdocs.com'

// Defaults
env.bitbucketNotification = 'true'
env.hipchatNotification = 'false'
env.emailNotification = 'true'
env.mvnCfg = "cfg/maven/ci/settings.xml"
env.mvnHome = tool 'maven3.3.9'
env.mvnParams = "-gs ${mvnCfg} -Ddocker.image.release=${env.BUILD_NUMBER}"
env.KUBECONFIG = '/etc/kubernetes/kubeconfig'
env.kubernetesHome = '/usr/bin/'
env.k8s_test_namespace = "${microserviceName}-test-tmpns"
env.PATH = "${mvnHome}/bin:${kubernetesHome}:${env.PATH}:/usr/local/bin"
env.BuildUnitTestParams = "${unitTestParams} -Pjacocoremote -Djacoco.enabled=true -DfailIfNoTests=false -Dkubernetes.host=${env.KUBERNETES_HOST} -Dnamespace.use.existing=${k8s_test_namespace} -Dnamespace.cleanup.enabled=false"
env.DeploymentKubernetesFile = "deployments/kubernetes/kubelet_ilvpbg723.conf"