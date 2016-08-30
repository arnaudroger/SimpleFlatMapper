package org.simpleflatmapper.test.beans;

import java.util.List;

public class ProfessorC implements Professor<StudentC> {
    private final int id;
    private final String name;
    private final List<StudentC> students;

    public ProfessorC(int id, String name, List<StudentC> students) {
        this.id = id;
        this.name = name;
        this.students = students;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public List<StudentC> getStudents() {
        return students;
    }

}
