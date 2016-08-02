package org.simpleflatmapper.osgi;

public class TestClass {
    private final String name;
    private final int id;


    public TestClass(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TestClass{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
