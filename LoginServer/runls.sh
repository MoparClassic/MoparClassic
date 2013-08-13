#!/bin/bash
libs=$(find lib/ -type f -print0 | sed "s/\x0/:/g")
#libs="lib/slf4j.jar:lib/hex-string.jar:lib/mysql-connector.jar:lib/xstream.jar:lib/xpp3.jar:lib/mina.jar:"
java -cp "$libs./ls.jar" org.moparscape.msc.ls.Server
