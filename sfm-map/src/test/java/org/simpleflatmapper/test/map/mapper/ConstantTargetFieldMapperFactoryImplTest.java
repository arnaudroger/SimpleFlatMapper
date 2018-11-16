package org.simpleflatmapper.test.map.mapper;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.error.RethrowMapperBuilderErrorHandler;
import org.simpleflatmapper.test.map.fieldmapper.ConstantSourceFieldMapperFactoryImplTest;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactory;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactoryImpl;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.primitive.BooleanSetter;
import org.simpleflatmapper.reflect.primitive.ByteSetter;
import org.simpleflatmapper.reflect.primitive.CharacterSetter;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;
import org.simpleflatmapper.reflect.primitive.ShortSetter;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class ConstantTargetFieldMapperFactoryImplTest {


    SetterFactory<Appendable, PropertyMapping<?, ?, SampleFieldKey>> setterFactory =
            new SetterFactory<Appendable, PropertyMapping<?, ?, SampleFieldKey>>() {
                @Override
                public <P> Setter<Appendable, P> getSetter(PropertyMapping<?, ?, SampleFieldKey> arg) {
                    Type propertyType = arg.getPropertyMeta().getPropertyType();
                    
                    if (boolean.class.equals(propertyType)) {
                        return (Setter<Appendable, P>) new BooleanAppendableSetter();
                    }
                    if (byte.class.equals(propertyType)) {
                        return (Setter<Appendable, P>) new ByteAppendableSetter();
                    }
                    if (char.class.equals(propertyType)) {
                        return (Setter<Appendable, P>) new CharacterAppendableSetter();
                    }
                    if (short.class.equals(propertyType)) {
                        return (Setter<Appendable, P>) new ShortAppendableSetter();
                    }
                    if (int.class.equals(propertyType)) {
                        return (Setter<Appendable, P>) new IntAppendableSetter();
                    }
                    if (long.class.equals(propertyType)) {
                        return (Setter<Appendable, P>) new LongAppendableSetter();
                    }
                    if (float.class.equals(propertyType)) {
                        return (Setter<Appendable, P>) new FloatAppendableSetter();
                    }
                    if (double.class.equals(propertyType)) {
                        return (Setter<Appendable, P>) new DoubleAppendableSetter();
                    }
                    if (TypeHelper.isJavaLang(propertyType)) {
                        return new AppendableSetter<P>();
                    }
                    return null;
                }
            };
    ConstantTargetFieldMapperFactory<Appendable, SampleFieldKey> factory;

    @Before
    public void setUp() {
        factory = ConstantTargetFieldMapperFactoryImpl.<Appendable, SampleFieldKey>newInstance(setterFactory, Appendable.class);
    }

    @Test
    public void testPrimitiveMapping() throws Exception {
        DbPrimitiveObjectWithSetter objectWithSetter = new DbPrimitiveObjectWithSetter();
        objectWithSetter.setpBoolean(true);
        objectWithSetter.setpByte((byte) 52);
        objectWithSetter.setpCharacter('a');
        objectWithSetter.setpShort((short) 139);
        objectWithSetter.setpInt(12345);
        objectWithSetter.setpLong(1234567);
        objectWithSetter.setpFloat((float) 1.23);
        objectWithSetter.setpDouble(1.234567);

        testMapping(objectWithSetter, "pBoolean", "Ztrue");
        testMapping(objectWithSetter, "pByte", "B52");
        testMapping(objectWithSetter, "pCharacter", "Ca");
        testMapping(objectWithSetter, "pShort", "S139");
        testMapping(objectWithSetter, "pInt", "I12345");
        testMapping(objectWithSetter, "pLong", "L1234567");
        testMapping(objectWithSetter, "pFloat", "F1.23");
        testMapping(objectWithSetter, "pDouble", "D1.234567");

    }

    private <T> void testMapping(T object, String property, String expectedValue) throws Exception {
        PropertyMapping<T, String, SampleFieldKey> pm =
                ConstantSourceFieldMapperFactoryImplTest.createPropertyMapping((Class<T>) object.getClass(), property);
        FieldMapper<T, Appendable> fieldMapper = factory.newFieldMapper(pm, null, RethrowMapperBuilderErrorHandler.INSTANCE);

        StringBuilder sb = new StringBuilder();
        fieldMapper.mapTo(object, sb, null);

        assertEquals(expectedValue, sb.toString());

    }

    @Test
    public void testGetterNotFound() {
        ConstantTargetFieldMapperFactory<Appendable, SampleFieldKey> factory = ConstantTargetFieldMapperFactoryImpl.<Appendable, SampleFieldKey>newInstance(setterFactory, Appendable.class);

        try {
            PropertyMeta<Object, Object> pm = mock(PropertyMeta.class);
            PropertyMapping<Object, Object, SampleFieldKey> propertyMapping =
                    new PropertyMapping<Object, Object, SampleFieldKey>(pm, new SampleFieldKey("hh", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());
            factory.newFieldMapper(propertyMapping, null, RethrowMapperBuilderErrorHandler.INSTANCE);
            fail();
        } catch (MapperBuildingException e) {
        }
    }

    @Test
    public void testSetterNotFound() {
        SetterFactory<Appendable, PropertyMapping<?, ?, SampleFieldKey>> setterFactory =
                new SetterFactory<Appendable, PropertyMapping<?, ?, SampleFieldKey>>() {
                    @Override
                    public <P> Setter<Appendable, P> getSetter(PropertyMapping<?, ?, SampleFieldKey> arg) {
                        return null;
                    }
                };

        ConstantTargetFieldMapperFactory<Appendable, SampleFieldKey> factory = ConstantTargetFieldMapperFactoryImpl.<Appendable, SampleFieldKey>newInstance(setterFactory, Appendable.class);
        try {
            PropertyMapping<DbObject, Object, SampleFieldKey> propertyMapping =
                    ConstantSourceFieldMapperFactoryImplTest.createPropertyMapping(DbObject.class, "id");
            assertNotNull(propertyMapping.getPropertyMeta().getGetter());
            factory.newFieldMapper(propertyMapping, null, RethrowMapperBuilderErrorHandler.INSTANCE);
            fail();
        } catch (MapperBuildingException e) {
        }
    }

    @Test
    public void testSetterFactoryProperty() {

    }

    @Test
    public void testObjectWithOneProperty() throws Exception {
        Wrapper w = new Wrapper();
        w.o = new ObjectWithOneProp("1");
        testMapping(w, "o", "1");
    }

    public static class Wrapper {
        public ObjectWithOneProp o;
    }
    public static class ObjectWithOneProp {
        private final String value;

        public ObjectWithOneProp(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static class AppendableSetter<P> implements
            Setter<Appendable, P>

    {
        @Override
        public void set(Appendable target, P value) throws Exception {
            target.append(String.valueOf(value));
        }

      
    }

    private class BooleanAppendableSetter implements Setter<Appendable, Boolean>, BooleanSetter<Appendable> {
        @Override
        public void set(Appendable target, Boolean value) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setBoolean(Appendable target, boolean value) throws Exception {
            target.append("Z" + value);
        }
    }

    private class ByteAppendableSetter implements Setter<Appendable, Byte>, ByteSetter<Appendable> {
        @Override
        public void set(Appendable target, Byte value) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setByte(Appendable target, byte value) throws Exception {
            target.append("B" + value);
        }
    }

    private class CharacterAppendableSetter implements Setter<Appendable, Character>, CharacterSetter<Appendable> {
        @Override
        public void set(Appendable target, Character value) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setCharacter(Appendable target, char value) throws Exception {
            target.append("C" + value);
        }
    }

    private class ShortAppendableSetter implements Setter<Appendable, Short>, ShortSetter<Appendable> {
        @Override
        public void set(Appendable target, Short value) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setShort(Appendable target, short value) throws Exception {
            target.append("S" + value);
        }
    }
    private class IntAppendableSetter implements Setter<Appendable, Integer>, IntSetter<Appendable> {
        @Override
        public void set(Appendable target, Integer value) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setInt(Appendable target, int value) throws Exception {
            target.append("I" + value);
        }
    }
    private class LongAppendableSetter implements Setter<Appendable, Long>, LongSetter<Appendable> {
        @Override
        public void set(Appendable target, Long value) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setLong(Appendable target, long value) throws Exception {
            target.append("L" + value);
        }
    }
    private class FloatAppendableSetter implements Setter<Appendable, Float>, FloatSetter<Appendable> {
        @Override
        public void set(Appendable target, Float value) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setFloat(Appendable target, float value) throws Exception {
            target.append("F" + value);
        }
    }
    private class DoubleAppendableSetter implements Setter<Appendable, Double>, DoubleSetter<Appendable> {
        @Override
        public void set(Appendable target, Double value) throws Exception {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setDouble(Appendable target, double value) throws Exception {
            target.append("D" + value);
        }
    }
}