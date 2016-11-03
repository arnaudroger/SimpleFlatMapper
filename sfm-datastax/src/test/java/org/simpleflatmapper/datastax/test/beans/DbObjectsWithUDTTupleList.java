package org.simpleflatmapper.datastax.test.beans;

import org.simpleflatmapper.tuple.Tuple2;

import java.util.List;

public class DbObjectsWithUDTTupleList {
    private long id;
    private MyType t;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public MyType getT() {
        return t;
    }

    public void setT(MyType t) {
        this.t = t;
    }


    public static class MyType {
        public String str;
        public Tuple2<Long, List<Integer>> t;
    }
}
