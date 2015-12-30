REM javadoc -d html -classpath $MYCP ../rxtx/src/*.java -exclude gnu.io roombacomm
javadoc -d html -classpath .:./rxtxlib/RXTXcomm.jar roombacomm/*.java
