package org.sfm.csv.impl;

import org.junit.Test;
import org.sfm.reflect.ExecutableInstantiatorDefinition;
import org.sfm.reflect.InstantiatorDefinition;
import org.sfm.reflect.Parameter;

import java.util.Date;

import static org.junit.Assert.*;

import static org.sfm.csv.impl.CellSetterFactory.*;

public class CellSetterFactoryTest {


    @Test
    public void testCompatibilityScorerStringGreaterThanAll() {
        assertTrue(score(String.class) == score(CharSequence.class));
        assertTrue(score(String.class) > score(Integer.class));
        assertTrue(score(String.class) > score(Date.class));
        assertTrue(score(String.class) > score(Object.class));
        assertTrue(score(String.class) > score(StringBuilder.class));
    }

    @Test
    public void testCompatibilityScorerNumberGreaterThanAll() {
        assertTrue(score(Integer.class) == score(Long.class));
        assertTrue(score(Integer.class) > score(Date.class));
        assertTrue(score(Integer.class) > score(Object.class));
    }

    private int score(Class<?> aClass) {
        return COMPATIBILITY_SCORER.score(newID(aClass));
    }

    private InstantiatorDefinition newID(Class<?> clazz) {
        return new ExecutableInstantiatorDefinition(null, new Parameter(0, "xx", clazz));
    }
}