package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sfm.reflect.ReflectionService;
import org.sfm.utils.LibrarySet;
import org.sfm.utils.MultiClassLoaderJunitRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MultiClassLoaderJunitRunner.class)
@LibrarySet(libraryGroups = {"http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.0.3/cassandra-driver-core-3.0.3.jar"},
        includes={ReflectionService.class, DatastaxCrud.class, DatastaxCrudTest.class})
public class Datastax3Test {

    private Class<?> localDateClass;

    @Before
    public void setUp() throws IOException, ClassNotFoundException {
        localDateClass = getClass().getClassLoader().loadClass("com.datastax.driver.core.LocalDate");
    }

    @Test
    public void testDataTypeHelperLocalDateClassSet() throws NoSuchFieldException, IllegalAccessException {
        assertNotNull(DataTypeHelper.localDateClass);
        assertEquals(this.localDateClass, localDateClass);
    }

    @Test
    public void testDatastax3NewDataTypeAsJavaClass() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertDataTypeNameIsMapToExpectedType(Long.class, "TIME");
        assertDataTypeNameIsMapToExpectedType(Short.class, "SMALLINT");
        assertDataTypeNameIsMapToExpectedType(Byte.class, "TINYINT");
        assertDataTypeNameIsMapToExpectedType(localDateClass, "DATE");
    }


    @Test
    public void testDataTypeHelperIsNumberOnDatastax3ExtraType() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertDataTypeNameIsNumber(true, "TIME");
        assertDataTypeNameIsNumber(true, "SMALLINT");
        assertDataTypeNameIsNumber(true, "TINYINT");
        assertDataTypeNameIsNumber(false, "DATE");

    }

    private void assertDataTypeNameIsNumber(boolean expected, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertEquals(expected, DataTypeHelper.isNumber(DataType.Name.valueOf(name)));
    }

    private void assertDataTypeNameIsMapToExpectedType(Class<?> expectedType, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertEquals(expectedType, DataTypeHelper.asJavaClass(DataType.Name.valueOf(name)));
    }

}
