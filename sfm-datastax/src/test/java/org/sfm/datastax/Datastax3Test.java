package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import org.junit.Before;
import org.junit.Test;
import org.sfm.datastax.utils.Datastax3ClassLoaderUtil;

import java.io.IOException;
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
    public void testDataTypeHelperType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        testType(Long.class, "TIME");
        testType(Short.class, "SMALLINT");
        testType(Byte.class, "TINYINT");
        testType(localDate, "DATE");
    }

    private void testType(Class<?> expectedType, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object eName = Enum.valueOf(dataTypeName, name);

        Method asJavaClass = dataTypeHelper.getDeclaredMethod("asJavaClass", dataTypeName);

        assertEquals(expectedType, asJavaClass.invoke(null, eName));

    }
}
