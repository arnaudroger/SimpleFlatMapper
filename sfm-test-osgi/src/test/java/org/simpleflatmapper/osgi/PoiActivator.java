package org.simpleflatmapper.osgi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.simpleflatmapper.poi.SheetMapper;
import org.simpleflatmapper.poi.SheetMapperFactory;

public class PoiActivator implements BundleActivator {
    @Override
    public void start(BundleContext context) throws Exception {
        SheetMapper<TestClass> sheetMapper = SheetMapperFactory.newInstance().newMapper(TestClass.class);

        Workbook workbook = new HSSFWorkbook();
        Sheet sheet = workbook.createSheet();
        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("id");
        header.createCell(1).setCellValue("name");

        Row data1 = sheet.createRow(1);
        data1.createCell(0).setCellValue(1);
        data1.createCell(1).setCellValue("name1");

        Row data2 = sheet.createRow(2);
        data2.createCell(0).setCellValue(2);
        data2.createCell(1).setCellValue("name2");

        sheetMapper.stream(sheet).forEach(System.out::println);


    }

    @Override
    public void stop(BundleContext context) throws Exception {
        // nothing to do here
    }

}
