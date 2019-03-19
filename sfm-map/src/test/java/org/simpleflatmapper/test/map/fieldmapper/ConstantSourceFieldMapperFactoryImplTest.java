package org.simpleflatmapper.test.map.fieldmapper;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.converter.ConverterService;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.fieldmapper.*;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.ConstantBooleanGetter;
import org.simpleflatmapper.reflect.getter.ConstantByteGetter;
import org.simpleflatmapper.reflect.getter.ConstantCharacterGetter;
import org.simpleflatmapper.reflect.getter.ConstantDoubleGetter;
import org.simpleflatmapper.reflect.getter.ConstantFloatGetter;
import org.simpleflatmapper.reflect.getter.ConstantIntGetter;
import org.simpleflatmapper.reflect.getter.ConstantLongGetter;
import org.simpleflatmapper.reflect.getter.ConstantShortGetter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.util.ConstantPredicate;

import java.lang.reflect.Method;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConstantSourceFieldMapperFactoryImplTest {

    public static final RethrowMapperBuilderErrorHandler MAPPING_ERROR_HANDLER = RethrowMapperBuilderErrorHandler.INSTANCE;
    public static final ReflectionService REFLECTION_SERVICE = ReflectionService.newInstance();

    ConstantSourceFieldMapperFactoryImpl<Object, SampleFieldKey> constantSourceFieldMapperFactory;
    MappingContextFactoryBuilder<Object, SampleFieldKey> mappingContextFactoryBuilder;
    GetterFactory<Object, SampleFieldKey> getterFactory;
    KeySourceGetter<SampleFieldKey, Object> keySourceGetter;

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {
        getterFactory = mock(GetterFactory.class);
        constantSourceFieldMapperFactory = new ConstantSourceFieldMapperFactoryImpl<Object, SampleFieldKey>(new ContextualGetterFactoryAdapter<Object, SampleFieldKey>(getterFactory), ConverterService.getInstance(), Object.class);
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

        DbPrimitiveObjectWithSetter object = new DbPrimitiveObjectWithSetter();

        {
            FieldMapper<Object, DbPrimitiveObjectWithSetter> fieldMapper =
                    createFieldMapper(DbPrimitiveObjectWithSetter.class, "pBoolean", new ConstantBooleanGetter<Object>(true));

            assertPrimitiveSetter(fieldMapper, object, "ispBoolean", BooleanConstantSourceFieldMapper.class, false, true);
        }

        {
            FieldMapper<Object, DbPrimitiveObjectWithSetter> fieldMapper =
                    createFieldMapper(DbPrimitiveObjectWithSetter.class, "pByte", new ConstantByteGetter<Object>((byte) 16));

            assertPrimitiveSetter(fieldMapper, object, "getpByte", ByteConstantSourceFieldMapper.class, (byte) 0, (byte) 16);
        }

        {
            FieldMapper<Object, DbPrimitiveObjectWithSetter> fieldMapper =
                    createFieldMapper(DbPrimitiveObjectWithSetter.class, "pCharacter", new ConstantCharacterGetter<Object>((char) 16));

            assertPrimitiveSetter(fieldMapper, object, "getpCharacter", CharacterConstantSourceFieldMapper.class, (char) 0, (char) 16);
        }

        {
            FieldMapper<Object, DbPrimitiveObjectWithSetter> fieldMapper =
                    createFieldMapper(DbPrimitiveObjectWithSetter.class, "pShort", new ConstantShortGetter<Object>((short) 16));

            assertPrimitiveSetter(fieldMapper, object, "getpShort", ShortConstantSourceFieldMapper.class, (short) 0, (short) 16);
        }

        {
            FieldMapper<Object, DbPrimitiveObjectWithSetter> fieldMapper =
                    createFieldMapper(DbPrimitiveObjectWithSetter.class, "pInt", new ConstantIntGetter<Object>(16));

            assertPrimitiveSetter(fieldMapper, object, "getpInt", IntConstantSourceFieldMapper.class, 0, 16);
        }

        {
            FieldMapper<Object, DbPrimitiveObjectWithSetter> fieldMapper =
                    createFieldMapper(DbPrimitiveObjectWithSetter.class, "pLong", new ConstantLongGetter<Object>(16l));

            assertPrimitiveSetter(fieldMapper, object, "getpLong", LongConstantSourceFieldMapper.class, 0l, 16l);
        }

        {
            FieldMapper<Object, DbPrimitiveObjectWithSetter> fieldMapper =
                    createFieldMapper(DbPrimitiveObjectWithSetter.class, "pFloat", new ConstantFloatGetter<Object>(16f));

            assertPrimitiveSetter(fieldMapper, object, "getpFloat", FloatConstantSourceFieldMapper.class, 0f, 16f);
        }

        {
            FieldMapper<Object, DbPrimitiveObjectWithSetter> fieldMapper =
                    createFieldMapper(DbPrimitiveObjectWithSetter.class, "pDouble", new ConstantDoubleGetter<Object>(16.0));

            assertPrimitiveSetter(fieldMapper, object, "getpDouble", DoubleConstantSourceFieldMapper.class, 0.0, 16.0);
        }

    }

    private <T> void assertPrimitiveSetter(FieldMapper<Object, T> fieldMapper, T object, String getter, Class<?> primitiveInterface, Object... expected) throws Exception {
        assertTrue("Expect " + fieldMapper + " to be an newInstance of " + primitiveInterface, primitiveInterface.isInstance(fieldMapper));
        Method getterMethod = object.getClass().getMethod(getter);
        assertEquals(expected[0], getterMethod.invoke(object));
        fieldMapper.mapTo(null, object, null);
        assertEquals(expected[1], getterMethod.invoke(object));
    }

    private <T, P> FieldMapper<Object, T> createFieldMapper(
            Class<T> target, String property, Getter<Object, P> getter) {

        PropertyMapping<T, P, SampleFieldKey> pm = createPropertyMapping(target, property);
        when(getterFactory.<P>newGetter(pm.getPropertyMeta().getPropertyType(), pm.getColumnKey(), pm.getColumnDefinition().properties())).thenReturn(getter);
        return constantSourceFieldMapperFactory.newFieldMapper(pm, mappingContextFactoryBuilder, MAPPING_ERROR_HANDLER);
    }
    public static <T, P> PropertyMapping<T, P, SampleFieldKey> createPropertyMapping(
            Class<T> target, String property) {
        ClassMeta<T> classMeta = REFLECTION_SERVICE.getClassMeta(target);

        PropertyMeta<T, P> propertyMeta = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of(property), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter());

        PropertyMapping<T, P , SampleFieldKey> pm =
                new PropertyMapping<T, P, SampleFieldKey>(
                        propertyMeta,
                        new SampleFieldKey(property, 0),
                        FieldMapperColumnDefinition.<SampleFieldKey>identity());
        return pm;
    }

}