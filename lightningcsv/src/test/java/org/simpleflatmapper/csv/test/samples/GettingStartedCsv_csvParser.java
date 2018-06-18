package org.simpleflatmapper.csv.test.samples;

import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.util.CloseableIterator;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Stream;

public class GettingStartedCsv_csvParser {
    public static void main(String[] args) throws Exception {
        File file = new File(GettingStartedCsv_csvParser.class.getClassLoader().getResource("samples.csv").getFile());

        // Callback
        CsvParser
                .forEach(file, row -> System.out.println(Arrays.toString(row)));

        // Iterator
        try (CloseableIterator<String[]> it = CsvParser.iterator(file)) {
            while(it.hasNext()) {
                System.out.println(Arrays.toString(it.next()));
            }
        }

        // Stream
        try (Stream<String[]> stream = CsvParser.stream(file)) {
            stream.forEach(row -> System.out.println(Arrays.toString(row)));
        }
    }
}
