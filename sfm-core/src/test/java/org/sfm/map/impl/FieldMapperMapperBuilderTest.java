package org.sfm.map.impl;

import org.junit.Test;
import org.sfm.map.FieldMapper;
import org.sfm.map.GetterFactory;
import org.sfm.map.error.RethrowMapperBuilderErrorHandler;
import org.sfm.map.impl.fieldmapper.ConstantSourceFieldMapperFactoryImpl;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.impl.fieldmapper.ConstantSourceFieldMapperFactory;
import org.sfm.reflect.Getter;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.impl.ConstantGetter;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.samples.SampleFieldKey;

import java.lang.reflect.Type;
import java.util.Date;

import static org.junit.Assert.*;


public class FieldMapperMapperBuilderTest {

    @Test
    public void testAnonymousParameterWithDifferentType() throws Exception {

        ClassMeta<MyObjectWithInner> classMeta = ReflectionService.disableAsm().getClassMeta(MyObjectWithInner.class);
        ConstantSourceFieldMapperFactory<Object, SampleFieldKey> factory = new ConstantSourceFieldMapperFactoryImpl<Object, SampleFieldKey>(new GetterFactory<Object, SampleFieldKey>() {
            @Override
            public <P> Getter<Object, P> newGetter(Type target, SampleFieldKey key, ColumnDefinition<?, ?> columnDefinition) {
                return new ConstantGetter<Object, P>((P) new Date());
            }
        });

        PropertyMeta<MyObjectWithInner, MultiConstructorObject> propertyMeta = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("prop"));
        FieldMapperColumnDefinition<SampleFieldKey> identity = FieldMapperColumnDefinition.identity();
        PropertyMapping<MyObjectWithInner, MultiConstructorObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> propertyMapping =
                new PropertyMapping<MyObjectWithInner, MultiConstructorObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        propertyMeta, new SampleFieldKey("prop", 1), identity);
        FieldMapper<Object, MyObjectWithInner> fieldMapper = factory.newFieldMapper(propertyMapping, null, new RethrowMapperBuilderErrorHandler());

        MyObjectWithInner o = new MyObjectWithInner();
        fieldMapper.mapTo(new Object(), o, null);
        assertNotNull(o.prop.date);
    }


    public static class MyObjectWithInner {
        public MultiConstructorObject prop;
    }
    public static class MultiConstructorObject {
        private String str;
        private Date date;

        public MultiConstructorObject(String bob) {
            this.str = bob;
        }
        public MultiConstructorObject(Date bap) {
            this.date = bap;
        }
    }

}