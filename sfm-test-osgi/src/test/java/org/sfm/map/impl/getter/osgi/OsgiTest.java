package org.sfm.map.impl.getter.osgi;

import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.sfm.csv.CsvParser;
import org.sfm.poi.SheetMapper;

import java.io.*;

public class OsgiTest {


    @Test
    public void testCsvParser() throws BundleException, InterruptedException, IOException {
        HostApplication hostApplication = new HostApplication();
        try {


            hostApplication.deployBundleWithClass(ClassVisitor.class);
            hostApplication.deployBundleWithClass(CsvParser.class);

            InputStream is =
                    TinyBundles.bundle().add(CsvActivator.class)
                            .add(TestClass.class)
                            .set(Constants.BUNDLE_ACTIVATOR, CsvActivator.class.getName())
                            .set(Constants.IMPORT_PACKAGE, "org.osgi.framework,org.sfm.csv" )
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


            hostApplication.deployWrapBundleWithClass(Cell.class);
            hostApplication.deployBundleWithClass(ClassVisitor.class);
            hostApplication.deployBundleWithClass(CsvParser.class);
            hostApplication.deployBundleWithClass(SheetMapper.class);

            InputStream is =
                    TinyBundles.bundle().add(PoiActivator.class)
                            .add(TestClass.class)
                            .set(Constants.BUNDLE_ACTIVATOR, PoiActivator.class.getName())
                            .set(Constants.IMPORT_PACKAGE, "org.osgi.framework,org.sfm.poi,org.apache.poi.ss.usermodel,org.apache.poi.hssf.usermodel" )
                            .build();
            Bundle bundle = hostApplication.install("tinyBundle", is);

            bundle.start();


        } finally {
            hostApplication.shutdownApplication();
        }
    }

}
