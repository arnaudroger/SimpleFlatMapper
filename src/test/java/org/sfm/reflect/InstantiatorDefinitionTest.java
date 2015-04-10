package org.sfm.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;

public class InstantiatorDefinitionTest {


    Constructor c;
    Method setUpMethod;
    Method valueOfMethod;
    Parameter parameter;

    @Before
    public void setUp() throws Exception {
        c = InstantiatorDefinitionTest.class.getConstructor();
        setUpMethod = InstantiatorDefinitionTest.class.getMethod("setUp");
        valueOfMethod = InstantiatorDefinitionTest.class.getMethod("valueOf");
        parameter = new Parameter("i0", String.class);
    }

    public void valueOf() {
    }

    @Test
    public void testCompareConstructorSize() {
        assertCompare(new InstantiatorDefinition(c, parameter), new InstantiatorDefinition(c, parameter, parameter));
    }

    private void assertCompare(InstantiatorDefinition i0, InstantiatorDefinition i1) {
        assertTrue(i0.compareTo(i1) < 0);
        assertTrue(i1.compareTo(i0) > 0);
        assertTrue(i1.compareTo(i1) == 0);
    }


    @Test
    public void testCompareConstructorOverMethod() {
        assertCompare(new InstantiatorDefinition(c, parameter, parameter), new InstantiatorDefinition(valueOfMethod,parameter));
    }

    @Test
    public void testCompareMethodValueOfOverOther() {
        assertCompare(new InstantiatorDefinition(valueOfMethod, parameter, parameter), new InstantiatorDefinition(setUpMethod,parameter));
    }

    @Test
    public void testCompareMethodSize() {
        assertCompare(new InstantiatorDefinition(valueOfMethod, parameter), new InstantiatorDefinition(valueOfMethod,parameter, parameter));

    }
}
