package org.simpleflatmapper.csv.bug;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.map.property.RenameProperty;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.reflect.Setter;

import java.io.IOException;

public class Issue364 {

    public static String DATA = "Benchmark,Score\n" +
            "MyAlgo.forFile,0.23423\n" +
            "MyAlgo.forString,0.13423\n" +
            "OtherAlgo.forFile,0.34233\n" +
            "OtherAlgo.forString,0.14423";


    public static class Data {
        public String algorithm;
        public String type;
        public double score;


        public void setBenchmark(String value) {
                int indexOfDot = value.indexOf('.');
                algorithm = value.substring(0, indexOfDot);
                type = value.substring(indexOfDot + 1, value.length());

        }
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

//        CsvMapper<Data> mapper = CsvMapperFactory
//                .newInstance()
//                .addColumnProperty("Benchmark", new Setter<Data, String>() {
//                    @Override
//                    public void set(Data target, String value) throws Exception {
//                        int indexOfDot = value.indexOf('.');
//                        target.algorithm = value.substring(0, indexOfDot);
//                        target.type = value.substring(indexOfDot + 1, value.length());
//                    }
//                })
//                .newMapper(Data.class);


        Setter<Data, String> setter = (target, value)  -> {
                int indexOfDot = value.indexOf('.');
                target.algorithm = value.substring(0, indexOfDot);
                target.type = value.substring(indexOfDot + 1, value.length());
            };

        CsvMapper<Data> mapper = CsvMapperFactory
                .newInstance()
//                .addColumnProperty("Benchmark", new SetterProperty(setter))
                .newMapper(Data.class);
        CsvParser.mapWith(mapper).stream(DATA).forEach(System.out::println);
    }
}
