:: pick one
set JAVAC=javac
:: JAVAC=/System/Library/Frameworks/JavaVM.framework/Versions/1.4.2/Commands/javac
:: #JAVAC=/System/Library/Frameworks/JavaVM.framework/Versions/1.5/Commands/javac


REM %JAVAC% -classpath .;rxtxlib/RXTXcomm.jar roombacomm/*.java 
%JAVAC% -Xlint:unchecked  -classpath .;rxtxlib/RXTXcomm.jar roombacomm/*.java
