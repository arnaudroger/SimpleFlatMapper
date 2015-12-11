package org.sfm.map.impl.getter.osgi;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.objectweb.asm.ClassVisitor;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.sfm.csv.CsvParser;
import org.sfm.poi.SheetMapper;

import java.io.*;

/**
 * Created by aroger on 08/12/15.
 */
public class OsgiTest {


    @Test
    public void testPackageDeployed() throws BundleException, InterruptedException, IOException {

        System.out.println("Hello!");
        HostApplication hostApplication = new HostApplication();
        try {


            hostApplication.deployBundleWithClass(CsvParser.class);
            hostApplication.deployWrapBundleWithClass(Cell.class);
            hostApplication.deployBundleWithClass(SheetMapper.class);

            for(Bundle b : hostApplication.getInstalledBundles()) {
                System.out.println("b = " + b.getSymbolicName() + "/" + b.getState());
            }

        } finally {
            hostApplication.shutdownApplication();
        }
    }

    @Test
    public void testCsvParser() throws BundleException, InterruptedException, IOException {

        System.out.println("Hello!");
        HostApplication hostApplication = new HostApplication();
        try {


            hostApplication.deployBundleWithClass(ClassVisitor.class);
            hostApplication.deployBundleWithClass(CsvParser.class);

            InputStream is =
                    TinyBundles.bundle().add(CsvActivator.class)
                            .set(Constants.BUNDLE_ACTIVATOR, CsvActivator.class.getName())
                            .set(Constants.IMPORT_PACKAGE, "org.osgi.framework,org.sfm.csv" )
                            .build();
            Bundle bundle = hostApplication.install("tinyBundle", is);

            bundle.start();


        } finally {
            hostApplication.shutdownApplication();
        }
    }


}
