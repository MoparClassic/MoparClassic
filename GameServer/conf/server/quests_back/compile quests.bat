@echo off
set JAVA_HOME=C:\Program Files\Java\jdk1.6.0_03\bin
del *.class
javac -classpath .;../../../rscd.jar *.java
pause