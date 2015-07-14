package org.sfm.csv.bug;

import org.junit.Test;
import org.sfm.csv.CsvMapperFactory;
import org.sfm.csv.CsvParser;
import org.sfm.csv.impl.ParsingException;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;


public class Bug206 {

    final CsvParser.MapWithDSL<MyClass> dsl = CsvParser.mapWith(CsvMapperFactory.newInstance().useAsm(false).newMapper(MyClass.class));

    @Test
    public void testCanParseDateInSubObject() throws IOException, ParseException {
        MyClass o = dsl.iterator(new StringReader("id,p1_p2\n1,2014-01-01 11:11:11")).next();
        assertEquals(1, o.id);
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-01-01 11:11:11"), o.p1.p2);
    }

    @Test
    public void testErrorHandler() throws IOException, ParseException {
        try {
            dsl.iterator(new StringReader("id,p1_p2\n1,aaa")).next();
        } catch (ParsingException e) {
            // expected
        }
    }


    public static class MyClass {
        public long id;
        public MyClass2 p1;
    }

    public static class MyClass2 {
        public Date p2;
    }

}
