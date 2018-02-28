package org.simpleflatmapper.csv.test.bug;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.util.ListCollector;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Issue492Test {
    public class Foo {
        int id;

        public int getId() {
            return id;
        }

        public Foo setId(int id) {
            this.id = id;
            return this;
        }

    }


    @Test
    public void testIssue() throws IOException {
        ;
        
        List<Foo> foos = CsvParser
                .mapWith(CsvMapperFactory.newInstance().useAsm(true).newBuilder(Foo.class).addMapping("id").mapper())
                .forEach("1\n2", new ListCollector<Foo>()).getList();
        
        assertEquals(2, foos.size());
        assertEquals(1, foos.get(0).id);
        assertEquals(2, foos.get(1).id);
        
    }
}
