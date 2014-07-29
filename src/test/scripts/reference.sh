#!/bin/sh
java -Xms1g -cp target/classes:target/test-classes/:$HOME/.m2/repository/org/hsqldb/hsqldb/2.3.2/hsqldb-2.3.2.jar:$HOME/.m2/repository/junit/junit/4.11/junit-4.11.jar org.sfm.benchmark.ReferenceBenchmark
