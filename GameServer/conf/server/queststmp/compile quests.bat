@echo off
del *.class
javac -classpath .;../../../rscd.jar *.java
pause