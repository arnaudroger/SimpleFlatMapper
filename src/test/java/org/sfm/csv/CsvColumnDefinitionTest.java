package org.sfm.csv;


import org.junit.Test;
import org.sfm.csv.impl.ParsingContext;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CsvColumnDefinitionTest {

    @Test
    public void testComposition() {

        CellValueReaderFactory cellValueReaderFactory = new CellValueReaderFactory() {
            @Override
            public <P> CellValueReader getReader(Type propertyType, int index, CsvColumnDefinition columnDefinition) {
                return null;
            }
        };
        CsvColumnDefinition compose = CsvColumnDefinition.IDENTITY.addDateFormat("yyyyMM").addRename("blop").addCustomReader(
                new CellValueReader<Integer>() {
                    @Override
                    public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                        return 3;
                    }
                }).addCustomCellValueReaderFactory(cellValueReaderFactory);

        assertEquals("blop", compose.rename(new CsvColumnKey("bar", -1)).getName());
        assertEquals("yyyyMM", compose.dateFormat());
        assertEquals(new Integer(3), compose.getCustomReader().read(null, 0, 0 , null));
        assertEquals(cellValueReaderFactory, compose.getCustomCellValueReaderFactory());

        assertTrue(compose.hasCustomSource());
        assertEquals(Integer.class, compose.getCustomSourceReturnType());
    }
}
