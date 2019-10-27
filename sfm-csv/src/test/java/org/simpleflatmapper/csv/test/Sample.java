package org.simpleflatmapper.csv.test;

import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.lightningcsv.CsvReader;

import java.io.IOException;

public class Sample {

    private static final CsvMapper<MyObject> mapper =
            CsvMapperFactory
                    .newInstance()
                    .newBuilder(MyObject.class)
                    .addMapping("col1").addMapping("col2").mapper();

    public static void main(String[] args) throws IOException {
        CsvReader reader = CsvParser.dsl().reader("val1,val2");
        MyObject next = mapper.iterator(reader).next();
        System.out.println("next = " + next);
    }


    public static class MyObject {
        public final String col1;
        public final String col2;

        public MyObject(String col1, String col2) {
            this.col1 = col1;
            this.col2 = col2;
        }

        @Override
        public String toString() {
            return "MyObject{" +
                    "col1='" + col1 + '\'' +
                    ", col2='" + col2 + '\'' +
                    '}';
        }
    }
}
