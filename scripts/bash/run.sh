#!/bin/bash

VERZE=0.1-SNAPSHOT

cd $HOME/workspace/liga

java -cp "derby.jar:target/reaktor-$VERZE.jar" cz.i.ping.pong.liga.Commander