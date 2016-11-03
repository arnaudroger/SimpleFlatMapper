package org.simpleflatmapper.datastax.test.beans;

import java.util.Map;

public class DbObjectsWithMap {
    private long id;
    private Map<Integer, String> emails;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public  Map<Integer, String> getEmails() {
        return emails;
    }

    public void setEmails( Map<Integer, String> emails) {
        this.emails = emails;
    }
}
