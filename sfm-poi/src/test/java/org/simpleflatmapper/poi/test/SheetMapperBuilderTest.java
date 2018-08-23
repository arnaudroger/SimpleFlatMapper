package org.simpleflatmapper.poi.test;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.poi.RowMapper;
import org.simpleflatmapper.poi.SheetMapperBuilder;
import org.simpleflatmapper.poi.SheetMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.poi.impl.CsvColumnKeyRowKeySourceGetter;

import java.util.Date;

import static org.junit.Assert.*;


public class SheetMapperBuilderTest {

    Workbook workbook;
    Sheet sheet;
    Row row;
    Date now = new Date();
    CsvColumnKeyRowKeySourceGetter keySourceGetter = CsvColumnKeyRowKeySourceGetter.INSTANCE;

    @Before
    public void setUp(){


        workbook = new HSSFWorkbook();

        sheet = workbook.createSheet();

        row = sheet.createRow(0);

        Date now = new Date();
        row.createCell(0).setCellValue(13);
        row.createCell(1).setCellValue("name");
        row.createCell(2).setCellValue("email");
        row.createCell(3).setCellValue(now);
        row.createCell(4).setCellValue(1);
        row.createCell(5).setCellValue("type3");

        final Row rowKey = sheet.createRow(1);
        rowKey.createCell(0).setCellValue("val");
        rowKey.createCell(1).setCellValue(new Date(12000l));
        rowKey.createCell(2).setCellValue(1.234);
        rowKey.createCell(3).setCellValue(true);
        rowKey.createCell(4);

    }

    @Test
    public void testKeyGetterOnNull() {
       assertNull(keySourceGetter.getValue(new CsvColumnKey("", 5), sheet.getRow(1)));
    }
    @Test
    public void testKeyGetterOnBlank() {
        assertNull(keySourceGetter.getValue(new CsvColumnKey("", 5), sheet.getRow(1)));
    }

    @Test
    public void testKeyGetterOnString() {
        assertEquals("val", keySourceGetter.getValue(new CsvColumnKey("", 0), sheet.getRow(1)));
    }
    @Test
    public void testKeyGetterOnDate() {
        assertEquals(25569, (Double)keySourceGetter.getValue(new CsvColumnKey("", 1), sheet.getRow(1)), 1);
    }

    @Test
    public void testKeyGetterOnDouble() {
        assertEquals(1.234, (Double)keySourceGetter.getValue(new CsvColumnKey("", 2), sheet.getRow(1)), 0.0001);
    }

    @Test
    public void testKeyGetterOnBoolean() {
        assertEquals(Boolean.TRUE, keySourceGetter.getValue(new CsvColumnKey("", 3), sheet.getRow(1)));
    }

    @Test
    public void testMapDbObjectFromRow() {
        SheetMapperBuilder<DbObject> builder = SheetMapperFactory.newInstance().newBuilder(DbObject.class);

        builder
                .addMapping("id")
                .addMapping("name")
                .addMapping("email")
                .addMapping("creation_time")
                .addMapping("type_ordinal")
                .addMapping("type_name");

        RowMapper<DbObject> mapper = builder.mapper();


        final DbObject dbObject = mapper.map(row);
        assertEquals(13, dbObject.getId());
        assertEquals("name", dbObject.getName());
        assertEquals("email", dbObject.getEmail());
        assertTrue(Math.abs(now.getTime() -  dbObject.getCreationTime().getTime()) < 1000);
        assertEquals(DbObject.Type.type2, dbObject.getTypeOrdinal());
        assertEquals(DbObject.Type.type3, dbObject.getTypeName());
    }
}