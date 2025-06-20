#!/bin/sh
  
set -x

cd src
javac -Xlint -d ../bin mqprot/*.java

test $? -eq 0 || exit 1

cd ../bin

jar cf ../common.jar mqprot/*.class

