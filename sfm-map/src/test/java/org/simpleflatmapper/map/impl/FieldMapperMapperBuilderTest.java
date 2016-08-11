package org.simpleflatmapper.map.impl;

import org.junit.Test;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SampleFieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.FieldMapperMapperBuilder;
import org.simpleflatmapper.map.mapper.MapperSource;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;

import java.lang.reflect.Type;
import java.util.Date;

import static org.junit.Assert.*;


public class FieldMapperMapperBuilderTest {

    public static final Date DATE = new Date();
    public static final String STRING = "hello!";

    ClassMeta<MyObjectWithInner> classMeta = ReflectionService.disableAsm().getClassMeta(MyObjectWithInner.class);


    GetterFactory<Object, SampleFieldKey> getterFactory = new GetterFactory<Object, SampleFieldKey>() {
        @SuppressWarnings("unchecked")
        @Override
        public <P> Getter<Object, P> newGetter(Type target, SampleFieldKey key, Object... properties) {

            if (target.equals(Date.class)) {
                return (Getter<Object, P>) new ConstantGetter<Object, Date>(DATE);
            } else if (target.equals(String.class)) {
                return (Getter<Object, P>) new ConstantGetter<Object, String>(STRING);
            }
            return null;
        }
    };

    MapperSource<Object, SampleFieldKey> mapperSource = new MapperSource<Object, SampleFieldKey>() {
        @Override
        public Class<Object> source() {
            return Object.class;
        }

        @Override
        public GetterFactory<Object, SampleFieldKey> getterFactory() {
            return getterFactory;
        }
    };
    @Test
    public void testAnonymousParameterWithDifferentType() throws Exception {

        FieldMapperMapperBuilder<Object, MyObjectWithInner, SampleFieldKey> fieldMapperMapperBuilder =
                new FieldMapperMapperBuilder<Object, MyObjectWithInner, SampleFieldKey>(
                        mapperSource,
                        classMeta,
                        MapperConfig.config(new IdentityFieldMapperColumnDefinitionProvider<SampleFieldKey>()),
                        new MappingContextFactoryBuilder<Object, SampleFieldKey>(null),
                        SampleFieldKey.KEY_FACTORY
                );



        fieldMapperMapperBuilder.addMapping(new SampleFieldKey("prop", 0, Date.class), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        Mapper<Object, MyObjectWithInner> mapper = fieldMapperMapperBuilder.mapper();

        MyObjectWithInner o = mapper.map(null);

        assertEquals(DATE, o.prop.date);
        assertNull(o.prop.str);
    }


    @Test
    public void testMapInnerObjectWithMapper() throws Exception {


        FieldMapperMapperBuilder<Object, MyObjectWithInner, SampleFieldKey> fieldMapperMapperBuilder =
                new FieldMapperMapperBuilder<Object, MyObjectWithInner, SampleFieldKey>(
                        mapperSource,
                        classMeta,
                        MapperConfig.config(new IdentityFieldMapperColumnDefinitionProvider<SampleFieldKey>()),
                                new MappingContextFactoryBuilder<Object, SampleFieldKey>(null),
                                SampleFieldKey.KEY_FACTORY
                                );

        fieldMapperMapperBuilder.addMapping(new SampleFieldKey("prop_date", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());


        Mapper<Object, MyObjectWithInner> mapper = fieldMapperMapperBuilder.mapper();

        MyObjectWithInner o = mapper.map(null);

        assertEquals(DATE, o.prop.date);
        assertNull(o.prop.str);
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