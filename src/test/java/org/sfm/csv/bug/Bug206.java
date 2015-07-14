package org.sfm.csv.bug;

import org.junit.Test;
import org.sfm.csv.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;


public class Bug206 {

    @Test
    public void testCanParseDateInSubObject() throws IOException, ParseException {
        CsvParser.MapWithDSL<MyClass> csvparser = CsvParser.mapTo(MyClass.class);


        MyClass o = csvparser.iterator(new StringReader("id,p1_p2\n1,2014-01-01 11:11:11")).next();

        assertEquals(1, o.id);
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-01-01 11:11:11"), o.p1.p2);
    }


    public static class MyClass {
        public long id;
        public MyClass2 p1;
    }

    public static class MyClass2 {
        public Date p2;
    }

}
