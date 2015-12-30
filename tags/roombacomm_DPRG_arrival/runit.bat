REM run simple serial test .bat file

java -Djava.library.path=rxtxlib -classpath .;rxtxlib/RXTXcomm.jar;rxtxlib/bluecove-2.1.0.jar %*
