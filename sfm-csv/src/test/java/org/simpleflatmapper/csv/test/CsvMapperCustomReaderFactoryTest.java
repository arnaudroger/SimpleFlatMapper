package org.simpleflatmapper.csv.test;


import org.junit.Test;
import org.simpleflatmapper.csv.CellValueReader;
import org.simpleflatmapper.csv.CellValueReaderFactory;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.ParsingContext;
import org.simpleflatmapper.csv.ParsingContextFactoryBuilder;
import org.simpleflatmapper.test.beans.DbObject;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CsvMapperCustomReaderFactoryTest {

    @Test
    public void testCustomReaderFactory() throws IOException {
        CsvMapper<DbObject> mapper = CsvMapperFactory.newInstance().failOnAsm(true).cellValueReaderFactory(new CellValueReaderFactory() {
            @Override
            public <P> CellValueReader<P> getReader(Type propertyType, int index, CsvColumnDefinition columnDefinition, ParsingContextFactoryBuilder builder) {
                return new CellValueReader<P>() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public P read(char[] chars, int offset, int length, ParsingContext parsingContext) {
                        return (P) "Hello!";
                    }
                };
            }
        }).newBuilder(DbObject.class).addMapping("name").mapper();

        DbObject bop = mapper.iterator(new StringReader("bop")).next();

        assertEquals("Hello!", bop.getName());


    }
}
