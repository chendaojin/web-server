@echo off
set jh=%JAVA_HOME%

if "%jh%"=="" (
echo Î´¼ì²éµ½JAVA_HOME
pause
) else echo USING JAVA_HOME: %jh%

REM javac -encoding UTF8 -classpath lib\log4j-1.2.17.jar;lib\dom4j-1.6.1.jar -sourcepath src src\server\Main.java -d bin

start javaw -classpath .\bin;.\lib\log4j-1.2.17.jar;.\lib\dom4j-1.6.1.jar server.Main GUI