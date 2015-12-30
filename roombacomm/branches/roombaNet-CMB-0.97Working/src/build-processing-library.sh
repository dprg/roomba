#!/bin/sh
#
# Builds the distribution zip file that contains a properly formatted 
# Processing library.  Unzip the resulting file in the 'libraries' directory
# in Processing to install.
#
# Before running this script, run 'build-jar.sh'
#

pushd packaging
rm -rf processing
mkdir -p                  processing/roombacomm/library
cp ../roombacomm.jar      processing/roombacomm/library/roombacomm.jar
cp processing-export.txt  processing/roombacomm/library/export.txt
cp ../rxtxlib/*           processing/roombacomm/library
cp -r ../processing-examples processing/roombacomm/examples
cp ../README              processing/roombacomm/README
cp processing-readme.txt  processing/roombacomm/README-processing.txt
pushd processing
zip -r roombacomm-processing.zip roombacomm
cp roombacomm-processing.zip ../..
popd
popd
