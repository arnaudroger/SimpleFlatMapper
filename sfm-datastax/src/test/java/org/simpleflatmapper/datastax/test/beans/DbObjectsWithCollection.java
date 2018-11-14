package org.simpleflatmapper.datastax.test.beans;

import java.util.Collection;

public class DbObjectsWithCollection {
    private long id;
    private Collection<String> emails;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Collection<String> getEmails() {
        return emails;
    }

    public void setEmails(Collection<String> emails) {
        this.emails = emails;
    }
}
