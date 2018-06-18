package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvColumnDefinition;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperBuilder;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuples;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static org.junit.Assert.assertEquals;

public class CsvMapperDateFormatDefinitionTest {


    String df1 = "yyyyMMdd";
    String df2 = "MMyydd";


    TimeZone tz1 = TimeZone.getTimeZone("America/Los_Angeles");
    TimeZone tz2 = TimeZone.getTimeZone("Europe/Berlin");

    SimpleDateFormat sdf1 = new SimpleDateFormat(df1);
    SimpleDateFormat sdf2 = new SimpleDateFormat(df2);

    public CsvMapperDateFormatDefinitionTest() {
        sdf1.setTimeZone(tz1);
        sdf2.setTimeZone(tz2);
    }
    @Test
    public void testCustomDateFormatOnBuilder() throws IOException, ParseException {

        CsvMapperBuilder<Tuple2<Date, Date>> builder = CsvMapperFactory
                .newInstance()
                .newBuilder(Tuples.typeDef(Date.class, Date.class));
        builder
                .addMapping("date0", CsvColumnDefinition.dateFormatDefinition(df1).addTimeZone(tz1))
                .addMapping("date1", CsvColumnDefinition.dateFormatDefinition(df2).addTimeZone(tz2));

        CsvMapper<Tuple2<Date, Date>> mapper = builder.mapper();

        Tuple2<Date, Date> next = mapper.iterator(new StringReader("20140909,091409")).next();

        assertEquals(sdf1.parse("20140909"), next.first());
        assertEquals(sdf2.parse("091409"), next.second());
    }

    @Test
    public void testCustomDateFormatOnFactory() throws IOException, ParseException {

        CsvMapperFactory csvMapperFactory = CsvMapperFactory.newInstance();

        csvMapperFactory.addColumnDefinition("date0", CsvColumnDefinition.dateFormatDefinition(df1).addTimeZone(tz1));
        csvMapperFactory.addColumnDefinition("date1", CsvColumnDefinition.dateFormatDefinition(df2).addTimeZone(tz2));

        CsvMapper<Tuple2<Date, Date>> mapper = csvMapperFactory.newMapper(Tuples.typeDef(Date.class, Date.class));

        Tuple2<Date, Date> next = mapper.iterator(new StringReader("date0,date1\n20140909,091409")).next();


        assertEquals(sdf1.parse("20140909"), next.first());
        assertEquals(sdf2.parse("091409"), next.second());
    }
}
