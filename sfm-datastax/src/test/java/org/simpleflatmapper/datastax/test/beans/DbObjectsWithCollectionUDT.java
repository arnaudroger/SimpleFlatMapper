package org.simpleflatmapper.datastax.test.beans;

import java.util.Collection;

public class DbObjectsWithCollectionUDT {
    private long id;
    private Collection<MyType> l;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<MyType> getL() {
        return l;
    }

    public void setL(Collection<MyType> l) {
        this.l = l;
    }

    public static class MyType {
        public String str;
        public long l;
    }
}
