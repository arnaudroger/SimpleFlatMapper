package org.sfm.map.impl.getter.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.sfm.csv.CsvParser;

import java.io.StringReader;

/**
 * Created by aroger on 11/12/2015.
 */
public class CsvActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        CsvParser.mapTo(TestClass.class).stream(new StringReader("id,name\n1,name1\n2,name2")).forEach(System.out::println);
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }


    public static class TestClass {
        private final String name;
        private final int id;


        public TestClass(String name, int id) {
            this.name = name;
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "TestClass{" +
                    "name='" + name + '\'' +
                    ", id=" + id +
                    '}';
        }
    }

}
