package org.sfm.datastax.beans;

import java.util.List;

public class DbObjectsWithListLong {
    private long id;
    private List<Long> l;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<Long> getL() {
        return l;
    }

    public void setL(List<Long> l) {
        this.l = l;
    }
}
