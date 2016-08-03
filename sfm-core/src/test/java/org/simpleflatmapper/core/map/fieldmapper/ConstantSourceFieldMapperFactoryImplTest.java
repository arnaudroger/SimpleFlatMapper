package org.simpleflatmapper.core.map.fieldmapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.core.map.FieldMapper;
import org.simpleflatmapper.core.map.GetterFactory;
import org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import org.simpleflatmapper.core.map.context.KeySourceGetter;
import org.simpleflatmapper.core.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.core.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.core.map.mapper.PropertyMapping;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.getter.ConstantBooleanGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantByteGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantCharacterGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantDoubleGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantFloatGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantIntGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantLongGetter;
import org.simpleflatmapper.core.reflect.getter.ConstantShortGetter;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;
import org.simpleflatmapper.core.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.core.reflect.primitive.BooleanGetter;
import org.simpleflatmapper.core.samples.SampleFieldKey;
import org.simpleflatmapper.test.beans.DbPrimitiveObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConstantSourceFieldMapperFactoryImplTest {

    public static final RethrowMapperBuilderErrorHandler MAPPING_ERROR_HANDLER = new RethrowMapperBuilderErrorHandler();
    public static final ReflectionService REFLECTION_SERVICE = ReflectionService.newInstance();
    public static final ReflectionService REFLECTION_SERVICE_NO_ASM = ReflectionService.disableAsm();

    ConstantSourceFieldMapperFactoryImpl<Object, SampleFieldKey> constantSourceFieldMapperFactory;
    MappingContextFactoryBuilder<Object, SampleFieldKey> mappingContextFactoryBuilder;
    GetterFactory<Object, SampleFieldKey> getterFactory;
    KeySourceGetter<SampleFieldKey, Object> keySourceGetter;

    @Before
    public void setUp() {
        getterFactory = mock(GetterFactory.class);
        constantSourceFieldMapperFactory = new ConstantSourceFieldMapperFactoryImpl<Object, SampleFieldKey>(getterFactory);
        keySourceGetter = mock(KeySourceGetter.class);
        mappingContextFactoryBuilder = null;
    }

    @After
    public void tearDown() {
        getterFactory = null;
        constantSourceFieldMapperFactory = null;
    }

    @Test
    public void testFieldMapperPrimitives() throws Exception {

        DbPrimitiveObject object = new DbPrimitiveObject();

        {
            FieldMapper<Object, DbPrimitiveObject> fieldMapper =
                    createFieldMapper(DbPrimitiveObject.class, "pBoolean", new ConstantBooleanGetter<Object>(true));

            assertPrimitiveSetter(fieldMapper, object, "ispBoolean", BooleanFieldMapper.class, false, true);
        }

        {
            FieldMapper<Object, DbPrimitiveObject> fieldMapper =
                    createFieldMapper(DbPrimitiveObject.class, "pByte", new ConstantByteGetter<Object>((byte) 16));

            assertPrimitiveSetter(fieldMapper, object, "getpByte", ByteFieldMapper.class, (byte) 0, (byte) 16);
        }

        {
            FieldMapper<Object, DbPrimitiveObject> fieldMapper =
                    createFieldMapper(DbPrimitiveObject.class, "pCharacter", new ConstantCharacterGetter<Object>((char) 16));

            assertPrimitiveSetter(fieldMapper, object, "getpCharacter", CharacterFieldMapper.class, (char) 0, (char) 16);
        }

        {
            FieldMapper<Object, DbPrimitiveObject> fieldMapper =
                    createFieldMapper(DbPrimitiveObject.class, "pShort", new ConstantShortGetter<Object>((short) 16));

            assertPrimitiveSetter(fieldMapper, object, "getpShort", ShortFieldMapper.class, (short) 0, (short) 16);
        }

        {
            FieldMapper<Object, DbPrimitiveObject> fieldMapper =
                    createFieldMapper(DbPrimitiveObject.class, "pInt", new ConstantIntGetter<Object>(16));

            assertPrimitiveSetter(fieldMapper, object, "getpInt", IntFieldMapper.class, 0, 16);
        }

        {
            FieldMapper<Object, DbPrimitiveObject> fieldMapper =
                    createFieldMapper(DbPrimitiveObject.class, "pLong", new ConstantLongGetter<Object>(16l));

            assertPrimitiveSetter(fieldMapper, object, "getpLong", LongFieldMapper.class, 0l, 16l);
        }

        {
            FieldMapper<Object, DbPrimitiveObject> fieldMapper =
                    createFieldMapper(DbPrimitiveObject.class, "pFloat", new ConstantFloatGetter<Object>(16f));

            assertPrimitiveSetter(fieldMapper, object, "getpFloat", FloatFieldMapper.class, 0f, 16f);
        }

        {
            FieldMapper<Object, DbPrimitiveObject> fieldMapper =
                    createFieldMapper(DbPrimitiveObject.class, "pDouble", new ConstantDoubleGetter<Object>(16.0));

            assertPrimitiveSetter(fieldMapper, object, "getpDouble", DoubleFieldMapper.class, 0.0, 16.0);
        }

    }

    private <T> void assertPrimitiveSetter(FieldMapper<Object, T> fieldMapper, T object, String getter, Class<?> primitiveInterface, Object... expected) throws Exception {
        assertTrue("Expect " + fieldMapper + " to be an instance of " + primitiveInterface, primitiveInterface.isInstance(fieldMapper));
        Method getterMethod = object.getClass().getMethod(getter);
        assertEquals(expected[0], getterMethod.invoke(object));
        fieldMapper.mapTo(null, object, null);
        assertEquals(expected[1], getterMethod.invoke(object));
    }

    private <T, P> FieldMapper<Object, T> createFieldMapper(
            Class<T> target, String property, Getter<Object, P> getter) {

        PropertyMapping<T, P, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> pm = createPropertyMapping(target, property);
        when(getterFactory.<P>newGetter(pm.getPropertyMeta().getPropertyType(), pm.getColumnKey(), pm.getColumnDefinition())).thenReturn(getter);
        return constantSourceFieldMapperFactory.newFieldMapper(pm, mappingContextFactoryBuilder, MAPPING_ERROR_HANDLER);


    }
    private <T, P> PropertyMapping<T, P, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> createPropertyMapping(
            Class<T> target, String property) {
        ClassMeta<T> classMeta = REFLECTION_SERVICE.getClassMeta(target);

        PropertyMeta<T, P> propertyMeta = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of(property));

        PropertyMapping<T, P , SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> pm =
                new PropertyMapping<T, P, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        propertyMeta,
                        new SampleFieldKey(property, 0),
                        FieldMapperColumnDefinition.<SampleFieldKey>identity());
        return pm;
    }

}