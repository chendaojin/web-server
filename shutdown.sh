#! /bin/bash

# javac -encoding UTF8 -cp lib/log4j-1.2.17.jar:lib/dom4j-1.6.1.jar -sourcepath src src/exit/RequestExit.java -d bin

java -classpath ./bin:./lib/log4j-1.2.17.jar:./lib/dom4j-1.6.1.jar exit.RequestExit
