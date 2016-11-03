package org.simpleflatmapper.reflect.test;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.instantiator.InstantiatorDefinitions;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
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
        parameter = new Parameter(0, "i0", String.class);
    }

    public void valueOf() {
    }

    @Test
    public void testCompareConstructorSize() {
        assertCompare(new ExecutableInstantiatorDefinition(c, parameter), new ExecutableInstantiatorDefinition(c, parameter, parameter));
    }

    private void assertCompare(ExecutableInstantiatorDefinition i0, ExecutableInstantiatorDefinition i1) {
        assertTrue(InstantiatorDefinitions.COMPARATOR.compare(i0, i1) < 0);
        assertTrue(InstantiatorDefinitions.COMPARATOR.compare(i1, i0) > 0);
        assertTrue(InstantiatorDefinitions.COMPARATOR.compare(i1, i1) == 0);
    }


    @Test
    public void testCompareConstructorOverMethod() {
        assertCompare(new ExecutableInstantiatorDefinition(c, parameter, parameter), new ExecutableInstantiatorDefinition(valueOfMethod,parameter));
    }

    @Test
    public void testCompareMethodValueOfOverOther() {
        assertCompare(new ExecutableInstantiatorDefinition(valueOfMethod, parameter, parameter), new ExecutableInstantiatorDefinition(setUpMethod,parameter));
    }

    @Test
    public void testCompareMethodSize() {
        assertCompare(new ExecutableInstantiatorDefinition(valueOfMethod, parameter), new ExecutableInstantiatorDefinition(valueOfMethod,parameter, parameter));

    }

    @Test
    public void testLookForCompatibleOneArgumentReturnBestScore() {
        InstantiatorDefinition id1 = new ExecutableInstantiatorDefinition(c, parameter);
        InstantiatorDefinition id2 = new ExecutableInstantiatorDefinition(c, parameter);

        InstantiatorDefinition sid = InstantiatorDefinitions.lookForCompatibleOneArgument(
                Arrays.asList(id1, id2),
                new InstantiatorDefinitions.CompatibilityScorer() {
                    int i;
                    @Override
                    public int score(InstantiatorDefinition id) {
                        return i++;
                    }
                });
        assertSame(sid, id2);



        sid = InstantiatorDefinitions.lookForCompatibleOneArgument(
                Arrays.asList(id1, id2),
                new InstantiatorDefinitions.CompatibilityScorer() {
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
        InstantiatorDefinition id1 = new ExecutableInstantiatorDefinition(c, parameter);

        InstantiatorDefinition sid = InstantiatorDefinitions.lookForCompatibleOneArgument(
                Arrays.asList(id1),
                new InstantiatorDefinitions.CompatibilityScorer() {
                    @Override
                    public int score(InstantiatorDefinition id) {
                        return -1;
                    }
                });
        assertNull(sid);
    }

    @Test
    public void testDefaultCompatibilityScorers() {
        InstantiatorDefinitions.CompatibilityScorer compatibilityScorer = InstantiatorDefinitions.getCompatibilityScorer(new Object());
        assertEquals(0, compatibilityScorer.score(new ExecutableInstantiatorDefinition(null, new Parameter(0, "", InputStream.class))));
        assertEquals(1, compatibilityScorer.score(new ExecutableInstantiatorDefinition(null, new Parameter(0, "", Number.class))));
    }

    @Test
    public void testTypeAffinityCompatibilityScorers() {
        InstantiatorDefinitions.CompatibilityScorer compatibilityScorer = InstantiatorDefinitions.getCompatibilityScorer(new TypeAffinity() {
            @Override
            public Class<?>[] getAffinities() {
                return new Class<?>[] {Date.class, URL.class};
            }
        });
        assertEquals(0, compatibilityScorer.score(new ExecutableInstantiatorDefinition(null, new Parameter(0, "", InputStream.class))));
        assertEquals(12, compatibilityScorer.score(new ExecutableInstantiatorDefinition(null, new Parameter(0, "", Date.class))));
        assertEquals(11, compatibilityScorer.score(new ExecutableInstantiatorDefinition(null, new Parameter(0, "", URL.class))));
        assertEquals(1, compatibilityScorer.score(new ExecutableInstantiatorDefinition(null, new Parameter(0, "", Number.class))));
    }

}
