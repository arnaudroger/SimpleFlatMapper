package org.sfm.csv;


import org.junit.Test;
import org.sfm.csv.impl.ParsingContext;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.Predicate;

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
                }).addCustomCellValueReaderFactory(cellValueReaderFactory).addTimeZone(tz).addKey(appliesTo);

        assertEquals("blop", compose.rename(new CsvColumnKey("bar", -1)).getName());
        assertEquals("yyyyMM", compose.dateFormat());
        assertEquals(new Integer(3), compose.getCustomReader().read(null, 0, 0, null));
        assertEquals(cellValueReaderFactory, compose.getCustomCellValueReaderFactory());
        assertEquals(tz, compose.getTimeZone());

        assertTrue(compose.hasCustomSource());
        assertFalse(compose.ignore());
        assertEquals(Integer.class, compose.getCustomSourceReturnType());

        assertTrue(CsvColumnDefinition.IDENTITY.addIgnore().ignore());

        assertTrue(compose.isKey());
        assertSame(appliesTo, compose.keyAppliesTo());

        assertEquals("ColumnDefinition{DateFormat{'yyyyMM'}," +
                " Rename{'blop'}, CustomReader{CellValueReader}," +
                " CellValueReaderFactory{CellValueReaderFactory}," +
                " TimeZone{Central European Time}, Key{}, Ignore{}}", compose.addIgnore().toString());



    }
}
