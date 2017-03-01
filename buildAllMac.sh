#!/bin/bash

MVN_OPTS=-Xss200k
git reset --hard && java9 && mvn clean install -Pdev9 && java8 && mvn clean install -Pdev && java7  && mvn clean install && java6 && mvn clean install  
git reset --hard 
java8

