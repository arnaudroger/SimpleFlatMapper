package org.simpleflatmapper.datastax.test.beans;

import com.datastax.driver.core.TupleValue;

public class DbObjectsWithTupleValue {
    private long id;
    private TupleValue t;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public TupleValue getT() {
        return t;
    }

    public void setT(TupleValue t) {
        this.t = t;
    }
}
