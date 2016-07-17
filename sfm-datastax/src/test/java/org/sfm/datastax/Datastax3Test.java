package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import org.junit.Before;
import org.junit.Test;
import org.sfm.datastax.utils.Datastax3ClassLoaderUtil;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Datastax3Test {

    @Before
    public void setUp() throws IOException, ClassNotFoundException {
        classLoader = Datastax3ClassLoaderUtil.getDatastax3ClassLoader();
        dataTypeHelper = classLoader.loadClass(DataTypeHelper.class.getName());
        dataTypeName = classLoader.loadClass(DataType.Name.class.getName());
        localDate = classLoader.loadClass("com.datastax.driver.core.LocalDate");
    }

    ClassLoader classLoader;
    Class<?> dataTypeHelper;
    Class dataTypeName;
    Class localDate;

    @Test
    public void testDataTypeHelperLocalDateClassSet() throws NoSuchFieldException, IllegalAccessException {
        Class<?> localDateClass = (Class<?>) dataTypeHelper.getField("localDateClass").get(null);
        assertNotNull(localDateClass);
        assertEquals(localDate, localDateClass);
    }

    @Test
    public void testDatastax3NewDataTypeAsJavaClass() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertDataTypeNameIsMapToExpectedType(Long.class, "TIME");
        assertDataTypeNameIsMapToExpectedType(Short.class, "SMALLINT");
        assertDataTypeNameIsMapToExpectedType(Byte.class, "TINYINT");
        assertDataTypeNameIsMapToExpectedType(localDate, "DATE");
    }

    @Test
    public void testAllDataTypeNameHaveAnAssignedType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object values = dataTypeName.getDeclaredMethod("values").invoke(null);
        Method asJavaClass = dataTypeHelper.getDeclaredMethod("asJavaClass", dataTypeName);

        for(int i = 0; i < Array.getLength(values); i++) {
            Object enumValue = Array.get(values, i);
            assertNotNull(asJavaClass.invoke(null, enumValue));
        }
    }

    @Test
    public void testDataTypeHelperIsNumberOnDatastax3ExtraType() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        assertDataTypeNameIsNumber(true, "TIME");
        assertDataTypeNameIsNumber(true, "SMALLINT");
        assertDataTypeNameIsNumber(true, "TINYINT");
        assertDataTypeNameIsNumber(false, "DATE");

    }

    private void assertDataTypeNameIsNumber(boolean expected, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object eName = Enum.valueOf(dataTypeName, name);
        Method asJavaClass = dataTypeHelper.getDeclaredMethod("isNumber", dataTypeName);
        assertEquals(expected, asJavaClass.invoke(null, eName));
    }

    private void assertDataTypeNameIsMapToExpectedType(Class<?> expectedType, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object eName = Enum.valueOf(dataTypeName, name);
        Method asJavaClass = dataTypeHelper.getDeclaredMethod("asJavaClass", dataTypeName);
        assertEquals(expectedType, asJavaClass.invoke(null, eName));
    }
}
