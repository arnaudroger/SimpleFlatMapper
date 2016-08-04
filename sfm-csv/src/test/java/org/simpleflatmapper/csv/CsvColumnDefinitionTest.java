package org.simpleflatmapper.csv;


import org.junit.Test;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.TimeZone;

import static org.junit.Assert.*;

public class CsvColumnDefinitionTest {

    @Test
    public void testComposition() {

        TimeZone tz = TimeZone.getTimeZone("Europe/Brussels");
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
        final Predicate<PropertyMeta<?, ?>> appliesTo = new Predicate<PropertyMeta<?, ?>>() {
            @Override
            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                return false;
            }
        };
        CsvColumnDefinition compose = CsvColumnDefinition.identity().addDateFormat("yyyyMM").addRename("blop").addCustomReader(
                new CellValueReader<Integer>() {
                    @Override
                    public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                        return 3;
                    }

                    @Override
                    public String toString() {
                        return "CellValueReader";
                    }
                }).addCustomCellValueReaderFactory(cellValueReaderFactory).addTimeZone(tz).addKey(appliesTo);

        assertEquals("blop", compose.rename(new CsvColumnKey("bar", -1)).getName());
        assertArrayEquals(new String[] {"yyyyMM"}, compose.dateFormats());
        assertEquals(new Integer(3), compose.getCustomReader().read(null, 0, 0, null));
        assertEquals(cellValueReaderFactory, compose.getCustomCellValueReaderFactory());
        assertEquals(tz, compose.getTimeZone());

        assertTrue(compose.hasCustomSource());
        assertFalse(compose.ignore());
        assertEquals(Integer.class, compose.getCustomSourceReturnType());

        assertTrue(CsvColumnDefinition.identity().addIgnore().ignore());

        assertTrue(compose.isKey());
        assertSame(appliesTo, compose.keyAppliesTo());

        final String toString = compose.addIgnore().toString();
        assertTrue(toString.startsWith("ColumnDefinition{DateFormat{'yyyyMM'}," +
                " Rename{'blop'}, CustomReader{CellValueReader}," +
                " CellValueReaderFactory{CellValueReaderFactory}," +
                " TimeZone{Central European"));



    }
}
