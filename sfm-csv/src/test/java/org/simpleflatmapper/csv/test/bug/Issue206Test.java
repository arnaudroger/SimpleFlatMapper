package org.simpleflatmapper.csv.test.bug;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.csv.CsvWriter;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;

public class Issue206Test {

    final CsvParser.MapWithDSL<MyClass> dsl = CsvParser.mapWith(CsvMapperFactory.newInstance().useAsm(false).newMapper(MyClass.class));

    @Test
    public void testCanParseDateInSubObject() throws IOException, ParseException {
        MyClass o = dsl.iterator(new StringReader("id,p1_p2\n1,2014-01-01 11:11:11")).next();
        assertEquals(1, o.id);
        assertEquals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-01-01 11:11:11"), o.p1.p2);
    }

    @Test
    public void testCanWriteDateInSubObject() throws IOException, ParseException {
        StringBuilder sb = new StringBuilder();
        MyClass myObject = new MyClass();
        myObject.id = 1;
        myObject.p1 = new MyClass2();
        myObject.p1.p2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2014-01-01 11:11:11");

        CsvWriter.from(MyClass.class).to(sb).append(myObject);

        assertEquals("id,p1_p2\r\n1,2014-01-01 11:11:11\r\n", sb.toString());
    }

    @Test
    public void testErrorHandler() throws Exception {
        try {
            dsl.iterator(new StringReader("id,p1_p2\n1,aaa")).next();
        } catch (Exception e) {
            if (!(e instanceof ParseException))  {
                throw e;
            }
            
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
