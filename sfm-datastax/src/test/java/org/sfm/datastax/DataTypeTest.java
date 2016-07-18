package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.SettableByIndexData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.sfm.datastax.impl.RowGetterFactory;
import org.sfm.datastax.impl.SettableDataSetterFactory;
import org.sfm.datastax.impl.SettableDataSetterFactoryTest;
import org.sfm.datastax.utils.RecorderInvocationHandler;
import org.sfm.map.MapperConfig;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.Setter;
import org.sfm.reflect.meta.ObjectClassMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.primitive.ByteGetter;
import org.sfm.reflect.primitive.ByteSetter;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.DoubleSetter;
import org.sfm.reflect.primitive.FloatGetter;
import org.sfm.reflect.primitive.FloatSetter;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.IntSetter;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.reflect.primitive.LongSetter;
import org.sfm.reflect.primitive.ShortGetter;
import org.sfm.reflect.primitive.ShortSetter;
import org.sfm.utils.LibrarySet;
import org.sfm.utils.MultiClassLoaderJunitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MultiClassLoaderJunitRunner.class)
@LibrarySet(
        libraryGroups = {
                //IFJAVA8_START
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.0.3/cassandra-driver-core-3.0.3.jar",
                //IFJAVA8_END
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/2.1.8/cassandra-driver-core-2.1.8.jar"
        },
        includes={ReflectionService.class, DatastaxCrud.class, DatastaxCrudTest.class, Mock.class},
        names={"v303", "v218"}
)
public class DataTypeTest {


    @Test
    public void testAllDataTypeNameHaveAnAssignedType() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        for(DataType.Name name : DataType.Name.values()) {
            assertNotNull(DataTypeHelper.asJavaClass(name));
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

    private void testNumber(Class<?> numberClass, Class<?> primitiveGetter, Class<?> primitiveSetter) throws Exception {
        testNumberGetter(numberClass, primitiveGetter);
        testNumberSetter(numberClass, primitiveSetter);
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

                    Getter getter = getGetter(numberClass, dataType);

                    assertNotNull("No getter for " + numberClass + ", " + dataType, getter);

                    if (numberClass.isPrimitive() && dataTypeClass.equals(numberClass) && primitiveGetter != null) {
                        primitiveGetter.isInstance(getter);
                    }

                    RecorderInvocationHandler recorder = new RecorderInvocationHandler();
                    Object gettableByDataInstance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { GettableByIndexData.class }, recorder);
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

    private void testNumberSetter(Class<?> numberClass, Class<?> primitiveSetter) throws Exception {
        Method[] methods = DataType.class.getMethods();
        for(int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (method.getReturnType().equals(DataType.class)
                    && Modifier.isStatic(method.getModifiers())
                    && method.getParameterTypes().length == 0) {
                DataType dataType = (DataType) method.invoke(null);
                if (DataTypeHelper.isNumber(dataType)) {
                    Class dataTypeClass = DataTypeHelper.asJavaClass(dataType);

                    Setter setter = getSetter(numberClass, dataType);

                    assertNotNull("No setter for " + numberClass + ", " + dataType, setter);

                    if (numberClass.isPrimitive() && dataTypeClass.equals(numberClass) && primitiveSetter != null) {
                        primitiveSetter.isInstance(setter);
                    }

                    RecorderInvocationHandler recorder = new RecorderInvocationHandler();
                    Object settableByDataInstance = Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { SettableByIndexData.class }, recorder);
                    Object value = getValue(numberClass);
                    setter.set(settableByDataInstance, value);
                    recorder.invokedOnce(setterMethodFor(dataType), 1, getValue(dataTypeClass));

                    recorder.reset();

                    setter.set(settableByDataInstance, null);
                    recorder.invokedOnce("setToNull", 1);
                }
            }
        }
    }

    private Object getValue(Class<?> numberClass) {
        if (numberClass.equals(byte.class) || numberClass.equals(Byte.class)) {
            return (byte)1;
        }
        if (numberClass.equals(short.class) || numberClass.equals(Short.class)) {
            return (short)1;
        }
        if (numberClass.equals(int.class) || numberClass.equals(Integer.class)) {
            return (int)1;
        }
        if (numberClass.equals(long.class) || numberClass.equals(Long.class)) {
            return (long)1;
        }
        if (numberClass.equals(float.class) || numberClass.equals(Float.class)) {
            return (float)1;
        }
        if (numberClass.equals(double.class) || numberClass.equals(Double.class)) {
            return (double)1;
        }
        if (numberClass.equals(BigInteger.class)) {
            return BigInteger.ONE;
        }
        if (numberClass.equals(BigDecimal.class)) {
            return BigDecimal.ONE;
        }
        throw new IllegalArgumentException("not number for "+ numberClass);
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

    private String setterMethodFor(DataType dataType) throws Exception {
        return getterMethodFor(dataType).replace("get", "set");
    }

    private Getter getGetter(Class<?> target, DataType dataType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        RowGetterFactory rowGetterFactory = new RowGetterFactory(null);

        DatastaxColumnKey columnKey = new DatastaxColumnKey("col", 1, dataType);

        FieldMapperColumnDefinition<DatastaxColumnKey> columnDefinition = FieldMapperColumnDefinition.identity();

        return rowGetterFactory.newGetter(target, columnKey, columnDefinition);
    }

    private Setter getSetter(Class<?> target, DataType dataType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {

        MapperConfig<DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>> mapperConfig = MapperConfig.<DatastaxColumnKey>fieldMapperConfig();
        ReflectionService reflectionService = ReflectionService.newInstance();
        SettableDataSetterFactory factory = new SettableDataSetterFactory(mapperConfig, reflectionService);

        DatastaxColumnKey columnKey = new DatastaxColumnKey("col", 1, dataType);


        return factory.getSetter(newPM(target, dataType, columnKey));
    }
    private <T, P> PropertyMapping<?, ?, DatastaxColumnKey, ? extends ColumnDefinition<DatastaxColumnKey, ?>> newPM(Type clazz, DataType datatype, DatastaxColumnKey columnKey) {
        PropertyMeta<T, P> propertyMeta = mock(PropertyMeta.class);
        when(propertyMeta.getPropertyType()).thenReturn(clazz);
        return
                new PropertyMapping<T, P, DatastaxColumnKey, FieldMapperColumnDefinition<DatastaxColumnKey>>(
                        propertyMeta,
                        columnKey,
                        FieldMapperColumnDefinition.<DatastaxColumnKey>identity());
    }
}
