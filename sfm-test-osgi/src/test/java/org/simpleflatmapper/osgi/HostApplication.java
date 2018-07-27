package org.simpleflatmapper.osgi;

import org.apache.commons.io.IOUtils;
import org.apache.felix.framework.Felix;
import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.resolver.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleException;
import org.osgi.framework.wiring.BundleRevision;
import org.osgi.framework.wiring.BundleWiring;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;

public class HostApplication
{
    private HostActivator m_activator = null;
    private Felix m_felix = null;

    public HostApplication() throws IOException {
        // Create a configuration property map.
        Map<String, Object> config = new HashMap<String, Object>();
        // Create host activator;
        m_activator = new HostActivator();
        List<BundleActivator> list = new ArrayList<BundleActivator>();
        list.add(m_activator);
        config.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);
        config.put(FelixConstants.FRAMEWORK_STORAGE, "target/felix");
        config.put(FelixConstants.LOG_LEVEL_PROP, "5");

        cleanUp("target/felix");

        try
        {
            // Now create an instance of the framework with
            // our configuration properties.
            m_felix = new Felix(config);
            // Now start Felix instance.
            m_felix.start();
        }
        catch (Exception ex)
        {
            System.err.println("Could not create framework: " + ex);
            ex.printStackTrace();
        }
    }

    private void cleanUp(String location) throws IOException {
        System.out.println("cleanUp = " + location);
        Path path = FileSystems.getDefault().getPath(location);
        if(!Files.exists(path)) {
            return;
        }

        List<Path> collect = Files.walk(path).collect(Collectors.toList());

        for(int i = collect.size() - 1; i >=0 ; i--) {
            Files.delete(collect.get(i));
        }

    }

    public Bundle install(String location) throws BundleException {
        return m_activator.getContext().installBundle(location);
    }
    public Bundle[] getInstalledBundles()
    {
        // Use the system bundle activator to gain external
        // access to the set of installed bundles.
        return m_activator.getBundles();
    }

    public void shutdownApplication() throws BundleException, InterruptedException {
        // Shut down the felix framework when stopping the
        // host application.
        m_felix.stop();
        m_felix.waitForStop(0);
    }


    public void deployBundleWithClass(Class<?> aClass) throws BundleException, IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(aClass.getName().replace('.', '/') + ".class");

        if (resource == null) {
            throw new RuntimeException("Could not find resource for " + aClass);
        }

        System.out.println("resource = " + resource);
        Bundle b;
        if ("jar".equals(resource.getProtocol())) {
            String file = resource.getFile();
            file = file.substring(0, file.indexOf('!'));
            System.out.println("jar = " + file);
            b = install(file);

        } else {
            int indexOfTarget = resource.getFile().indexOf("target/classes");
            if (indexOfTarget >= 0) {
                File root = new File(resource.getFile().substring(0, indexOfTarget + "target/classes".length()));

                b = installBundle(root);


            } else {
                throw  new RuntimeException("Cannot build jar");
            }


        }
        b.start();

        ClassLoader classLoader = b.adapt(BundleWiring.class).getClassLoader();

        try {
            Class<?> aClass1 = classLoader.loadClass(aClass.getName());
            System.out.println("aClass1 = " + aClass1);

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        debugBundle(b);
    }
    
    public void installMavenLocalModule(String str) throws IOException, BundleException {

        URL resource = getClass().getClassLoader().getResource(getClass().getName().replace(".", "/") + ".class");
        File f = new File(resource.getFile()).getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile().getParentFile();

        File classes = new File(f, str + "/target/classes");

        installBundle(classes);
    }

    private Bundle installBundle(File classes) throws IOException, BundleException {
        Bundle b;
        System.out.println("dir = " + classes);

        File tmp = File.createTempFile("tmp", "jar");

        Manifest man;
        File file = new File(classes, "META-INF/MANIFEST.MF");
        System.out.println("manifest = " + file + " " + file.exists());
        try (FileInputStream is = new FileInputStream(file)) {
            man = new Manifest(is);
        }

        try (FileOutputStream fos = new FileOutputStream(tmp);
             JarOutputStream jarOutputStream = new JarOutputStream(fos, man)) {

            Files.walk(Paths.get(classes.getPath())).forEach((p) -> {
              if (Files.isRegularFile(p) && !p.endsWith("MANIFEST.MF")) {
                ZipEntry zipEntry = new ZipEntry(p.toString().substring(classes.getPath().length() + 1));
                  try {
                      jarOutputStream.putNextEntry(zipEntry);
                      Files.copy(p, jarOutputStream);

                  } catch (IOException e) {
                      e.printStackTrace();
                  }

              }
            });

        }


        b = install("file:" + tmp.getPath());
        return b;
    }


    public void deployWrapBundleWithClass(Class<?> aClass) throws BundleException, IOException {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(aClass.getName().replace('.', '/') + ".class");

        if (resource == null) {
            throw new RuntimeException("Could not find resource for " + aClass);
        }
        if ("jar".equals(resource.getProtocol())) {
            String file = resource.getFile();
            file = file.substring(file.indexOf(':') + 1, file.indexOf('!'));

            File tmpFile = File.createTempFile("wrap", ".jar");

            Set<String> packages = new HashSet<String>();
            try (FileInputStream fis = new FileInputStream(file);
                 JarInputStream jis = new JarInputStream(fis);
            ) {
                JarEntry zentry;
                while ((zentry = jis.getNextJarEntry()) != null) {
                    String name = zentry.getName();
                    if (name.endsWith(".class")) {
                        packages.add(name.substring(0, name.lastIndexOf('/')));
                    }
                }
            }

            try (FileInputStream fis = new FileInputStream(file);
                 JarInputStream jis = new JarInputStream(fis);
            ) {
                Manifest man = jis.getManifest();

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                man.write(bos);


                Attributes mainAttributes = man.getMainAttributes();
                mainAttributes.put(new Attributes.Name("Bundle-ManifestVersion"), "2");
                mainAttributes.put(new Attributes.Name("Bundle-SymbolicName"), file.substring(file.lastIndexOf("/") + 1, file.lastIndexOf('.')));
                StringBuilder sb = new StringBuilder();
                boolean first = true;
                for(String p : packages) {

                    if (!first) {
                        sb.append(",");
                    }
                    sb.append(p.replace('/', '.'));
                    first = false;
                }

                mainAttributes.put(new Attributes.Name("Export-Package"), sb.toString());


                try (FileOutputStream fos = new FileOutputStream(tmpFile);
                     JarOutputStream jos = new JarOutputStream(fos, man)
                ) {
                    JarEntry zentry;
                    while ((zentry = jis.getNextJarEntry()) != null) {
                        jos.putNextEntry(zentry);
                        IOUtils.copy(jis, jos);
                    }
                }
            }



            Bundle b = install("file:" + tmpFile.getPath());


            b.start();
            ClassLoader classLoader = b.adapt(BundleWiring.class).getClassLoader();

            try {
                Class<?> aClass1 = classLoader.loadClass(aClass.getName());
                System.out.println("aClass1 = " + aClass1);

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            debugBundle(b);

        } else {
            throw new RuntimeException("No jar " + resource);
        }
    }

    private void debugBundle(Bundle b) {
        BundleRevision br = b.adapt(BundleRevision.class);

        br.getCapabilities(null).forEach(System.out::println);

        b.adapt(BundleWiring.class).getProvidedWires(null).forEach(System.out::println);
        ;
    }


    public Bundle install(URL url) throws IOException, BundleException {
        System.out.println("install = " + url);
        InputStream is = url.openStream();
        try {
            Bundle b = install(url.toString(), is);
            b.start();
            return b;
        } finally {
            is.close();
        }
    }
    public Bundle install(String location, InputStream is) throws BundleException {
        return m_activator.getContext().installBundle(location, is);
    }
}