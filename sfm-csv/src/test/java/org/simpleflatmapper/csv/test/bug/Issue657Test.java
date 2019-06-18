package org.simpleflatmapper.csv.test.bug;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapperFactory;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class Issue657Test {


    @Test
    public void test() throws IOException, ParseException {
        String dateFormat = "yyyy-MM-dd";
        Date expected = new SimpleDateFormat(dateFormat).parse("2019-06-18");

        CsvMapperFactory csvMapperFactory = CsvMapperFactory.newInstance().defaultDateFormat(dateFormat);

        assertEquals(expected, csvMapperFactory.newMapper(MyObject.class).iterator(new StringReader("date\n2019-06-18")).next().date);
        assertEquals(expected, csvMapperFactory.newErrorCollectingMapper(MyObject.class).iterator(new StringReader("date\n2019-06-18")).next().getValue().date);
    }


    public static class MyObject {
        public Date date;
    }
}
