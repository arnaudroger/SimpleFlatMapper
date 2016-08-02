package  org.simpleflatmapper.core.map.impl;

import org.junit.Test;
import  org.simpleflatmapper.core.map.FieldMapper;
import  org.simpleflatmapper.core.map.GetterFactory;
import  org.simpleflatmapper.core.map.error.RethrowMapperBuilderErrorHandler;
import  org.simpleflatmapper.core.map.fieldmapper.ConstantSourceFieldMapperFactoryImpl;
import  org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import  org.simpleflatmapper.core.map.mapper.PropertyMapping;
import  org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import  org.simpleflatmapper.core.map.fieldmapper.ConstantSourceFieldMapperFactory;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.getter.ConstantGetter;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;
import org.simpleflatmapper.core.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.core.samples.SampleFieldKey;

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
                if (target.equals(Date.class)) {
                    return new ConstantGetter<Object, P>((P) new Date());
                }
                return null;
            }
        });

        PropertyMeta<MyObjectWithInner, MultiConstructorObject> propertyMeta = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("prop"));
        FieldMapperColumnDefinition<SampleFieldKey> identity = FieldMapperColumnDefinition.identity();
        PropertyMapping<MyObjectWithInner, MultiConstructorObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> propertyMapping =
                new PropertyMapping<MyObjectWithInner, MultiConstructorObject, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        propertyMeta, new SampleFieldKey("prop", 1, Date.class), identity);
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