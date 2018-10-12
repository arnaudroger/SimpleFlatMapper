#!/bin/bash

MVN_OPTS=-Xss200k
git reset --hard && \
java11 && mvn clean install -Pdev9 && \
java10 && mvn clean install -Pdev9 && \
java9 && mvn clean install -Pdev9 && \
java8 && mvn clean install -Pdev && \
java7  && mvn clean install -Dhttps.protocols=TLSv1.2
  
java8
git reset --hard 


