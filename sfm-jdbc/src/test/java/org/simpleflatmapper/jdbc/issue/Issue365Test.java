package org.simpleflatmapper.jdbc.issue;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.reflect.Setter;

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

//
//        public void setBenchmark(String value) {
//                int indexOfDot = value.indexOf('.');
//                algorithm = value.substring(0, indexOfDot);
//                type = value.substring(indexOfDot + 1, value.length());
//
//        }
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

        JdbcMapper<Data> mapper = JdbcMapperFactory
                .newInstance()
                .addColumnProperty("Benchmark", new Setter<Data, String>() {
                    @Override
                    public void set(Data target, String value) throws Exception {
                        int indexOfDot = value.indexOf('.');
                        target.algorithm = value.substring(0, indexOfDot);
                        target.type = value.substring(indexOfDot + 1, value.length());
                    }
                })
                .newBuilder(Data.class).addMapping("benchmark").addMapping("score").mapper();


    }
}
