### kie-server-embedded-app-example

1. Setup standard RHPAM 7.0.1 installation which has business-central.war and kie-server.war (e.g. use installer)
2. Remove kie-server.war
3. Edit standalone-full.xml to point at "http://localhost:8080/kie-server-embedded-app/services/rest/server". So system properties would be like this:

~~~
        <property name="org.kie.server.user" value="controllerUser"/>
        <property name="org.kie.server.pwd" value="${VAULT::vaulted::controller.password::1}"/>
        <property name="org.kie.server.location" value="http://localhost:8080/kie-server-embedded-app/services/rest/server"/>
        <property name="org.kie.server.controller" value="http://localhost:8080/business-central/rest/controller"/>
        <property name="org.kie.server.controller.user" value="controllerUser"/>
        <property name="org.kie.server.controller.pwd" value="${VAULT::vaulted::controller.password::1}"/>
        <property name="org.kie.server.id" value="default-kieserver"/>
~~~

4. Start RHPAM
5. Import project "simple-proj1" via Business Central ("simple-proj1" repo exists inside this zip)
6. Build application

 $ cd kie-server-embedded-app
 $ mvn clean install

7. Deploy application

 $ cp target/kie-server-embedded-app-7.0.0-SNAPSHOT.war $JBOSS_HOME/standalone/deployments/

8. Deploy the project by "Deploy" button in Business Central
9. Run the application via http://localhost:8080/kie-server-embedded-app/
 - "Start Reward Process" -> [Start a process]
 - "John's Task" -> "Approve"
 - "Mary's Task" -> "Approve"
10. You can view the alive process instances via "Process Instances" in Business Central

