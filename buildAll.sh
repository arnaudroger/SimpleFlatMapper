#!/bin/bash
git reset --hard && java8 && mvn clean install  && java7  && mvn clean install && java6 && mvn clean install && git reset --hard 

