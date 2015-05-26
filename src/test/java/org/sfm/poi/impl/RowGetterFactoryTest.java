package org.sfm.poi.impl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Test;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.CsvColumnKey;
import org.sfm.reflect.Getter;
import org.sfm.reflect.primitive.*;

import java.util.Date;

import static org.junit.Assert.*;

public class RowGetterFactoryTest {

    RowGetterFactory rowGetterFactory = new RowGetterFactory();

    CsvColumnKey key = new CsvColumnKey("key", 1);

    Workbook wb = new HSSFWorkbook();
    Sheet sheet = wb.createSheet();
    Row row = sheet.createRow(1);
    Cell cell = row.createCell(1);

    @Test
    public void testGetStringOnStringCell() throws Exception {
        final Getter<Row, String> getter = rowGetterFactory.newGetter(String.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue("value");
        assertEquals("value", getter.get(row));
    }

    @Test
    public void testGetDoubleOnDoubleCell() throws Exception {
        final Getter<Row, Double> getter = rowGetterFactory.newGetter(Double.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3.22);
        assertEquals(3.22, getter.get(row), 0.0001);
        assertEquals(3.22, ((DoubleGetter<Row>)getter).getDouble(row), 0.0001);
    }

    @Test
    public void testGetFloatOnDoubleCell() throws Exception {
        final Getter<Row, Float> getter = rowGetterFactory.newGetter(Float.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3.22);
        assertEquals(3.22, getter.get(row), 0.0001);
        assertEquals(3.22, ((FloatGetter<Row>)getter).getFloat(row), 0.0001);
    }

    @Test
    public void testGetLongOnDoubleCell() throws Exception {
        final Getter<Row, Long> getter = rowGetterFactory.newGetter(Long.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3l);
        assertEquals(3l, getter.get(row).longValue());
        assertEquals(3l, ((LongGetter<Row>)getter).getLong(row));
    }

    @Test
    public void testGetIntegerOnDoubleCell() throws Exception {
        final Getter<Row, Integer> getter = rowGetterFactory.newGetter(Integer.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3);
        assertEquals(3, getter.get(row).intValue());
        assertEquals(3, ((IntGetter<Row>)getter).getInt(row));
    }

    @Test
    public void testGetShortOnDoubleCell() throws Exception {
        final Getter<Row, Short> getter = rowGetterFactory.newGetter(Short.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3);
        assertEquals(3, getter.get(row).shortValue());
        assertEquals(3, ((ShortGetter<Row>)getter).getShort(row));
    }

    @Test
    public void testGetCharacterOnDoubleCell() throws Exception {
        final Getter<Row, Character> getter = rowGetterFactory.newGetter(Character.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3);
        assertEquals(3, getter.get(row).charValue());
        assertEquals(3, ((CharacterGetter<Row>)getter).getCharacter(row));
    }

    @Test
    public void testGetByteOnDoubleCell() throws Exception {
        final Getter<Row, Byte> getter = rowGetterFactory.newGetter(Byte.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3);
        assertEquals(3, getter.get(row).byteValue());
        assertEquals(3, ((ByteGetter<Row>)getter).getByte(row));
    }

    @Test
    public void testGetDateOnDateCell() throws Exception {
        final Getter<Row, Date> getter = rowGetterFactory.newGetter(Date.class, key, CsvColumnDefinition.IDENTITY);
        Date now = new Date();
        cell.setCellValue(now);
        assertEquals(now, getter.get(row));
    }

}