package org.simpleflatmapper.datastax.test.beans;

import java.util.Map;

public class DbObjectsWithMapLongLong {
    private long id;
    private Map<Long, Long> ll;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public  Map<Long, Long> getLl() {
        return ll;
    }

    public void setLl( Map<Long, Long> ll) {
        this.ll = ll;
    }
}
