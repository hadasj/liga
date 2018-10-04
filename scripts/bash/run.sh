#!/bin/bash

VERZE=1.1-SNAPSHOT

cd $HOME/workspace/liga

java -cp "derby.jar:target/reaktor-$VERZE.jar" cz.i.ping.pong.liga.Commander
