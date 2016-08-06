package org.simpleflatmapper.osgi;

import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.poi.SheetMapper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ErrorHelper;

import java.io.*;

public class OsgiTest {


    @Test
    public void testCsvParser() throws BundleException, InterruptedException, IOException {
        HostApplication hostApplication = new HostApplication();
        try {


            loadClassVisitor(hostApplication);
            installSfmMap(hostApplication);
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
            installSfmMap(hostApplication);
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

    public void installSfmMap(HostApplication hostApplication) throws BundleException, IOException {
        hostApplication.deployBundleWithClass(Tuple2.class);
        hostApplication.deployBundleWithClass(ErrorHelper.class);
        hostApplication.deployBundleWithClass(Converter.class);
        hostApplication.deployBundleWithClass(Getter.class);
        hostApplication.deployBundleWithClass(Mapper.class);
    }

    private void loadClassVisitor(HostApplication hostApplication) throws BundleException, IOException {
        if (!System.getProperty("java.version").contains("9-ea")) {
            hostApplication.deployBundleWithClass(ClassVisitor.class);
        }
    }

}
