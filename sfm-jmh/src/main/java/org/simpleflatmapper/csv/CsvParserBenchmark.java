package org.simpleflatmapper.csv;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.simpleflatmapper.lightningcsv.parser.CellConsumer;

import java.io.IOException;

@State(Scope.Benchmark)
public class CsvParserBenchmark {

    /**
     Benchmark                      Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse       avgt   20  479.269 ± 1.984  ns/op
     CsvParserBenchmark.parseQuote  avgt   20  951.399 ± 5.064  ns/op
     CsvParserBenchmark.parseTrim   avgt   20  756.353 ± 2.033  ns/op
     Benchmark                      Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse       avgt   20  469.553 ± 1.793  ns/op
     CsvParserBenchmark.parseQuote  avgt   20  996.914 ± 3.604  ns/op
     CsvParserBenchmark.parseTrim   avgt   20  736.330 ± 2.716  ns/op

     Benchmark                      Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse       avgt   20  453.102 ± 1.281  ns/op
     CsvParserBenchmark.parseQuote  avgt   20  856.279 ± 3.323  ns/op
     CsvParserBenchmark.parseTrim   avgt   20  775.451 ± 2.041  ns/op


     Benchmark                      Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse       avgt   20  428.510 ± 1.684  ns/op
     CsvParserBenchmark.parseQuote  avgt   20  866.024 ± 2.639  ns/op
     CsvParserBenchmark.parseTrim   avgt   20  778.768 ± 1.860  ns/op


    Mac 2.9.4
     Benchmark                       Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse        avgt   20  212.758 ± 2.900  ns/op
     CsvParserBenchmark.parseQuote   avgt   20  289.970 ± 4.882  ns/op
     CsvParserBenchmark.parseQuote2  avgt   20  359.580 ± 4.132  ns/op
     CsvParserBenchmark.parseTrim    avgt   20  263.594 ± 5.287  ns/op


     Mac 2.9.5
     Benchmark                       Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse        avgt   20  184.681 ± 4.202  ns/op
     CsvParserBenchmark.parseQuote   avgt   20  317.010 ± 2.848  ns/op
     CsvParserBenchmark.parseQuote2  avgt   20  385.423 ± 8.884  ns/op
     CsvParserBenchmark.parseTrim    avgt   20  275.656 ± 1.510  ns/op

     Perf branch
     Benchmark                       Mode  Cnt    Score   Error  Units
     CsvParserBenchmark.parse        avgt   20  168.031 ± 2.957  ns/op
     CsvParserBenchmark.parseQuote   avgt   20  286.045 ± 5.008  ns/op
     CsvParserBenchmark.parseQuote2  avgt   20  337.570 ± 5.398  ns/op
     CsvParserBenchmark.parseTrim    avgt   20  256.215 ± 1.940  ns/op


     */
    public String csv = "val,val2  sdssddsds,lllll llll,sdkokokokokads<>Sddsdsds, adsdsadsad ,1, 3 ,4";
    public String csvPipe = "val|val2  sdssddsds|lllll llll|sdkokokokokads<>Sddsdsds| adsdsadsad |1| 3 |4";
    public String csvQuote = "\"val\",\"val2  sdssddsds\",\"lllll llll\",\"sdkokokokokads<>Sddsdsds\",\"adsdsadsad\",\"1\",\"3\",\"4\"";
    public String csvPipeQuote = "\"val\"|\"val2  sdssddsds\"|\"lllll llll\"|\"sdkokokokokads<>Sddsdsds\"|\"adsdsadsad\"|\"1\"|\"3\"|\"4\"";
    public String csvQuote2 = "\"val \"\" \",\"val2  \"\"sdssddsds\",\"lllll llll\",\"sdkokokokokads<>Sddsdsds\",\"adsdsadsad\",\"1\",\"3\",\"4\"";


    public static final CsvParser.DSL dsl = CsvParser.dsl();
    public static final CsvParser.DSL dslPipe = CsvParser.dsl().separator('|');

    public static final CsvParser.DSL tdsl = CsvParser.dsl().trimSpaces();

    @Benchmark
    public void parse(Blackhole blackhole) throws IOException {
        dsl.parse(csv, new MyCellConsumer(blackhole));
    }

    @Benchmark
    public void parsePipe(Blackhole blackhole) throws IOException {
        dslPipe.parse(csvPipe, new MyCellConsumer(blackhole));
    }

    @Benchmark
    public void parseTrim(Blackhole blackhole) throws IOException {
        tdsl.parse(csv, new MyCellConsumer(blackhole));
    }

    @Benchmark
    public void parseQuote(Blackhole blackhole) throws IOException {
        dsl.parse(csvQuote, new MyCellConsumer(blackhole));
    }
    @Benchmark
    public void parsePipeQuote(Blackhole blackhole) throws IOException {
        dslPipe.parse(csvPipeQuote, new MyCellConsumer(blackhole));
    }

    public void parseQuote2(Blackhole blackhole) throws IOException {
        dsl.parse(csvQuote2, new MyCellConsumer(blackhole));
    }
    public static void main(String[] args) throws IOException {
        new CsvParserBenchmark().parseQuote(null);
    }


    private static class MyCellConsumer implements CellConsumer {
        private final Blackhole blackhole;

        public MyCellConsumer(Blackhole blackhole) {
            this.blackhole= blackhole;
        }

        @Override
        public void newCell(char[] chars, int offset, int length) {
            if (blackhole != null) {
                blackhole.consume(String.valueOf(chars, offset, length));
            }
        }

        @Override
        public boolean endOfRow() {
            return true;
        }

        @Override
        public void end() {

        }
    }
}
