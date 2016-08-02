package org.simpleflatmapper.osgi;

import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.simpleflatmapper.core.map.Mapper;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.poi.SheetMapper;

import java.io.*;

public class OsgiTest {


    @Test
    public void testCsvParser() throws BundleException, InterruptedException, IOException {
        HostApplication hostApplication = new HostApplication();
        try {


            loadClassVisitor(hostApplication);
            hostApplication.deployBundleWithClass(Mapper.class);
            hostApplication.deployBundleWithClass(CsvParser.class);

            InputStream is =
                    TinyBundles.bundle().add(CsvActivator.class)
                            .add(TestClass.class)
                            .set(Constants.BUNDLE_ACTIVATOR, CsvActivator.class.getName())
                            .set(Constants.IMPORT_PACKAGE, "org.osgi.framework,org.simpleflatmapper.csv" )
                            .build();
            Bundle bundle = hostApplication.install("tinyBundle", is);

            bundle.start();


        } finally {
            hostApplication.shutdownApplication();
        }
    }


    @Test
    public void testPoiParser() throws BundleException, InterruptedException, IOException {

        HostApplication hostApplication = new HostApplication();
        try {


            loadClassVisitor(hostApplication);
            hostApplication.deployWrapBundleWithClass(Cell.class);
            hostApplication.deployBundleWithClass(Mapper.class);
            hostApplication.deployBundleWithClass(CsvParser.class);
            hostApplication.deployBundleWithClass(SheetMapper.class);

            InputStream is =
                    TinyBundles.bundle().add(PoiActivator.class)
                            .add(TestClass.class)
                            .set(Constants.BUNDLE_ACTIVATOR, PoiActivator.class.getName())
                            .set(Constants.IMPORT_PACKAGE, "org.osgi.framework,org.simpleflatmapper.poi,org.apache.poi.ss.usermodel,org.apache.poi.hssf.usermodel" )
                            .build();
            Bundle bundle = hostApplication.install("tinyBundle", is);

            bundle.start();


        } finally {
            hostApplication.shutdownApplication();
        }
    }

    private void loadClassVisitor(HostApplication hostApplication) throws BundleException, IOException {
        if (!System.getProperty("java.version").contains("9-ea")) {
            hostApplication.deployBundleWithClass(ClassVisitor.class);
        }
    }

}
