# H2 Database Web Frontend

This WAR makes the database managment frontend bundled with H2 drivers
accessible. The H2 database comes with WildFly 11. This module has no
dependency on H2 and needs H2 being provided by the JavaEE instance or
whatever servlet container provider is running it. You could remove
the provided scope from the H2 dependency in pom.xml and thereby
package H2 with this WAR if you intend to use the frontend against
another database.

The H2 frontend can be used to access all JDBC-type databases if their
driver is on the classpath.
