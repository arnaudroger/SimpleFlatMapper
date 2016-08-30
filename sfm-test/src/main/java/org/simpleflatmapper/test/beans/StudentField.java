package org.simpleflatmapper.test.beans;

import java.util.List;

public class StudentField implements Student {
    public int id;
    public String name;
    public List<String> phones;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<String> getPhones() {
        return phones;
    }
}
