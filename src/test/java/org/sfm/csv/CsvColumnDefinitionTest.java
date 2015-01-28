package org.sfm.csv;


import org.junit.Test;
import org.sfm.csv.impl.ParsingContext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CsvColumnDefinitionTest {

    @Test
    public void testComposition() {

        CsvColumnDefinition compose = CsvColumnDefinition.compose(CsvColumnDefinition.compose(CsvColumnDefinition.dateFormatDefinition("yyyyMM"), CsvColumnDefinition.renameDefinition("blop")),
                CsvColumnDefinition.customReaderDefinition(new CellValueReader<Integer>() {
                    @Override
                    public Integer read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                        return 3;
                    }
                }));

        assertEquals("blop", compose.rename(new CsvColumnKey("bar", -1)).getName());
        assertEquals("yyyyMM", compose.dateFormat());
        assertEquals(new Integer(3), compose.getCustomReader().read(null, 0, 0 , null));

        assertTrue(compose.hasCustomSource());
        assertEquals(Integer.class, compose.getCustomSourceReturnType());
    }
}
