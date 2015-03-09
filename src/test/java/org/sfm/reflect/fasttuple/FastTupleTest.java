package org.sfm.reflect.fasttuple;

import com.boundary.tuple.FastTuple;
import com.boundary.tuple.TupleSchema;
import org.junit.Test;

import java.util.Date;

public class FastTupleTest {



    @Test
    public void test() throws Exception {
        TupleSchema schema = TupleSchema.builder().
                addField("fieldA", Long.TYPE).
                addField("fieldB", Integer.TYPE).
                addField("fieldC", Short.TYPE).
                heapMemory().
                build();

        //creates a new tuple allocated on the JVM heap
        FastTuple tuple = schema.createTuple();

        System.out.println(tuple.getClass().getMethods());

    }
}
