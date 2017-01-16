package org.simpleflatmapper.csv.test.samples;

import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.util.CloseableIterator;

import java.io.File;
import java.util.stream.Stream;

public class GettingStartedCsv_csvMapper {
    public static void main(String[] args) throws Exception {
        File file = new File(GettingStartedCsv_csvMapper.class.getClassLoader().getResource("samples.csv").getFile());

        // Callback
        CsvParser
                .mapTo(MyObject.class)
                .forEach(file, System.out::println);

        // Iterator
        try (CloseableIterator<MyObject> it =
                     CsvParser.mapTo(MyObject.class).iterator(file)) {
            while(it.hasNext()) {
                System.out.println(it.next());
            }
        }

        // Stream
        try (Stream<MyObject> stream =
                     CsvParser.mapTo(MyObject.class).stream(file)) {
            stream.forEach(System.out::println);
        }

        // override headers
        CsvParser
                .skip(1)
                .mapTo(MyObject.class)
                .headers("id", "email", "name")
                .forEach(file, System.out::println);
    }

    public static class MyObject {
        private final String id;
        private final String name;
        private final String email;

        public MyObject(String id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        @Override
        public String toString() {
            return "MyObject{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    '}';
        }
    }
}
