package org.simpleflatmapper.poi.test;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.poi.SheetMapper;
import org.simpleflatmapper.poi.SheetMapperFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.CheckedConsumer;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

//IFJAVA8_START
import java.util.stream.Collectors;
//IFJAVA8_END

public class StaticSheetMapperTest {


    Sheet staticSheet;

    SheetMapper<DbObject> staticSheetMapper;
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

    }

    @Test
    public void iteratorOnSheetFrom0WithStaticMapper() {
        Iterator<DbObject> iterator = staticSheetMapper.iterator(staticSheet);
        testIteratorHasExpectedValue(iterator);
    }

    @Test
    public void forEachOnSheetFrom0WithStaticMapper() {
        int row = staticSheetMapper.forEach(staticSheet, new CheckedConsumer<DbObject>() {
            int row = 0;

            @Override
            public void accept(DbObject dbObject) throws Exception {
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
    public void streamOnSheetFrom0WithStreamWithLimitAndStaticMapper() {
        List<DbObject> list = staticSheetMapper.stream(staticSheet).limit(2).collect(Collectors.toList());
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
