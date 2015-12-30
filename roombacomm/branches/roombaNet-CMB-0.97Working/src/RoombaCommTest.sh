#!/bin/sh


java -Djava.library.path=rxtxlib -classpath .:rxtxlib/RXTXcomm.jar roombacomm.RoombaCommTest
