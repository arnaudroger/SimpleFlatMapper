package org.simpleflatmapper.test.beans;

import org.simpleflatmapper.test.beans.Professor;
import org.simpleflatmapper.test.beans.StudentGS;

import java.util.List;

public class ProfessorGS implements Professor<StudentGS> {
    private int id;
    private String name;
    private List<StudentGS> students;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StudentGS> getStudents() {
        return students;
    }

    public void setStudents(List<StudentGS> students) {
        this.students = students;
    }
}
