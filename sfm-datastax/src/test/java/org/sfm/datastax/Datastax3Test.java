package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sfm.datastax.impl.RowGetterFactory;
import org.sfm.datastax.utils.RecorderInvocationHandler;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.primitive.ShortGetter;
import org.sfm.utils.LibrarySet;
import org.sfm.utils.MultiClassLoaderJunitRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;

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
    public void testAllDataTypeNameHaveAnAssignedType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for(DataType.Name name : DataType.Name.values()) {
            assertNotNull(DataTypeHelper.asJavaClass(name));
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
        assertEquals(expected, DataTypeHelper.isNumber(DataType.Name.valueOf(name)));
    }

    private void assertDataTypeNameIsMapToExpectedType(Class<?> expectedType, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertEquals(expectedType, DataTypeHelper.asJavaClass(DataType.Name.valueOf(name)));
    }

    @Test
    public void testSmallIntGetter() throws Exception {
        testNumberGetter(short.class, ShortGetter.class);
    }

    private void testNumberGetter(Class<?> numberClass, Class<?> primitiveGetter) throws Exception {

        Method[] methods = DataType.class.getMethods();
        for(int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getReturnType().equals(DataType.class)
                    && Modifier.isStatic(method.getModifiers())
                    && method.getParameterTypes().length == 0) {
                DataType dataType = (DataType) method.invoke(null);
                if (DataTypeHelper.isNumber(dataType)) {
                    Class dataTypeClass = DataTypeHelper.asJavaClass(dataType);

                    Object getter = getGetter(numberClass, dataType);

                    assertNotNull(getter);

                    if (numberClass.isPrimitive() && dataTypeClass.equals(numberClass) && primitiveGetter != null) {
                        primitiveGetter.isInstance(getter);
                    }

                    RecorderInvocationHandler recorder = new RecorderInvocationHandler();
                    Object gettableByDataInstance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { GettableByIndexData.class }, recorder);
                    getter.getClass().getDeclaredMethod("get", Object.class).invoke(getter, gettableByDataInstance);
                    recorder.invokedOnce(getterMethodFor(dataType), 1);
                }
            }
        }



    }

    private String getterMethodFor(DataType dataType) throws Exception {
        String value = dataType.getName().name();

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
        DataType dataTypeInstance = (DataType) DataType.class.getDeclaredMethod(datatype).invoke(null);
        return getGetter(target, dataTypeInstance);
    }

    private Object getGetter(Class<?> target, DataType dataType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        RowGetterFactory rowGetterFactory = new RowGetterFactory(null);

        DatastaxColumnKey columnKey = new DatastaxColumnKey("col", 1, dataType);

        FieldMapperColumnDefinition<DatastaxColumnKey> columnDefinition = FieldMapperColumnDefinition.identity();

        return rowGetterFactory.newGetter(target, columnKey, columnDefinition);
    }

}
