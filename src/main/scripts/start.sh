#!/bin/bash
kill -9 $(ps aux | grep aws-sar | grep -v grep | awk '{ print $2 }')
java -jar aws-sar-java-0.0.1-SNAPSHOT.jar 1> /home/ubuntu/sar-java-logs/1.log 2> /home/ubuntu/sar-java-logs/2.log &
