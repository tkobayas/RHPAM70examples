1. Start RHPAM

2. Build projects and deploy REST application

$ cd simple-pojo
$ mvn clean install
$ cd ../helloworld-rs-pojo
$ mvn clean install
$ cp target/helloworld-rs-pojo-1.0.0.war $JBOSS_HOME/standalone/deployments

3. Unzip helloREST-git-repo.zip and import it via "Import project" in business-central

4. Deploy the project and start a process "helloREST"

