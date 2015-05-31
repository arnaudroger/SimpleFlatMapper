package org.sfm.poi;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.utils.RowHandler;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

//IFJAVA8_START
import java.util.stream.Collectors;
//IFJAVA8_END


import static org.junit.Assert.*;

public class SheetMapperTest {


    Sheet staticSheet;
    Sheet dynamicSheet;
    SheetMapper<DbObject> staticSheetMapper;
    SheetMapper<DbObject> dynamicSheetMapper;
    @Before
    public void setUp(){
        Workbook wb = new HSSFWorkbook();
        staticSheet = wb.createSheet();

        for(int i = 0; i < 3; i++) {
            Row row = staticSheet.createRow(i);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue("name" + i);
            row.createCell(2).setCellValue("email" + i);
            row.createCell(3).setCellValue(new Date(i * 10000 ));
            row.createCell(4).setCellValue(DbObject.Type.values()[i].ordinal());
            row.createCell(5).setCellValue(DbObject.Type.values()[i].name());
        }

        dynamicSheet = wb.createSheet();

        Row header = dynamicSheet.createRow(0);
        header.createCell(0).setCellValue("id");
        header.createCell(1).setCellValue("name");
        header.createCell(2).setCellValue("email");
        header.createCell(3).setCellValue("creation_time");
        header.createCell(4).setCellValue("type_ordinal");
        header.createCell(5).setCellValue("type_name")
        ;
        for(int i = 0; i < 3; i++) {
            Row row = dynamicSheet.createRow(i +1);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue("name" + i);
            row.createCell(2).setCellValue("email" + i);
            row.createCell(3).setCellValue(new Date(i * 10000 ));
            row.createCell(4).setCellValue(DbObject.Type.values()[i].ordinal());
            row.createCell(5).setCellValue(DbObject.Type.values()[i].name());
        }



        staticSheetMapper =
            SheetMapperFactory
                .newInstance()
                .newBuilder(DbObject.class)
                .addMapping("id")
                .addMapping("name")
                .addMapping("email")
                .addMapping("creation_time")
                .addMapping("type_ordinal")
                .addMapping("type_name")
                .mapper();
        dynamicSheetMapper =
                SheetMapperFactory.newInstance().newMapper(DbObject.class);

    }

    @Test
    public void iteratorOnSheetFrom0WithStaticMapper() {
        Iterator<DbObject> iterator = staticSheetMapper.iterator(staticSheet);
        testIteratorHasExpectedValue(iterator);
    }

    @Test
    public void iteratorOnSheetFrom0WithDynamicMapper() {
        Iterator<DbObject> iterator = dynamicSheetMapper.iterator(dynamicSheet);
        testIteratorHasExpectedValue(iterator);
    }


    @Test
    public void forEachOnSheetFrom0WithStaticMapper() {
        int row = staticSheetMapper.forEach(staticSheet, new RowHandler<DbObject>() {
            int row = 0;

            @Override
            public void handle(DbObject dbObject) throws Exception {
                assertDbObject(row, dbObject);
                row++;
            }
        }).row;

        assertEquals(3, row);
    }


    @Test
    public void forEachOnSheetFrom0WithDynamicMapper() {
        int row = dynamicSheetMapper.forEach(dynamicSheet, new RowHandler<DbObject>() {
            int row = 0;

            @Override
            public void handle(DbObject dbObject) throws Exception {
                assertDbObject(row, dbObject);
                row++;
            }
        }).row;

        assertEquals(3, row);
    }

    //IFJAVA8_START
    @Test
    public void streamOnSheetFrom0WithStreamWithStaticMapper() {

        List<DbObject> list = staticSheetMapper.stream(staticSheet).collect(Collectors.toList());
        assertEquals(3, list.size());
        assertDbObject(0, list.get(0));
        assertDbObject(1, list.get(1));
        assertDbObject(2, list.get(2));
    }

    @Test
    public void streamOnSheetFrom0WithStreamWithDynamicMapper() {

        List<DbObject> list = dynamicSheetMapper.stream(dynamicSheet).collect(Collectors.toList());
        assertEquals(3, list.size());
        assertDbObject(0, list.get(0));
        assertDbObject(1, list.get(1));
        assertDbObject(2, list.get(2));
    }

    @Test
    public void streamOnSheetFrom0WithStreamWithLimitAndStaticMapper() {
        List<DbObject> list = staticSheetMapper.stream(staticSheet).limit(2).collect(Collectors.toList());
        assertEquals(2, list.size());
        assertDbObject(0, list.get(0));
        assertDbObject(1, list.get(1));
    }

    @Test
    public void streamOnSheetFrom0WithStreamWithLimitAndDynamicMapper() {
        List<DbObject> list = dynamicSheetMapper.stream(dynamicSheet).limit(2).collect(Collectors.toList());
        assertEquals(2, list.size());
        assertDbObject(0, list.get(0));
        assertDbObject(1, list.get(1));
    }
    //IFJAVA8_END

    protected void testIteratorHasExpectedValue(Iterator<DbObject> iterator) {
        assertTrue(iterator.hasNext());
        assertDbObject(0, iterator.next());
        assertTrue(iterator.hasNext());
        assertDbObject(1, iterator.next());
        assertTrue(iterator.hasNext());
        assertDbObject(2, iterator.next());
    }


    private void assertDbObject(int index, DbObject o) {
        assertEquals(index, o.getId());
        assertEquals("name" + index, o.getName());
        assertEquals("email" + index, o.getEmail());
        assertEquals(index * 10000, o.getCreationTime().getTime());
        assertEquals(DbObject.Type.values()[index % 4], o.getTypeOrdinal());
        assertEquals(DbObject.Type.values()[index % 4], o.getTypeName());
    }
}
