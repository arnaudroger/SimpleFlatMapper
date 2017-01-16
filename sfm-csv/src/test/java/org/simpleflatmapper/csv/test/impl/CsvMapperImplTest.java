package org.simpleflatmapper.csv.test.impl;

import org.junit.Test;
import org.simpleflatmapper.csv.impl.CsvMapperImpl;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.util.CheckedConsumer;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class CsvMapperImplTest {


    @Test
    public void testAsmFactoryTest() throws IOException {
        final CsvMapperImpl<DbObject> mapper1 = (CsvMapperImpl<DbObject>) CsvMapperFactory.newInstance().newBuilder(DbObject.class).addMapping("id").addMapping("name").addMapping("email").mapper();
        final CsvMapperImpl<DbObject> mapper2 = (CsvMapperImpl<DbObject>) CsvMapperFactory.newInstance().newBuilder(DbObject.class).addMapping("id", CsvColumnDefinition.customReaderDefinition(new CellValueReader<Long>() {
            @Override
            public Long read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                return 23l;
            }
        })).addMapping("name").addMapping("email").mapper();
        final CsvMapperImpl<DbObject> mapper3 = (CsvMapperImpl<DbObject>) CsvMapperFactory.newInstance().newBuilder(DbObject.class).addMapping("id").addMapping("name").addMapping("creationTime").mapper();

        final CheckedConsumer<DbObject> handler = new CheckedConsumer<DbObject>() {
            @Override
            public void accept(DbObject dbObject) throws Exception {

            }
        };

        assertSame(mapper1.csvMapperCellHandlerFactory().getClass(), mapper2.csvMapperCellHandlerFactory().getClass());
        assertNotSame(mapper1.csvMapperCellHandlerFactory().getClass(), mapper3.csvMapperCellHandlerFactory().getClass());

        assertTrue(mapper1.csvMapperCellHandlerFactory().getClass().getSimpleName().startsWith("AsmCsvMapperCellHandlerTo"));
        assertTrue(mapper2.csvMapperCellHandlerFactory().getClass().getSimpleName().startsWith("AsmCsvMapperCellHandlerTo"));
        assertTrue(mapper3.csvMapperCellHandlerFactory().getClass().getSimpleName().startsWith("AsmCsvMapperCellHandlerTo"));


        assertEquals(2l, mapper1.iterator(new StringReader("2")).next().getId());
        assertEquals(23l, mapper2.iterator(new StringReader("2")).next().getId());
    }


}
