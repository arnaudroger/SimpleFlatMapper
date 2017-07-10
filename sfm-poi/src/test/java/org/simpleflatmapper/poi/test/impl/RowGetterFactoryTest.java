package org.simpleflatmapper.poi.test.impl;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.joda.time.DateTime;
import org.junit.Test;
import org.simpleflatmapper.poi.impl.RowGetterFactory;
import org.simpleflatmapper.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.CharacterGetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.reflect.Getter;

import java.text.SimpleDateFormat;
//IFJAVA8_START
import java.time.LocalDateTime;
import java.time.ZoneId;
//IFJAVA8_END
import java.util.Date;

import static org.junit.Assert.*;

@SuppressWarnings("unchecked")
public class RowGetterFactoryTest {

    RowGetterFactory rowGetterFactory = new RowGetterFactory();

    CsvColumnKey key = new CsvColumnKey("key", 1);
    CsvColumnKey blankCellKey = new CsvColumnKey("key", 2);
    CsvColumnKey noCellKey = new CsvColumnKey("key", 3);

    Workbook wb = new HSSFWorkbook();
    Sheet sheet = wb.createSheet();
    Row row = sheet.createRow(1);
    Cell cell = row.createCell(1);
    Cell blankCell = row.createCell(2);

    DataFormat dataFormat = wb.createDataFormat();

    CellStyle twoDigitCellFormat = wb.createCellStyle();
    CellStyle dateCellFormat = wb.createCellStyle();
    {
        twoDigitCellFormat.setDataFormat(dataFormat.getFormat("#.##"));
        dateCellFormat.setDataFormat(dataFormat.getFormat("dd/MM/yyyy"));
    }

    @Test
    public void testGetStringOnStringCell() throws Exception {
        final Getter<Row, String> getter = rowGetterFactory.newGetter(String.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue("value");
        assertEquals("value", getter.get(row));
    }

    @Test
    public void testGetStringOnDoubleCell() throws Exception {
        final Getter<Row, String> getter = rowGetterFactory.newGetter(String.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3.1456);
        cell.setCellStyle(twoDigitCellFormat);
        assertEquals("3.15", getter.get(row));
    }

    @Test
    public void testGetStringOnDateCell() throws Exception {
        final Getter<Row, String> getter = rowGetterFactory.newGetter(String.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(new SimpleDateFormat("yyyyMMdd").parse("20150527"));
        cell.setCellStyle(dateCellFormat);
        assertEquals("27/05/2015", getter.get(row));
    }

    @Test
    public void testGetStringOnBooleanCell() throws Exception {
        final Getter<Row, String> getter = rowGetterFactory.newGetter(String.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(true);
        assertEquals("TRUE", getter.get(row));
    }

    @Test
    public void testGetStringOnBlankCell() throws Exception {
        final Getter<Row, String> getter = rowGetterFactory.newGetter(String.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertEquals("", getter.get(row));
    }
    @Test
    public void testGetStringOnNullCell() throws Exception {
        final Getter<Row, String> getter = rowGetterFactory.newGetter(String.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertEquals(null, getter.get(row));
    }

    @Test
    public void testGetDoubleOnDoubleCell() throws Exception {
        final Getter<Row, Double> getter = rowGetterFactory.newGetter(Double.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3.22);
        assertEquals(3.22, getter.get(row), 0.0001);
        assertEquals(3.22, ((DoubleGetter<Row>)getter).getDouble(row), 0.0001);
    }

    @Test
    public void testGetDoubleOnBlankCell() throws Exception {
        final Getter<Row, Double> getter = rowGetterFactory.newGetter(Double.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertEquals(0.0, ((DoubleGetter<Row>)getter).getDouble(row), 0.0001);
    }

    @Test
    public void testGetDoubleOnNullCell() throws Exception {
        final Getter<Row, Double> getter = rowGetterFactory.newGetter(Double.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertEquals(0.0, ((DoubleGetter<Row>)getter).getDouble(row), 0.0001);
    }

    @Test
    public void testGetFloatOnDoubleCell() throws Exception {
        final Getter<Row, Float> getter = rowGetterFactory.newGetter(Float.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3.22);
        assertEquals(3.22, getter.get(row), 0.0001);
        assertEquals(3.22, ((FloatGetter<Row>)getter).getFloat(row), 0.0001);
    }

    @Test
    public void testGetFloatOnBlankCell() throws Exception {
        final Getter<Row, Float> getter = rowGetterFactory.newGetter(Float.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertEquals(0.0, getter.get(row), 0.0001);
        assertEquals(0.0, ((FloatGetter<Row>)getter).getFloat(row), 0.0001);
    }

    @Test
    public void testGetFloatOnNullCell() throws Exception {
        final Getter<Row, Float> getter = rowGetterFactory.newGetter(Float.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertEquals(0.0, ((FloatGetter<Row>)getter).getFloat(row), 0.0001);
    }


    @Test
    public void testGetLongOnDoubleCell() throws Exception {
        final Getter<Row, Long> getter = rowGetterFactory.newGetter(Long.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3l);
        assertEquals(3l, getter.get(row).longValue());
        assertEquals(3l, ((LongGetter<Row>)getter).getLong(row));
    }

    @Test
    public void testGetLongOnBlankCell() throws Exception {
        final Getter<Row, Long> getter = rowGetterFactory.newGetter(Long.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertEquals(0, getter.get(row).longValue());
        assertEquals(0, ((LongGetter<Row>)getter).getLong(row));
    }

    @Test
    public void testGetLongOnNullCell() throws Exception {
        final Getter<Row, Long> getter = rowGetterFactory.newGetter(Long.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertEquals(0, ((LongGetter<Row>)getter).getLong(row));
    }

    @Test
    public void testGetIntegerOnDoubleCell() throws Exception {
        final Getter<Row, Integer> getter = rowGetterFactory.newGetter(Integer.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3);
        assertEquals(3, getter.get(row).intValue());
        assertEquals(3, ((IntGetter<Row>)getter).getInt(row));
    }

    @Test
    public void testGetIntegerOnBlankCell() throws Exception {
        final Getter<Row, Integer> getter = rowGetterFactory.newGetter(Integer.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertEquals(0, getter.get(row).intValue());
        assertEquals(0, ((IntGetter<Row>) getter).getInt(row));
    }

    @Test
    public void testGetIntegerOnNullCell() throws Exception {
        final Getter<Row, Integer> getter = rowGetterFactory.newGetter(Integer.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertEquals(0, ((IntGetter<Row>)getter).getInt(row));
    }

    @Test
    public void testGetShortOnDoubleCell() throws Exception {
        final Getter<Row, Short> getter = rowGetterFactory.newGetter(Short.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3);
        assertEquals(3, getter.get(row).shortValue());
        assertEquals(3, ((ShortGetter<Row>)getter).getShort(row));
    }

    @Test
    public void testGetShortOnBlankCell() throws Exception {
        final Getter<Row, Short> getter = rowGetterFactory.newGetter(Short.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertEquals(0, getter.get(row).shortValue());
        assertEquals(0, ((ShortGetter<Row>)getter).getShort(row));
    }


    @Test
    public void testGetShortOnNullCell() throws Exception {
        final Getter<Row, Short> getter = rowGetterFactory.newGetter(Short.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertEquals(0, ((ShortGetter<Row>)getter).getShort(row));
    }


    @Test
    public void testGetCharacterOnDoubleCell() throws Exception {
        final Getter<Row, Character> getter = rowGetterFactory.newGetter(Character.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3);
        assertEquals(3, getter.get(row).charValue());
        assertEquals(3, ((CharacterGetter<Row>)getter).getCharacter(row));
    }

    @Test
    public void testGetCharacterOnBlankCell() throws Exception {
        final Getter<Row, Character> getter = rowGetterFactory.newGetter(Character.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertEquals(0, getter.get(row).charValue());
        assertEquals(0, ((CharacterGetter<Row>)getter).getCharacter(row));
    }

    @Test
    public void testGetCharacterOnNullCell() throws Exception {
        final Getter<Row, Character> getter = rowGetterFactory.newGetter(Character.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertEquals(0, ((CharacterGetter<Row>)getter).getCharacter(row));
    }

    @Test
    public void testGetByteOnDoubleCell() throws Exception {
        final Getter<Row, Byte> getter = rowGetterFactory.newGetter(Byte.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(3);
        assertEquals(3, getter.get(row).byteValue());
        assertEquals(3, ((ByteGetter<Row>)getter).getByte(row));
    }

    @Test
    public void testGetByteOnBlankCell() throws Exception {
        final Getter<Row, Byte> getter = rowGetterFactory.newGetter(Byte.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertEquals(0, getter.get(row).byteValue());
        assertEquals(0, ((ByteGetter<Row>)getter).getByte(row));
    }


    @Test
    public void testGetByteOnNullCell() throws Exception {
        final Getter<Row, Byte> getter = rowGetterFactory.newGetter(Byte.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertEquals(0, ((ByteGetter<Row>)getter).getByte(row));
    }


    @Test
    public void testGetDateOnDateCell() throws Exception {
        final Getter<Row, Date> getter = rowGetterFactory.newGetter(Date.class, key, CsvColumnDefinition.IDENTITY);
        Date now = new Date();
        cell.setCellValue(now);
        assertEquals(now, getter.get(row));
    }

    @Test
    public void testGetDateOnBlankCell() throws Exception {
        final Getter<Row, Date> getter = rowGetterFactory.newGetter(Date.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
    }


    @Test
    public void testGetBooleanOnDoubleCell() throws Exception {
        final Getter<Row, Boolean> getter = rowGetterFactory.newGetter(Boolean.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(true);
        assertTrue(getter.get(row).booleanValue());
        assertTrue(((BooleanGetter<Row>) getter).getBoolean(row));
    }

    @Test
    public void testGetBooleanOnBlankCell() throws Exception {
        final Getter<Row, Boolean> getter = rowGetterFactory.newGetter(Boolean.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertFalse(getter.get(row).booleanValue());
        assertFalse(((BooleanGetter<Row>) getter).getBoolean(row));
    }


    @Test
    public void testGetBooleanOnNullCell() throws Exception {
        final Getter<Row, Boolean> getter = rowGetterFactory.newGetter(Boolean.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
        assertFalse(((BooleanGetter<Row>) getter).getBoolean(row));
    }


    @Test
    public void testGetDateOnNullCell() throws Exception {
        final Getter<Row, Date> getter = rowGetterFactory.newGetter(Date.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
    }

    @Test
    public void testGetEnumOnString() throws Exception {
        final Getter<Row, DbObject.Type> getter = rowGetterFactory.newGetter(DbObject.Type.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue("type1");
        assertEquals(DbObject.Type.type1, getter.get(row));
    }

    @Test
    public void testGetEnumOnNumber() throws Exception {
        final Getter<Row, DbObject.Type> getter = rowGetterFactory.newGetter(DbObject.Type.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(1);
        assertEquals(DbObject.Type.type2, getter.get(row));
    }
    @Test
    public void testGetEnumOnBoolean() throws Exception {
        final Getter<Row, DbObject.Type> getter = rowGetterFactory.newGetter(DbObject.Type.class, key, CsvColumnDefinition.IDENTITY);
        cell.setCellValue(true);
        try {
            getter.get(row);
            fail();
        } catch(Exception e) {

        }
    }

    @Test
    public void testGetEnumOnBlank() throws Exception {
        final Getter<Row, DbObject.Type> getter = rowGetterFactory.newGetter(DbObject.Type.class, blankCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
    }

    @Test
    public void testGetEnumOnNull() throws Exception {
        final Getter<Row, DbObject.Type> getter = rowGetterFactory.newGetter(DbObject.Type.class, noCellKey, CsvColumnDefinition.IDENTITY);
        assertNull(getter.get(row));
    }


}