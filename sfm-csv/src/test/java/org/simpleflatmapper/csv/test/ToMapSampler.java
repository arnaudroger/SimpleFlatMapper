package org.simpleflatmapper.csv.test;

import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.test.beans.Foo;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.TypeReference;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by aroger on 11/07/2017.
 */
public class ToMapSampler {
    public static void main(String[] args) throws IOException {
        Map<String, Foo> collect = CsvParser.mapTo(new TypeReference<Tuple2<String, Foo>>() {
        }).stream("name,foo\na,b\nc,d\ne,f").collect(Collectors.toMap(Tuple2::first, Tuple2::second));

        System.out.println("collect = " + collect);

    }
}
