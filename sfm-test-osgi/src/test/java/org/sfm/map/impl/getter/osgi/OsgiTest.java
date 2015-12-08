package org.sfm.map.impl.getter.osgi;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.sfm.csv.CsvParser;
import org.sfm.poi.SheetMapper;

import java.io.*;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

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


}
