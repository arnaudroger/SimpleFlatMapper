package org.simpleflatmapper.datastax.test.beans;

import org.simpleflatmapper.tuple.Tuple3;

public class DbObjectsWithTuple {
    private long id;
    private Tuple3<String, Long, Integer> t;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Tuple3<String, Long, Integer> getT() {
        return t;
    }

    public void setT(Tuple3<String, Long, Integer> t) {
        this.t = t;
    }
}
