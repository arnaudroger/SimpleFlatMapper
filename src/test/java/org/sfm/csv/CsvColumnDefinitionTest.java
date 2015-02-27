package org.sfm.csv;


import org.junit.Test;
import org.sfm.csv.impl.ParsingContext;

import java.lang.reflect.Type;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CsvColumnDefinitionTest {

    @Test
    public void testComposition() {

        TimeZone tz = TimeZone.getTimeZone("Europe/Brussel");
        CellValueReaderFactory cellValueReaderFactory = new CellValueReaderFactory() {
            @Override
            public <P> CellValueReader<P> getReader(Type propertyType, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder contextFactoryBuilder) {
                return null;
            }

            @Override
            public String toString() {
                return "CellValueReaderFactory";
            }
        };
        CsvColumnDefinition compose = CsvColumnDefinition.IDENTITY.addDateFormat("yyyyMM").addRename("blop").addCustomReader(
                new CellValueReader<Integer>() {
                    @Override
                    public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                        return 3;
                    }

                    @Override
                    public String toString() {
                        return "CellValueReader";
                    }
                }).addCustomCellValueReaderFactory(cellValueReaderFactory).addTimeZone(tz);

        assertEquals("blop", compose.rename(new CsvColumnKey("bar", -1)).getName());
        assertEquals("yyyyMM", compose.dateFormat());
        assertEquals(new Integer(3), compose.getCustomReader().read(null, 0, 0, null));
        assertEquals(cellValueReaderFactory, compose.getCustomCellValueReaderFactory());
        assertEquals(tz, compose.getTimeZone());

        assertTrue(compose.hasCustomSource());
        assertFalse(compose.ignore());
        assertEquals(Integer.class, compose.getCustomSourceReturnType());

        assertTrue(CsvColumnDefinition.IDENTITY.addIgnore().ignore());

        assertEquals("ColumnDefinition{DateFormat{'yyyyMM'}," +
                " Rename{'blop'}, CustomReader{CellValueReader}," +
                " CellValueReaderFactory{CellValueReaderFactory}," +
                " TimeZone{Greenwich Mean Time}, Ignore{}}", compose.addIgnore().toString());
    }
}
