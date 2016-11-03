package org.simpleflatmapper.datastax.test.beans;

import java.util.List;

public class DbObjectsWithList {
    private long id;
    private List<String> emails;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public List<String> getEmails() {
        return emails;
    }

    public void setEmails(List<String> emails) {
        this.emails = emails;
    }
}
