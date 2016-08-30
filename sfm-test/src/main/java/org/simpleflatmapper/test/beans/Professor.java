package org.simpleflatmapper.test.beans;

import java.util.List;

public interface Professor<T extends Student> extends Person {
    List<T> getStudents();
}
