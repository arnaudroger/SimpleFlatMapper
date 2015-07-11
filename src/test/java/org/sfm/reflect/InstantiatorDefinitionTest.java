package org.sfm.reflect;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
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

    @Test
    public void testLookForCompatibleOneArgumentReturnBestScore() {
        InstantiatorDefinition id1 = new InstantiatorDefinition(c, parameter);
        InstantiatorDefinition id2 = new InstantiatorDefinition(c, parameter);

        InstantiatorDefinition sid = InstantiatorDefinition.lookForCompatibleOneArgument(
                Arrays.asList(id1, id2),
                new InstantiatorDefinition.CompatibilityScorer() {
                    int i;
                    @Override
                    public int score(InstantiatorDefinition id) {
                        return i++;
                    }
                });
        assertSame(sid, id2);



        sid = InstantiatorDefinition.lookForCompatibleOneArgument(
                Arrays.asList(id1, id2),
                new InstantiatorDefinition.CompatibilityScorer() {
                    int i = 10;
                    @Override
                    public int score(InstantiatorDefinition id) {
                        return i--;
                    }
                });

        assertSame(sid, id1);
    }

    @Test
    public void testLookForCompatibleOneArgumentIgnoreNegativeScore() {
        InstantiatorDefinition id1 = new InstantiatorDefinition(c, parameter);

        InstantiatorDefinition sid = InstantiatorDefinition.lookForCompatibleOneArgument(
                Arrays.asList(id1),
                new InstantiatorDefinition.CompatibilityScorer() {
                    @Override
                    public int score(InstantiatorDefinition id) {
                        return -1;
                    }
                });
        assertNull(sid);
    }

}
