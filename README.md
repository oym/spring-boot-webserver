# spring-boot-webserver
Static web server using spring-boot

### Build webserver
	mvn clean install spring-boot:repackage

### Run webserver
	java -jar webserver-1.0.0-SNAPSHOT.jar [-r webroot] [-p serverport]

The default _webroot_ is the current directory  

The default _serverport_ is 3000

