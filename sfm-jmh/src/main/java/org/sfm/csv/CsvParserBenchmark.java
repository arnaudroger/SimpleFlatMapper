package org.sfm.csv;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.sfm.csv.parser.CellConsumer;

import java.io.IOException;
import java.util.List;

@State(Scope.Benchmark)
public class CsvParserBenchmark {

    /**
     * Benchmark                     Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse      avgt   20  173.738 ± 2.790  ns/op
     CsvParserBenchmark.parseTrim  avgt   20  241.522 ± 5.823  ns/op

     Benchmark                     Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse      avgt   20  163.853 ± 0.758  ns/op
     CsvParserBenchmark.parseTrim  avgt   20  239.365 ± 1.015  ns/op


     Benchmark                     Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse      avgt   20  290.077 ± 2.920  ns/op
     CsvParserBenchmark.parseTrim  avgt   20  360.767 ± 1.679  ns/op


     Benchmark                     Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse      avgt   20  293.699 ± 1.892  ns/op
     CsvParserBenchmark.parseTrim  avgt   20  361.417 ± 1.967  ns/op


     */
    public String csv = "val,val2  sdssddsds,lllll llll,sdkokokokokads<>Sddsdsds,adsdsadsad,1,3,4\r\nsddsds,sdds,dsds,sd,ds,dssds";


    public static final CsvParser.DSL dsl = CsvParser.dsl();

    public static final CsvParser.DSL tdsl = CsvParser.dsl().trimSpaces();

    @Benchmark
    public void parse(Blackhole blackhole) throws IOException {
        dsl.parse(csv, new MyCellConsumer(blackhole));
    }

    @Benchmark
    public void parseTrim(Blackhole blackhole) throws IOException {
        tdsl.parse(csv, new MyCellConsumer(blackhole));
    }

    private static class MyCellConsumer implements CellConsumer {
        private final Blackhole blackhole;

        public MyCellConsumer(Blackhole blackhole) {
            this.blackhole= blackhole;
        }

        @Override
        public void newCell(char[] chars, int offset, int length) {
            blackhole.consume(chars);
            blackhole.consume(offset);
            blackhole.consume(length);
        }

        @Override
        public void endOfRow() {

        }

        @Override
        public void end() {

        }
    }
}
