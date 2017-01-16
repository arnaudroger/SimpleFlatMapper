package org.simpleflatmapper.csv.test.bug;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.IOException;

public class Issue365Test {

    public static String DATA = "Benchmark,Score\n" +
            "MyAlgo.forFile,0.23423\n" +
            "MyAlgo.forString,0.13423\n" +
            "OtherAlgo.forFile,0.34233\n" +
            "OtherAlgo.forString,0.14423";


    public static class Data {
        public String algorithm;
        public String type;
        public double score;

        @Override
        public String toString() {
            return "Data{" +
                    "algorithm='" + algorithm + '\'' +
                    ", type='" + type + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

    @Test
    public void testParse() throws IOException {

        CsvMapper<Data> mapper = CsvMapperFactory
                .newInstance()
                .addColumnProperty("Benchmark", new Setter<Data, String>() {
                    @Override
                    public void set(Data target, String value) throws Exception {
                        int indexOfDot = value.indexOf('.');
                        target.algorithm = value.substring(0, indexOfDot);
                        target.type = value.substring(indexOfDot + 1, value.length());
                    }
                })
                .newMapper(Data.class);


        CsvParser.mapWith(mapper).forEach(DATA, new CheckedConsumer<Data>() {
            @Override
            public void accept(Data data) throws Exception {
                System.out.println("data = " + data);
            }
        });
    }
}
