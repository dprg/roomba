#!/bin/sh
#
# Builds the jar file for the core RoombaComm classes
# This core is used for all direct-connect (serial,etc) cases
# 
# Before running this script, compile all the .java files to .class files
# and "cd build"
#
#

base=`dirname "$0"`

echo "buidling roombacomm.jar..."
jar -cfm roombacomm.jar packaging/Manifest.txt roombacomm
echo "done"
