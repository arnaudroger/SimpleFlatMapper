package org.simpleflatmapper.test.beans;

import org.simpleflatmapper.test.beans.Professor;
import org.simpleflatmapper.test.beans.StudentField;

import java.util.List;

public class ProfessorField implements Professor<StudentField> {
    public int id;
    public String name;
    public List<StudentField> students;

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<StudentField> getStudents() {
        return students;
    }
}
