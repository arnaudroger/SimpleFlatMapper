package org.simpleflatmapper.util.test;

import org.junit.Test;
import org.simpleflatmapper.util.RowHandler;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class RowHandlerTest {

    //IFJAVA8_START
    @Test
    public void testDefaultBridge() throws Exception {
        List<String> values = new ArrayList<String>();
        RowHandler<String> rh = new RowHandler<String>() {
            @Override
            public void accept(String s) throws Exception {
                values.add(s);
            }
        };

        rh.handle("test");

        assertArrayEquals(new String[] {"test"}, values.toArray(new String[0]));
    }
    //IFJAVA8_END

}