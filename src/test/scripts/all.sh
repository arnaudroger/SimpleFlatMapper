#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
echo $DIR
ROOT=$DIR/../../..

OUTDIR=$ROOT/target/perf/benchmarks
echo $OUTDIR
mkdir -p $OUTDIR
$DIR/dynamicJdbcMapperForEach.sh > $OUTDIR/dynamicJdbcMapperForEach.txt
$DIR/dynamicJdbcMapperMap.sh > $OUTDIR/dynamicJdbcMapperMap.txt
$DIR/hibernateStatefull.sh > $OUTDIR/hibernateStatefull.txt
$DIR/hibernateStatefullWithCache.sh > $OUTDIR/hibernateStatefullWithCache.txt
$DIR/hibernateStateless.sh > $OUTDIR/hibernateStateless.txt
$DIR/mybatis.sh > $OUTDIR/mybatis.txt
$DIR/reference.sh > $OUTDIR/reference.txt
$DIR/staticJdbcMapperMap.sh > $OUTDIR/staticJdbcMapperMap.txt
