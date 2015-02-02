package org.sfm.csv;

import org.junit.Test;
import org.sfm.tuples.Tuple2;
import org.sfm.tuples.Tuples;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class CsvMappetDateFormatDefinition {


    String df1 = "yyyyMMdd";
    String df2 = "MMyydd";

    @Test
    public void testCustomDateFormatOnBuilder() throws IOException, ParseException {

        CsvMapperBuilder<Tuple2<Date, Date>> builder = CsvMapperFactory
                .newInstance()
                .newBuilder(Tuples.typeDef(Date.class, Date.class));
        builder
                .addMapping("date0", CsvColumnDefinition.dateFormatDefinition(df1))
                .addMapping("date1", CsvColumnDefinition.dateFormatDefinition(df2));

        CsvMapper<Tuple2<Date, Date>> mapper = builder.mapper();

        Tuple2<Date, Date> next = mapper.iterator(new StringReader("20140909,091409")).next();

        assertEquals(new SimpleDateFormat(df1).parse("20140909"), next.first());
        assertEquals(new SimpleDateFormat(df2).parse("091409"), next.second());
    }

    @Test
    public void testCustomDateFormatOnFactory() throws IOException, ParseException {

        CsvMapperFactory csvMapperFactory = CsvMapperFactory.newInstance();

        csvMapperFactory.addColumnDefinition("date0", CsvColumnDefinition.dateFormatDefinition(df1));
        csvMapperFactory.addColumnDefinition("date1", CsvColumnDefinition.dateFormatDefinition(df2));

        CsvMapper<Tuple2<Date, Date>> mapper = csvMapperFactory.newMapper(Tuples.typeDef(Date.class, Date.class));

        Tuple2<Date, Date> next = mapper.iterator(new StringReader("date0,date1\n20140909,091409")).next();

        assertEquals(new SimpleDateFormat(df1).parse("20140909"), next.first());
        assertEquals(new SimpleDateFormat(df2).parse("091409"), next.second());
    }
}
