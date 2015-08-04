package org.sfm.test.osgi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import org.sfm.csv.CsvMapperFactory;
import org.sfm.poi.SheetMapperFactory;

import java.io.StringReader;

public class Activator implements BundleActivator {


    public void start(BundleContext bundleContext) throws Exception {
        System.out.println("Starting");

        final ServiceReference<LogService> serviceReference = (ServiceReference<LogService>) bundleContext.getServiceReference(LogService.class);
        LogService logService = bundleContext.getService(serviceReference);

        logService.log(2, "test csv");
        CsvMapperFactory.newInstance().newMapper(MyClass.class).forEach(new StringReader("s1,s2\nv1,v2"), (s) -> logService.log(1, s.toString()));

        logService.log(2, "test poi");
        Workbook wb = new HSSFWorkbook();
        final Sheet sheet = wb.createSheet();
        Row row = sheet.createRow(0);
        row.createCell(0).setCellValue("s1");
        row.createCell(1).setCellValue("s1");
        row = sheet.createRow(1);
        row.createCell(0).setCellValue("v1");
        row.createCell(1).setCellValue("v2");

        SheetMapperFactory.newInstance().newMapper(MyClass.class).forEach(sheet, (s) -> logService.log(1, s.toString()));
    }

    public void stop(BundleContext bundleContext) throws Exception {

    }

    public static class MyClass {
        public String s1;
        public String s2;


        @Override
        public String toString() {
            return "MyClass{" +
                    "s1='" + s1 + '\'' +
                    ", s2='" + s2 + '\'' +
                    '}';
        }
    }
}
