package org.simpleflatmapper.datastax.test;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.SettableByIndexData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.datastax.DataTypeHelper;
import org.simpleflatmapper.datastax.DatastaxCrud;
import org.simpleflatmapper.datastax.test.utils.RecorderInvocationHandler;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.test.junit.LibrarySets;
import org.simpleflatmapper.test.junit.MultiClassLoaderJunitRunner;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(MultiClassLoaderJunitRunner.class)
@LibrarySets(
        librarySets = {
                "http://repo1.maven.org/maven2/com/datastax/cassandra/cassandra-driver-core/3.0.3/cassandra-driver-core-3.0.3.jar"
        },
        includes={Converter.class, ReflectionService.class, Mapper.class, DatastaxCrud.class, DatastaxCrudTest.class},
        excludes = { "org.junit", "io.netty"},
        names={"v303"}
)
public class Datastax3 {

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

    @Test
    public void testLocalDateGetter() throws Exception {
        Getter<GettableByIndexData, ?> getter = DataTypeTest.getGetter(localDateClass, (DataType) DataType.class.getMethod("date").invoke(null));

        RecorderInvocationHandler recorder = new RecorderInvocationHandler();
        GettableByIndexData gettableByDataInstance = (GettableByIndexData) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { GettableByIndexData.class }, recorder);

        getter.get(gettableByDataInstance);

        recorder.invokedOnce("getDate", 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testLocalDateSetter() throws Exception {
        Setter<SettableByIndexData, Object> setter = DataTypeTest.getSetter((Class<Object>)localDateClass, (DataType) DataType.class.getMethod("date").invoke(null));

        RecorderInvocationHandler recorder = new RecorderInvocationHandler();
        SettableByIndexData settableByDataInstance = (SettableByIndexData) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { SettableByIndexData.class }, recorder);

        Object localDateInstance = localDateClass.getDeclaredMethod("fromMillisSinceEpoch", long.class).invoke(null, System.currentTimeMillis());
        setter.set(settableByDataInstance, localDateInstance);

        recorder.invokedOnce("setDate", 1, localDateInstance);

        recorder.reset();

        setter.set(settableByDataInstance, null);
        recorder.invokedOnce("setToNull", 1);
    }

    private void assertDataTypeNameIsNumber(boolean expected, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertEquals(expected, DataTypeHelper.isNumber(DataType.Name.valueOf(name)));
    }

    private void assertDataTypeNameIsMapToExpectedType(Class<?> expectedType, String name) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        assertEquals(expectedType, DataTypeHelper.asJavaClass(DataType.Name.valueOf(name)));
    }

}
