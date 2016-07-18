package org.sfm.datastax;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sfm.datastax.impl.RowGetterFactory;
import org.sfm.datastax.utils.RecorderInvocationHandler;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.primitive.ByteGetter;
import org.sfm.reflect.primitive.DoubleGetter;
import org.sfm.reflect.primitive.FloatGetter;
import org.sfm.reflect.primitive.IntGetter;
import org.sfm.reflect.primitive.LongGetter;
import org.sfm.reflect.primitive.ShortGetter;
import org.sfm.utils.LibrarySet;
import org.sfm.utils.MultiClassLoaderJunitRunner;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(MultiClassLoaderJunitRunner.class)
@LibrarySet(
        libraryGroups = {
                //IFJAVA8_START
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.0.3/cassandra-driver-core-3.0.3.jar",
                //IFJAVA8_END
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/2.1.8/cassandra-driver-core-2.1.8.jar"
        },
        includes={ReflectionService.class, DatastaxCrud.class, DatastaxCrudTest.class},
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
        testNumberGetter(long.class, LongGetter.class);
    }

    @Test
    public void testIntGetter() throws Exception {
        testNumberGetter(int.class, IntGetter.class);
    }

    @Test
    public void testTinyIntGetter() throws Exception {
        testNumberGetter(byte.class, ByteGetter.class);
    }

    @Test
    public void testSmallIntGetter() throws Exception {
        testNumberGetter(short.class, ShortGetter.class);
    }


    @Test
    public void testFloatGetter() throws Exception {
        testNumberGetter(float.class, FloatGetter.class);
    }

    @Test
    public void testDoubleGetter() throws Exception {
        testNumberGetter(double.class, DoubleGetter.class);
    }

    @Test
    public void testBigDecimalGetter() throws Exception {
        testNumberGetter(BigDecimal.class, null);
    }

    @Test
    public void testBigIntegerGetter() throws Exception {
        testNumberGetter(BigInteger.class, null);
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

                    assertNotNull("No getter for " + numberClass + ", " + dataType, getter);

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


    private Object getGetter(Class<?> target, DataType dataType) throws InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, ClassNotFoundException {
        RowGetterFactory rowGetterFactory = new RowGetterFactory(null);

        DatastaxColumnKey columnKey = new DatastaxColumnKey("col", 1, dataType);

        FieldMapperColumnDefinition<DatastaxColumnKey> columnDefinition = FieldMapperColumnDefinition.identity();

        return rowGetterFactory.newGetter(target, columnKey, columnDefinition);
    }

}
