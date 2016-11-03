package org.simpleflatmapper.datastax.test.beans;

import java.util.Collection;

public class DbObjectsWithCollectionLong {
    private long id;
    private Collection<Long> l;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<Long> getL() {
        return l;
    }

    public void setL(Collection<Long> l) {
        this.l = l;
    }
}
