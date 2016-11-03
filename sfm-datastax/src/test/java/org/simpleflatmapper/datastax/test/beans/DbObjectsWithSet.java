package org.simpleflatmapper.datastax.test.beans;

import java.util.Set;

public class DbObjectsWithSet {
    private long id;
    private Set<String> emails;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }
}
