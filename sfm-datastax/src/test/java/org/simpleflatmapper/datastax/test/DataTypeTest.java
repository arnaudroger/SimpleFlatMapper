package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.SettableByIndexData;
import org.junit.Test;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.datastax.impl.RowGetterFactory;
import org.simpleflatmapper.datastax.impl.SettableDataSetterFactory;
import org.simpleflatmapper.datastax.test.utils.RecorderInvocationHandler;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.primitive.ByteGetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;
import org.simpleflatmapper.reflect.primitive.DoubleGetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;
import org.simpleflatmapper.reflect.primitive.FloatGetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.reflect.primitive.LongGetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;
import org.simpleflatmapper.reflect.primitive.ShortGetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DataTypeTest {

    private static Map<String, String> getterMethods = new HashMap<String, String>();
    static {
        getterMethods.put("BIGINT" , "getLong");
        getterMethods.put("COUNTER", "getLong");
        getterMethods.put("INT"    , "getInt");
        getterMethods.put("TINYINT"    , "getByte");
        getterMethods.put("TIME"    , "getTime");
        getterMethods.put("DECIMAL"    , "getDecimal");
        getterMethods.put("DOUBLE"  , "getDouble");
        getterMethods.put("FLOAT"  , "getFloat");
        getterMethods.put("VARINT"  , "getVarint");
        getterMethods.put("SMALLINT"  , "getShort");
    }
    
    @Test
    public void testAllDataTypeNameHaveAnAssignedType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for(DataType.Name name : DataType.Name.values()) {
            if (name != DataType.Name.DURATION) {
                assertNotNull("No asJavaClass for " + name, DataTypeHelper.asJavaClass(name));
            }
        }
    }

    @Test
    public void testIsNumber() throws Exception {
        assertFalse(DataTypeHelper.isNumber(DataType.ascii()));
    }

    @Test
    public void testLongGetter() throws Exception {
        testNumber(long.class, LongGetter.class, LongSetter.class);
    }

    @Test
    public void testIntGetter() throws Exception {
        testNumber(int.class, IntGetter.class, IntSetter.class);
    }

    @Test
    public void testTinyIntGetter() throws Exception {
        testNumber(byte.class, ByteGetter.class, ByteSetter.class);
    }

    @Test
    public void testSmallIntGetter() throws Exception {
        testNumber(short.class, ShortGetter.class, ShortSetter.class);
    }


    @Test
    public void testFloatGetter() throws Exception {
        testNumber(float.class, FloatGetter.class, FloatSetter.class);
    }

    @Test
    public void testDoubleGetter() throws Exception {
        testNumber(double.class, DoubleGetter.class, DoubleSetter.class);
    }

    @Test
    public void testBigDecimalGetter() throws Exception {
        testNumber(BigDecimal.class, null, null);
    }

    @Test
    public void testBigIntegerGetter() throws Exception {
        testNumber(BigInteger.class, null, null);
    }

    private <N> void testNumber(Class<N> numberClass, Class<?> primitiveGetter, Class<?> primitiveSetter) throws Exception {
        testNumberGetter(numberClass, primitiveGetter);
        testNumberSetter(numberClass, primitiveSetter);
    }

    private <N> void testNumberGetter(Class<N> numberClass, Class<?> primitiveGetter) throws Exception {
        Method[] methods = DataType.class.getMethods();
        assertTrue(methods.length > 0);
        for(int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getReturnType().equals(DataType.class)
                    && Modifier.isStatic(method.getModifiers())
                    && method.getParameterTypes().length == 0) {
                DataType dataType = (DataType) method.invoke(null);
                if (DataTypeHelper.isNumber(dataType)) {
                    Class dataTypeClass = DataTypeHelper.asJavaClass(dataType);

                    Getter<? super GettableByIndexData, N> getter = getGetter(numberClass, dataType);

                    assertNotNull("No getter for " + numberClass + ", " + dataType, getter);

                    if (numberClass.isPrimitive() && dataTypeClass.equals(numberClass) && primitiveGetter != null) {
                        primitiveGetter.isInstance(getter);
                    }

                    RecorderInvocationHandler recorder = new RecorderInvocationHandler();
                    GettableByIndexData gettableByDataInstance = (GettableByIndexData) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { GettableByIndexData.class }, recorder);
                    getter.get(gettableByDataInstance);
                    recorder.invokedOnce(getterMethodFor(dataType), 1);

                    if (!(dataTypeClass.equals(numberClass)
                            && (BigInteger.class.equals(numberClass)) || BigDecimal.class.equals(numberClass))) {
                        recorder.when("isNull", 1, true);
                        assertNull(" fail isNull check " + numberClass + " - " + dataTypeClass, getter.get(gettableByDataInstance));
                        recorder.reset();
                    }

                }
            }
        }
    }

    private <N> void testNumberSetter(Class<N> numberClass, Class<?> primitiveSetter) throws Exception {
        Method[] methods = DataType.class.getMethods();
        assertTrue(methods.length > 0);
        for(int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getReturnType().equals(DataType.class)
                    && Modifier.isStatic(method.getModifiers())
                    && method.getParameterTypes().length == 0) {
                DataType dataType = (DataType) method.invoke(null);
                if (DataTypeHelper.isNumber(dataType)) {
                    Class<?> dataTypeClass = DataTypeHelper.asJavaClass(dataType);

                    Setter<? super SettableByIndexData, N> setter = getSetter(numberClass, dataType);

                    assertNotNull("No setter for " + numberClass + ", " + dataType, setter);

                    if (numberClass.isPrimitive() && dataTypeClass.equals(numberClass) && primitiveSetter != null) {
                        primitiveSetter.isInstance(setter);
                    }

                    RecorderInvocationHandler recorder = new RecorderInvocationHandler();
                    SettableByIndexData settableByDataInstance = (SettableByIndexData) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { SettableByIndexData.class }, recorder);
                    N value = getValue(numberClass);
                    setter.set(settableByDataInstance, value);
                    recorder.invokedOnce(setterMethodFor(dataType), 1, getValue(dataTypeClass));

                    recorder.reset();

                    setter.set(settableByDataInstance, null);
                    recorder.invokedOnce("setToNull", 1);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <N> N getValue(Class<N> numberClass) {
        if (numberClass.equals(byte.class) || numberClass.equals(Byte.class)) {
            return (N)Byte.valueOf((byte)1);
        }
        if (numberClass.equals(short.class) || numberClass.equals(Short.class)) {
            return (N)Short.valueOf((short)1);
        }
        if (numberClass.equals(int.class) || numberClass.equals(Integer.class)) {
            return (N)Integer.valueOf(1);
        }
        if (numberClass.equals(long.class) || numberClass.equals(Long.class)) {
            return (N)Long.valueOf(1);
        }
        if (numberClass.equals(float.class) || numberClass.equals(Float.class)) {
            return (N)Float.valueOf(1);
        }
        if (numberClass.equals(double.class) || numberClass.equals(Double.class)) {
            return (N)Double.valueOf(1);
        }
        if (numberClass.equals(BigInteger.class)) {
            return (N)BigInteger.ONE;
        }
        if (numberClass.equals(BigDecimal.class)) {
            return (N)BigDecimal.ONE;
        }
        throw new IllegalArgumentException("not number for "+ numberClass);
    }



    public static String getterMethodFor(DataType dataType) throws Exception {
        String value = dataType.getName().name();

        String method = getterMethods.get(value);
        if (method != null) {
            return method;
        }
        throw new IllegalArgumentException("Not method define for " + value);
    }

    public static String setterMethodFor(DataType dataType) throws Exception {
        return getterMethodFor(dataType).replace("get", "set");
    }

    public static <N> Getter<GettableByIndexData, N> getGetter(Class<N> target, DataType dataType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        RowGetterFactory rowGetterFactory = new RowGetterFactory(null);

        DatastaxColumnKey columnKey = new DatastaxColumnKey("col", 1, dataType);

        FieldMapperColumnDefinition<DatastaxColumnKey> columnDefinition = FieldMapperColumnDefinition.identity();

        return rowGetterFactory.newGetter(target, columnKey, columnDefinition);
    }

    public static <N> Setter<SettableByIndexData, N> getSetter(Class<N> target, DataType dataType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

        MapperConfig<DatastaxColumnKey, ?> mapperConfig = MapperConfig.<DatastaxColumnKey, Row>fieldMapperConfig();
        ReflectionService reflectionService = ReflectionService.newInstance();
        SettableDataSetterFactory factory = new SettableDataSetterFactory(mapperConfig, reflectionService);

        DatastaxColumnKey columnKey = new DatastaxColumnKey("col", 1, dataType);


        return factory.getSetter(newPM(target, dataType, columnKey));
    }
    @SuppressWarnings("unchecked")
    public static <T, P> PropertyMapping<?, ?, DatastaxColumnKey> newPM(Type clazz, DataType datatype, DatastaxColumnKey columnKey) {
        PropertyMeta<T, P> propertyMeta = mock(PropertyMeta.class);
        when(propertyMeta.getPropertyType()).thenReturn(clazz);
        return
                new PropertyMapping<T, P, DatastaxColumnKey>(
                        propertyMeta,
                        columnKey,
                        FieldMapperColumnDefinition.<DatastaxColumnKey>identity());
    }
}
