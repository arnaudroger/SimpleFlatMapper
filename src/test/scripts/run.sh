#!/bin/bash

fn_exists()
{
    type $1 | grep -q 'shell function'
}

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

BENCHMARK=$DIR/../../../src/test/benchmarks
SCRIPTS=$DIR/../../../src/test/scripts

fn_exists cpustable && cpustable
$SCRIPTS/all.sh mock 1,10,100,1000 1000000 > $BENCHMARK/data/mock.csv
$SCRIPTS/all.sh hsqldb 1,10,100,1000 500000 > $BENCHMARK/data/hsqldb.csv
$SCRIPTS/all.sh mysql 1,10,100,1000 100000 > $BENCHMARK/data/mysql.csv
fn_exists cpufull && cpufull



