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

public class PoiMapperTest {


    Sheet sheet;
    PoiMapper<DbObject> poiMapper;
    @Before
    public void setUp(){
        Workbook wb = new HSSFWorkbook();
        sheet = wb.createSheet();

        for(int i = 0; i < 3; i++) {
            Row row = sheet.createRow(i);
            row.createCell(0).setCellValue(i);
            row.createCell(1).setCellValue("name" + i);
            row.createCell(2).setCellValue("email" + i);
            row.createCell(3).setCellValue(new Date(i * 10000 ));
            row.createCell(4).setCellValue(DbObject.Type.values()[i].ordinal());
            row.createCell(5).setCellValue(DbObject.Type.values()[i].name());
        }

        poiMapper =
            PoiMapperFactory
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

        Iterator<DbObject> iterator = poiMapper.iterator(sheet);

        assertTrue(iterator.hasNext());
        assertDbObject(0, iterator.next());
        assertTrue(iterator.hasNext());
        assertDbObject(1, iterator.next());
        assertTrue(iterator.hasNext());
        assertDbObject(2, iterator.next());

    }


    @Test
    public void forEachOnSheetFrom0WithStaticMapper() {
        int row = poiMapper.forEach(sheet, new RowHandler<DbObject>() {
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

        List<DbObject> list = poiMapper.stream(sheet).collect(Collectors.toList());
        assertEquals(3, list.size());
        assertDbObject(0, list.get(0));
        assertDbObject(1, list.get(1));
        assertDbObject(2, list.get(2));

    }

    @Test
    public void streamOnSheetFrom0WithStreamWithLimitAndStaticMapper() {
        List<DbObject> list = poiMapper.stream(sheet).limit(2).collect(Collectors.toList());
        assertEquals(2, list.size());
        assertDbObject(0, list.get(0));
        assertDbObject(1, list.get(1));
    }
    //IFJAVA8_END


    private void assertDbObject(int index, DbObject o) {
        assertEquals(index, o.getId());
        assertEquals("name" + index, o.getName());
        assertEquals("email" + index, o.getEmail());
        assertEquals(index * 10000, o.getCreationTime().getTime());
        assertEquals(DbObject.Type.values()[index % 4], o.getTypeOrdinal());
        assertEquals(DbObject.Type.values()[index % 4], o.getTypeName());
    }
}
