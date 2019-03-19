package org.simpleflatmapper.test.map.impl;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.getter.ContextualGetterFactory;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.impl.IdentityFieldMapperColumnDefinitionProvider;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantSourceMapperBuilder;
import org.simpleflatmapper.map.mapper.MapperSource;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.tuple.Tuple3;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;


public class ConstantSourceMapperBuilderTest {

    public static final Date DATE = new Date();
    public static final String STRING = "hello!";

    private ClassMeta<MyObjectWithInner> classMeta = ReflectionService.disableAsm().getClassMeta(MyObjectWithInner.class);


    private GetterFactory<Object, SampleFieldKey> getterFactory = new GetterFactory<Object, SampleFieldKey>() {
        @SuppressWarnings("unchecked")
        @Override
        public <P> Getter<Object, P> newGetter(Type target, SampleFieldKey key, Object... properties) {

            if (key.getIndex() == 16) {
                return new Getter<Object, P>() {
                    @Override
                    public P get(Object target) throws Exception {
                        throw new RuntimeException("Error !");
                    }
                };
            }
            if (target.equals(Date.class)) {
                return (Getter<Object, P>) new ConstantGetter<Object, Date>(DATE);
            } else if (target.equals(String.class)) {
                return (Getter<Object, P>) new ConstantGetter<Object, String>(STRING);
            }
            return null;
        }
    };

    private MapperSource<Object, SampleFieldKey> mapperSource = new MapperSource<Object, SampleFieldKey>() {
        @Override
        public Class<Object> source() {
            return Object.class;
        }

        @Override
        public ContextualGetterFactory<Object, SampleFieldKey> getterFactory() {
            return new ContextualGetterFactoryAdapter<Object, SampleFieldKey>(getterFactory);
        }
    };
    @Test
    public void testAnonymousParameterWithDifferentType() throws Exception {

        ConstantSourceMapperBuilder<Object, MyObjectWithInner, SampleFieldKey> constantSourceMapperBuilder =
                ConstantSourceMapperBuilder.<Object, MyObjectWithInner, SampleFieldKey>newConstantSourceMapperBuilder(
                        mapperSource,
                        classMeta,
                        MapperConfig.config(new IdentityFieldMapperColumnDefinitionProvider<SampleFieldKey>()),
                        new MappingContextFactoryBuilder<Object, SampleFieldKey>(null, true),
                        SampleFieldKey.KEY_FACTORY
                );



        constantSourceMapperBuilder.addMapping(new SampleFieldKey("prop", 0, Date.class), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        SourceMapper<Object, MyObjectWithInner> mapper = constantSourceMapperBuilder.mapper();

        MyObjectWithInner o = mapper.map(null, null);

        assertEquals(DATE, o.prop.date);
        assertNull(o.prop.str);
    }


    @Test
    public void testMapInnerObjectWithMapper() throws Exception {


        ConstantSourceMapperBuilder<Object, MyObjectWithInner, SampleFieldKey> constantSourceMapperBuilder =
                ConstantSourceMapperBuilder.<Object, MyObjectWithInner, SampleFieldKey>newConstantSourceMapperBuilder(
                        mapperSource,
                        classMeta,
                        MapperConfig.config(new IdentityFieldMapperColumnDefinitionProvider<SampleFieldKey>()),
                                new MappingContextFactoryBuilder<Object, SampleFieldKey>(null, true),
                                SampleFieldKey.KEY_FACTORY
                                );

        constantSourceMapperBuilder.addMapping(new SampleFieldKey("prop_date", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());


        SourceMapper<Object, MyObjectWithInner> mapper = constantSourceMapperBuilder.mapper();

        MyObjectWithInner o = mapper.map(null, null);

        assertEquals(DATE, o.prop.date);
        assertNull(o.prop.str);
    }

    @Test
    public void testFieldMapperErrorHandler() throws  Exception{
        ConstantSourceMapperBuilder<Object, MyObjectWithInner, SampleFieldKey> constantSourceMapperBuilder =
                ConstantSourceMapperBuilder.<Object, MyObjectWithInner, SampleFieldKey>newConstantSourceMapperBuilder(
                        mapperSource,
                        classMeta,
                        MapperConfig.config(new IdentityFieldMapperColumnDefinitionProvider<SampleFieldKey>()).fieldMapperErrorHandler(new FieldMapperErrorHandler<SampleFieldKey>() {
                            @Override
                            public void errorMappingField(SampleFieldKey key, Object source, Object target, Exception error, Context mappingContext) throws MappingException {
                            }
                        }),
                        new MappingContextFactoryBuilder<Object, SampleFieldKey>(null, true),
                        SampleFieldKey.KEY_FACTORY
                );

        constantSourceMapperBuilder.addMapping(new SampleFieldKey("prop_date", 16), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        SourceMapper<Object, MyObjectWithInner> mapper = constantSourceMapperBuilder.mapper();

        MyObjectWithInner o = mapper.map(null, null);

        System.out.println("mapper = " + mapper);
        assertNull(o.prop.date);

    }


    public static class MyObjectWithInner {
        public MultiConstructorObject prop;
    }
    public static class MultiConstructorObject {
        private String str;
        private Date date;

        public MultiConstructorObject(String str) {
            this.str = str;
        }
        public MultiConstructorObject(Date date) {
            this.date = date;
        }


        public String getStr() {
            return str;
        }

        public Date getDate() {
            return date;
        }
    }

    @Test
    public void testIssue495() throws Exception {
        TypeReference<Tuple2<TableA, List<Tuple3<TableB, List<TableC>, List<Tuple2<TableD, List<TableE>>>>>>> typeReference = new TypeReference<Tuple2<TableA, List<Tuple3<TableB, List<TableC>, List<Tuple2<TableD, List<TableE>>>>>>>() {
        };
        ClassMeta<Tuple2<TableA, List<Tuple3<TableB, List<TableC>, List<Tuple2<TableD, List<TableE>>>>>>> classMeta = this.classMeta.getReflectionService().getClassMeta(typeReference.getType());

        PropertyFinder.PropertyFilter propertyFilter = PropertyFinder.PropertyFilter.trueFilter();
        PropertyFinder<Tuple2<TableA, List<Tuple3<TableB, List<TableC>, List<Tuple2<TableD, List<TableE>>>>>>> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<Tuple2<TableA, List<Tuple3<TableB, List<TableC>, List<Tuple2<TableD, List<TableE>>>>>>, Object> p1 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("variableName", 0, false, false), new Object[0], (TypeAffinity)null, propertyFilter);
        PropertyMeta<Tuple2<TableA, List<Tuple3<TableB, List<TableC>, List<Tuple2<TableD, List<TableE>>>>>>, Object> p2 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("variableName", 0, false, false), new Object[0], (TypeAffinity)null, propertyFilter);

        System.out.println("p1.getPath() = " + p1.getPath());
        System.out.println("p2.getPath() = " + p2.getPath());


    }


    @Test
    public void testIssue495Simple() throws Exception {
        TypeReference<List<Tuple3<Foo, Foo, Foo>>> typeReference = new TypeReference<List<Tuple3<Foo, Foo, Foo>>>() {
        };
        ClassMeta<List<Tuple3<Foo, Foo, Foo>>> classMeta = this.classMeta.getReflectionService().getClassMeta(typeReference.getType());

        PropertyFinder.PropertyFilter propertyFilter = PropertyFinder.PropertyFilter.trueFilter();
        PropertyFinder<?> propertyFinder = classMeta.newPropertyFinder();
        PropertyMeta<?, ?> p1 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("bar", 0, false, false), new Object[0], (TypeAffinity)null, propertyFilter);
        PropertyMeta<?, ?> p2 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("bar", 0, false, false), new Object[0], (TypeAffinity)null, propertyFilter);

        assertEquals("[0].element0.bar", p1.getPath());
        assertEquals("[0].element1.bar", p2.getPath());

        propertyFinder = classMeta.newPropertyFinder();
        p1 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt0_elt0_bar", 0, false, false), new Object[0], (TypeAffinity)null, propertyFilter);
        p2 = propertyFinder.findProperty(new DefaultPropertyNameMatcher("elt1_elt0_bar", 0, false, false), new Object[0], (TypeAffinity)null, propertyFilter);

        assertEquals("[0].element0.bar", p1.getPath());
        assertEquals("[1].element0.bar", p2.getPath());


    }
    
    public static class Foo {
        private final String bar;

        public Foo(String bar) {
            this.bar = bar;
        }
    }
    public static class TableA {
        private int idA;

        public int getIdA() {
            return idA;
        }

        public void setIdA(int idA) {
            this.idA = idA;
        }
    }
    public static class TableB {
        private int idB;
        private int idA;

        public int getIdB() {
            return idB;
        }

        public void setIdB(int idB) {
            this.idB = idB;
        }

        public int getIdA() {
            return idA;
        }

        public void setIdA(int idA) {
            this.idA = idA;
        }
    }
    public static class TableC {
        private int idC;
        private int idB;
        private String variableName;

        public int getIdC() {
            return idC;
        }

        public void setIdC(int idC) {
            this.idC = idC;
        }

        public int getIdB() {
            return idB;
        }

        public void setIdB(int idB) {
            this.idB = idB;
        }

        public String getVariableName() {
            return variableName;
        }

        public void setVariableName(String variableName) {
            this.variableName = variableName;
        }
    }
    public static class TableD {
        private int idD;
        private int idC;

        public int getIdD() {
            return idD;
        }

        public void setIdD(int idD) {
            this.idD = idD;
        }

        public int getIdC() {
            return idC;
        }

        public void setIdC(int idC) {
            this.idC = idC;
        }
    }
    public static class TableE {
        private int idE;
        private int idD;
        private String variableName;

        public int getIdE() {
            return idE;
        }

        public void setIdE(int idE) {
            this.idE = idE;
        }

        public int getIdD() {
            return idD;
        }

        public void setIdD(int idD) {
            this.idD = idD;
        }

        public String getVariableName() {
            return variableName;
        }

        public void setVariableName(String variableName) {
            this.variableName = variableName;
        }
    }

}