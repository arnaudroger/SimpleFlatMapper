package org.simpleflatmapper.osgi;

import org.apache.commons.io.IOUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.junit.Test;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.poi.SheetMapper;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class OsgiTest {


    private static final URL FELIX   = url("https://repo1.maven.org/maven2/org/apache/felix/org.apache.felix.log/1.0.1/org.apache.felix.log-1.0.1.jar");
    private static final URL ASM     = url("https://repo1.maven.org/maven2/org/ow2/asm/asm-all/5.0.4/asm-all-5.0.4.jar");
    private static final URL ASM6    = url("https://repo1.maven.org/maven2/org/ow2/asm/asm-all/6.0_ALPHA/asm-all-6.0_ALPHA.jar");
    private static final URL OW2ASM     = url("https://repo1.maven.org/maven2/org/simpleflatmapper/ow2-asm/5.2/ow2-asm-5.2.jar");
    private static final URL OW2ASM6    = url("https://repo1.maven.org/maven2/org/simpleflatmapper/ow2-asm/6.2/ow2-asm-6.2.jar");
    private static final URL ARIES   = url("https://repo1.maven.org/maven2/org/apache/aries/org.apache.aries.util/1.1.3/org.apache.aries.util-1.1.3.jar");
    private static final URL SPLIFLY = url("https://repo1.maven.org/maven2/org/apache/aries/spifly/org.apache.aries.spifly.dynamic.bundle/1.0.12/org.apache.aries.spifly.dynamic.bundle-1.0.12.jar");


    private static URL url(String s) {
        try {
            return new URL(s);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCsvParser() throws BundleException, InterruptedException, IOException {
        HostApplication hostApplication = new HostApplication();
        try {

            installFelix(hostApplication);
            installSfmMap(hostApplication);
            hostApplication.installMavenLocalModule("lightningcsv");
            hostApplication.installMavenLocalModule("sfm-csv");

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

            installFelix(hostApplication);

            hostApplication.deployWrapBundleWithClass(Cell.class);
            installSfmMap(hostApplication);
            hostApplication.installMavenLocalModule("lightningcsv");
            hostApplication.installMavenLocalModule("sfm-csv");
            hostApplication.installMavenLocalModule("sfm-poi");
            
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
        hostApplication.installMavenLocalModule("sfm-tuples");
        hostApplication.installMavenLocalModule("sfm-util");
        hostApplication.installMavenLocalModule("sfm-converter");
        hostApplication.installMavenLocalModule("sfm-reflect");
        hostApplication.installMavenLocalModule("sfm-map");
    }

    private void installFelix(HostApplication hostApplication) throws IOException, BundleException {
        hostApplication.install(FELIX);

        String javaVersion = System.getProperty("java.version");
        System.out.println("javaVersion = " + javaVersion);
        if (javaVersion.startsWith("1.7") && javaVersion.startsWith("1.6")) {
            hostApplication.install(ASM);
            hostApplication.install(OW2ASM);
            hostApplication.install(ARIES);
            hostApplication.install(SPLIFLY);
        } else {
            hostApplication.install(repackage(ASM));
            hostApplication.install(repackage(OW2ASM6));
            hostApplication.install(repackage(ARIES));
            hostApplication.install(repackage(SPLIFLY));

        }
    }

    private URL repackage(URL asm6) throws IOException {
        File tmpFile = File.createTempFile("bundle", "jar");
        try (InputStream fis = asm6.openStream();
             JarInputStream jis = new JarInputStream(fis);
        ) {
            Manifest man = jis.getManifest();

            Attributes mainAttributes = man.getMainAttributes();

            System.out.println("mainAttributes = " + mainAttributes.keySet());

            mainAttributes.remove(new Attributes.Name("Bundle-RequiredExecutionEnvironment"));
            mainAttributes.remove(new Attributes.Name("Require-Capability"));


            try (FileOutputStream fos = new FileOutputStream(tmpFile);
                 JarOutputStream jos = new JarOutputStream(fos, man)
            ) {
                JarEntry zentry;
                while ((zentry = jis.getNextJarEntry()) != null) {
                    jos.putNextEntry(zentry);
                    IOUtils.copy(jis, jos);
                }
            } catch (Throwable t) {
                System.out.println("t = " + t);
                t.printStackTrace(System.out);
                throw t;
            }
        }

        return tmpFile.toURI().toURL();
    }


}
