package  org.simpleflatmapper.core.map.impl;

import org.junit.Test;
import  org.simpleflatmapper.core.map.FieldMapper;
import  org.simpleflatmapper.core.map.GetterFactory;
import org.simpleflatmapper.core.map.Mapper;
import org.simpleflatmapper.core.map.MapperConfig;
import org.simpleflatmapper.core.map.column.ColumnProperty;
import org.simpleflatmapper.core.map.context.MappingContextFactoryBuilder;
import  org.simpleflatmapper.core.map.error.RethrowMapperBuilderErrorHandler;
import  org.simpleflatmapper.core.map.fieldmapper.ConstantSourceFieldMapperFactoryImpl;
import  org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import org.simpleflatmapper.core.map.mapper.ColumnDefinitionProvider;
import org.simpleflatmapper.core.map.mapper.FieldMapperMapperBuilder;
import org.simpleflatmapper.core.map.mapper.MapperSource;
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
import org.simpleflatmapper.core.utils.BiConsumer;
import org.simpleflatmapper.core.utils.Predicate;

import java.lang.reflect.Type;
import java.util.Date;

import static org.junit.Assert.*;


public class FieldMapperMapperBuilderTest {

    public static final Date DATE = new Date();
    public static final String STRING = "hello!";

    ClassMeta<MyObjectWithInner> classMeta = ReflectionService.disableAsm().getClassMeta(MyObjectWithInner.class);


    GetterFactory<Object, SampleFieldKey> getterFactory = new GetterFactory<Object, SampleFieldKey>() {
        @Override
        public <P> Getter<Object, P> newGetter(Type target, SampleFieldKey key, ColumnDefinition<?, ?> columnDefinition) {

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