export CONF_API="http://localhost:4569"
export ENCRYPTION_KEY="foobar"
mvn clean install exec:java -Dexec.mainClass=com.mycompany.App