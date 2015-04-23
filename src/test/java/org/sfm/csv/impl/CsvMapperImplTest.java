package org.sfm.csv.impl;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.csv.CellValueReader;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.CsvMapperFactory;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.io.StringReader;

import static org.junit.Assert.*;

public class CsvMapperImplTest {


    @Test
    public void testAsmFactoryTest() throws IOException {
        final CsvMapperImpl<DbObject> mapper1 = (CsvMapperImpl<DbObject>) CsvMapperFactory.newInstance().newBuilder(DbObject.class).addMapping("id").addMapping("name").addMapping("email").mapper();
        final CsvMapperImpl<DbObject> mapper2 = (CsvMapperImpl<DbObject>) CsvMapperFactory.newInstance().newBuilder(DbObject.class).addMapping("id", CsvColumnDefinition.customReaderDefinition(new CellValueReader<Long>() {
            @Override
            public Long read(CharSequence value, ParsingContext parsingContext) {
                return 23l;
            }
        })).addMapping("name").addMapping("email").mapper();
        final CsvMapperImpl<DbObject> mapper3 = (CsvMapperImpl<DbObject>) CsvMapperFactory.newInstance().newBuilder(DbObject.class).addMapping("id").addMapping("name").addMapping("creationTime").mapper();

        final RowHandler<DbObject> handler = new RowHandler<DbObject>() {
            @Override
            public void handle(DbObject dbObject) throws Exception {

            }
        };

        assertSame(mapper1.csvMapperCellHandlerFactory.getClass(), mapper2.csvMapperCellHandlerFactory.getClass());
        assertNotSame(mapper1.csvMapperCellHandlerFactory.getClass(), mapper3.csvMapperCellHandlerFactory.getClass());

        assertTrue(mapper1.csvMapperCellHandlerFactory.getClass().getSimpleName().startsWith("AsmCsvMapperCellHandlerTo"));
        assertTrue(mapper2.csvMapperCellHandlerFactory.getClass().getSimpleName().startsWith("AsmCsvMapperCellHandlerTo"));
        assertTrue(mapper3.csvMapperCellHandlerFactory.getClass().getSimpleName().startsWith("AsmCsvMapperCellHandlerTo"));


        assertEquals(2l, mapper1.iterator(new StringReader("2")).next().getId());
        assertEquals(23l, mapper2.iterator(new StringReader("2")).next().getId());
    }


}
