package org.simpleflatmapper.datastax.test.beans;

import com.datastax.driver.core.UDTValue;

public class DbObjectsWithUDTValue {
    private long id;
    private UDTValue t;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UDTValue getT() {
        return t;
    }

    public void setT(UDTValue t) {
        this.t = t;
    }
}
