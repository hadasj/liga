#!/bin/bash

VERZE=1.4-SNAPSHOT
DB=$HOME/Documents/pp-test
WORKDIR=$HOME/Documents/test
JAVA11=$JAVA11_HOME/bin/java

cd $HOME/workspace/liga

$JAVA11 -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005 -cp "derby.jar:target/reaktor-$VERZE.jar" cz.i.ping.pong.liga.Commander $DB $WORKDIR
