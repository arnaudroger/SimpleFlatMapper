package org.simpleflatmapper.csv.test;


import org.junit.Test;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CellValueReaderFactory;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.ParsingContextFactoryBuilder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ConstantPredicate;
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
        final Predicate<PropertyMeta<?, ?>> appliesTo = ConstantPredicate.falsePredicate();
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
        assertEquals("blop", CsvColumnDefinition.renameDefinition("blop").rename(new CsvColumnKey("bar", -1)).getName());
        assertArrayEquals(new String[] {"yyyyMM"}, compose.dateFormats());
        assertEquals(new Integer(3), compose.getCustomReader().read(null, 0, 0, null));
        assertEquals(cellValueReaderFactory, compose.getCustomCellValueReaderFactory());
        assertEquals(tz, compose.getTimeZone());
        assertEquals(tz, CsvColumnDefinition.timeZoneDefinition(tz).getTimeZone());

        assertTrue(compose.hasCustomSourceFrom(Object.class));
        assertFalse(compose.ignore());
        assertEquals(Integer.class, compose.getCustomSourceReturnTypeFrom(Object.class));

        assertTrue(CsvColumnDefinition.identity().addIgnore().ignore());

        assertTrue(compose.isKey());

        assertTrue(CsvColumnDefinition.key(appliesTo).isKey());
        assertSame(appliesTo, compose.keyAppliesTo());

        final String toString = compose.addIgnore().toString();
        System.out.println("toString = " + toString);

    }


    @Test
    public void testIdentity() {
        CsvColumnDefinition identity = CsvColumnDefinition.identity();
        try {
            identity.dateFormats();
            fail();
        } catch (IllegalStateException e) {
        }

        assertNull(identity.getCustomCellValueReaderFactory());
        assertNull(identity.getCustomReader());
    }

}
