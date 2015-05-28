package org.sfm.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbObject;

import java.util.Date;

import static org.junit.Assert.*;


public class PoiMapperBuilderTest {

    Workbook workbook;
    Sheet sheet;
    Row row;
    Date now = new Date();
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
    }


    @Test
    public void testMapDbObjectFromRow() {
        PoiMapperBuilder<DbObject> builder = new PoiMapperBuilder<DbObject>(DbObject.class);

        builder
                .addMapping("id")
                .addMapping("name")
                .addMapping("email")
                .addMapping("creation_time")
                .addMapping("type_ordinal")
                .addMapping("type_name");

        PoiMapper<DbObject> mapper = builder.mapper();


        final DbObject dbObject = mapper.map(row);
        assertEquals(13, dbObject.getId());
        assertEquals("name", dbObject.getName());
        assertEquals("email", dbObject.getEmail());
        assertTrue(Math.abs(now.getTime() -  dbObject.getCreationTime().getTime()) < 1000);
        assertEquals(DbObject.Type.type2, dbObject.getTypeOrdinal());
        assertEquals(DbObject.Type.type3, dbObject.getTypeName());
    }
}