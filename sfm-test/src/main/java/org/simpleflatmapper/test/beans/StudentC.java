package org.simpleflatmapper.test.beans;

import java.util.List;

public class StudentC implements Student {
    private final int id;
    private final String name;
    private final List<String> phones;

    public StudentC(int id, String name, List<String> phones) {
        this.id = id;
        this.name = name;
        this.phones = phones;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<String> getPhones() {
        return phones;
    }
}
