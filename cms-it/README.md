

== start/stop the wildfly server

mvn clean wildfly:start@start-server wildfly:add-resource@add-datasource wildfly:deploy-artifact@deploy-war 

Now you can run the RootIT unit tests.
CMS website available at http://localhost:8081
WildFly mgmt available at http://localhost:9991

mvn wildfly:shutdown@shutdown-server
