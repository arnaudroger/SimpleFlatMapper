package org.simpleflatmapper.csv.test.bug;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;

import java.io.IOException;
import java.io.StringReader;
import java.util.Iterator;

import static org.junit.Assert.assertEquals;

public class Issue437 {
    
    public class A {
        private String a;

        public A(String a) {
            this.a = a;
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }
    }
    
    @Test
    public void testAIsSetProperly() throws IOException {

        CsvMapper<A> csvMapper = CsvMapperFactory.newInstance().useAsm(false).newBuilder(A.class).addMapping("a").mapper();

        Iterator<A> iterator = csvMapper.iterator(new StringReader("b"));
        
        A a = iterator.next();
                
        assertEquals("b", a.a);
    }
    
}
