package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.junit.Before;
import org.junit.Test;
import org.sfm.datastax.impl.RowGetterFactory;
import org.sfm.datastax.utils.Datastax3ClassLoaderUtil;
import org.sfm.datastax.utils.RecorderInvocationHandler;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.reflect.primitive.ShortGetter;
import org.sfm.reflect.primitive.ShortSetter;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class Datastax3Test {

    @Before
    public void setUp() throws IOException, ClassNotFoundException {
        classLoader = Datastax3ClassLoaderUtil.getDatastax3ClassLoader();
        dataTypeHelperClass = loadClass(DataTypeHelper.class);
        dataTypeNameClass = loadClass(DataType.Name.class);
        dataTypeClass = loadClass(DataType.class);
        localDateClass = loadClass("com.datastax.driver.core.LocalDate");
        rowGetterFactoryClass = loadClass(RowGetterFactory.class);
        gettableClass = loadClass(GettableByIndexData.class);
    }

    ClassLoader classLoader;
    Class<?> dataTypeHelperClass;
    Class dataTypeNameClass;
    Class dataTypeClass;
    Class localDateClass;
    Class gettableClass;

    Class rowGetterFactoryClass;

    @Test
    public void testDataTypeHelperLocalDateClassSet() throws NoSuchFieldException, IllegalAccessException {
        Class<?> localDateClass = (Class<?>) dataTypeHelperClass.getField("localDateClass").get(null);
        assertNotNull(localDateClass);
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
    public void testAllDataTypeNameHaveAnAssignedType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object values = dataTypeNameClass.getDeclaredMethod("values").invoke(null);
        Method asJavaClass = dataTypeHelperClass.getDeclaredMethod("asJavaClass", dataTypeNameClass);

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
        Object eName = Enum.valueOf(dataTypeNameClass, name);
        Method asJavaClass = dataTypeHelperClass.getDeclaredMethod("isNumber", dataTypeNameClass);
        assertEquals(expected, asJavaClass.invoke(null, eName));
    }

    private void assertDataTypeNameIsMapToExpectedType(Class<?> expectedType, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Object eName = Enum.valueOf(dataTypeNameClass, name);
        Method asJavaClass = dataTypeHelperClass.getDeclaredMethod("asJavaClass", dataTypeNameClass);
        assertEquals(expectedType, asJavaClass.invoke(null, eName));
    }

    @Test
    public void testSmallIntGetter() throws Exception {
        testNumberGetter(short.class, ShortGetter.class);
    }

    private void testNumberGetter(Class<?> numberClass, Class<?> primitiveGetter) throws Exception {
        Method asJavaClass = dataTypeHelperClass.getDeclaredMethod("asJavaClass", dataTypeClass);
        Method isNumber = dataTypeHelperClass.getDeclaredMethod("isNumber", dataTypeClass);

        Method[] methods = dataTypeClass.getMethods();
        for(int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getReturnType().equals(dataTypeClass)
                    && Modifier.isStatic(method.getModifiers())
                    && method.getParameterTypes().length == 0) {
                Object data = method.invoke(null);
                if (((Boolean)isNumber.invoke(null, data))) {
                    Class dataTypeClass = (Class) asJavaClass.invoke(null, data);

                    Object getter = getGetter(numberClass, data);

                    assertNotNull(getter);

                    if (numberClass.isPrimitive() && dataTypeClass.equals(numberClass) && primitiveGetter != null) {
                        loadClass(primitiveGetter).isInstance(getter);
                    }

                    RecorderInvocationHandler recorder = new RecorderInvocationHandler();
                    Object gettableByDataInstance = Proxy.newProxyInstance(classLoader, new Class[] {gettableClass}, recorder);
                    getter.getClass().getDeclaredMethod("get", Object.class).invoke(getter, gettableByDataInstance);
                    recorder.invokedOnce(getterMethodFor(data), 1);
                }
            }
        }



    }

    private String getterMethodFor(Object dataType) throws Exception {
        Object name = dataTypeClass.getDeclaredMethod("getName").invoke(dataType);
        String value = (String) Enum.class.getDeclaredMethod("name").invoke(name);

        switch (value) {
            case "BIGINT"  : return "getLong";
            case "COUNTER" : return "getLong";
            case "INT"     : return "getInt";
            case "TINYINT"     : return "getByte";
            case "TIME"     : return "getTime";
            case "DECIMAL"     : return "getDecimal";
            case "DOUBLE"   : return "getDouble";
            case "FLOAT"   : return "getFloat";
            case "VARINT"   : return "getVarint";
            case "SMALLINT"   : return "getShort";
        }

        throw new IllegalArgumentException("Not method define for " + value);
    }

    private Object getGetter(Class<?> target, String datatype) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {
        Object dataTypeInstance = dataTypeClass.getDeclaredMethod(datatype).invoke(null);
        return getGetter(target, dataTypeInstance);
    }

    private Object getGetter(Class<?> target, Object dataTypeInstance) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        Object rowGetterFactory = rowGetterFactoryClass
                .getConstructor(loadClass(DatastaxMapperFactory.class))
                .newInstance(new Object[] { null});

        Object columnKey = loadClass(DatastaxColumnKey.class)
                .getConstructor(String.class, int.class, dataTypeClass)
                .newInstance("col", 1, dataTypeInstance);

        Object columnDefinition = loadClass(FieldMapperColumnDefinition.class).getDeclaredMethod("identity").invoke(null);
        //newGetter(Type target, DatastaxColumnKey key, ColumnDefinition<?, ?> columnDefinition)
        return
                rowGetterFactoryClass.getDeclaredMethod("newGetter", Type.class, loadClass(DatastaxColumnKey.class),  loadClass(ColumnDefinition.class))
                .invoke(rowGetterFactory, target, columnKey, columnDefinition);
    }

    private Class<?> loadClass(Class<?> target) throws ClassNotFoundException {
        return loadClass(target.getName());
    }

    private Class<?> loadClass(String target) throws ClassNotFoundException {
        return classLoader.loadClass(target);
    }
}
