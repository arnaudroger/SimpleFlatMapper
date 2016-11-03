package org.simpleflatmapper.datastax.test.beans;

import java.util.Map;

public class DbObjectsWithMapUDT {
    private long id;
    private Map<Integer, MyType> l;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Map<Integer, MyType> getL() {
        return l;
    }

    public void setL(Map<Integer, MyType> l) {
        this.l = l;
    }

    public static class MyType {
        public String str;
        public long l;
    }
}
