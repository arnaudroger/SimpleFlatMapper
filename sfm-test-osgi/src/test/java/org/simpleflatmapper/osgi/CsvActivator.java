package org.simpleflatmapper.osgi;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.simpleflatmapper.csv.CsvParser;

import java.io.StringReader;

public class CsvActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        CsvParser.mapTo(TestClass.class).stream(new StringReader("id,name\n1,name1\n2,name2")).forEach(System.out::println);
    }

    @Override
    public void stop(BundleContext context) throws Exception {

    }


}
