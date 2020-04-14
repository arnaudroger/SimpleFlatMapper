package org.simpleflatmapper.test.map.mapper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.CaseInsensitiveFieldKeyNamePredicate;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.property.FieldMapperProperty;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.map.property.MandatoryProperty;
import org.simpleflatmapper.reflect.ModifyInjectedParams;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.primitive.*;
import org.simpleflatmapper.test.beans.DbListObject;
import org.simpleflatmapper.test.beans.DbPrimitiveObject;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.mapper.*;
import org.simpleflatmapper.map.property.DefaultValueProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.test.beans.DbFinal1DeepObject;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbObjectConstructorAndSetter;
import org.simpleflatmapper.test.beans.DbPartialFinalObject;
import org.simpleflatmapper.test.beans.FinalObjectWith1ParamConstruction;
import org.simpleflatmapper.test.beans.FinalObjectWith1ParamConstructionWithLoop;
import org.simpleflatmapper.test.beans.ObjectWith1ParamConstruction;
import org.simpleflatmapper.test.beans.ObjectWith1ParamConstructionWithLoop;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

//IFJAVA8_START
import java.util.Optional;
//IFJAVA8_END


import static org.junit.Assert.*;

public class AbstractMapperBuilderTest {

    @Test
    public void testConversionDateToJodaTime() throws Exception {
        ClassMeta<List<DateTime>> classMeta =
                ReflectionService.disableAsm().<List<DateTime>>getClassMeta(new TypeReference<List<DateTime>>() {}.getType());

        EnumerableMapper<Object[][] , List<DateTime>, ?> mapper =
                new SampleMapperBuilder<List<DateTime>>(classMeta)
                    .addMapping(new SampleFieldKey("0", 0, new Class[0], Date.class)).mapper();

        Object[] objects = new Object[] { new Date() };
        List<DateTime> map = mapper.iterator(new Object[][] {objects}).next();
        assertEquals(objects[0], map.get(0).toDate());
    }

    @Test
    public void testConversionCharacterToJodaTime() throws Exception {
        ClassMeta<List<DateTime>> classMeta =
                ReflectionService.disableAsm().<List<DateTime>>getClassMeta(new TypeReference<List<DateTime>>() {}.getType());

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss.SSSS Z");

        EnumerableMapper<Object[][] , List<DateTime>, ?> mapper =
                new SampleMapperBuilder<List<DateTime>>(classMeta)
                        .addMapping(new SampleFieldKey("0", 0, new Class[0], String.class), dateTimeFormatter).mapper();

        DateTime now = DateTime.now();
        Object[] objects = new Object[] { dateTimeFormatter.print(now) };
        List<DateTime> map = mapper.iterator(new Object[][] {objects}).next();
        assertEquals(now, map.get(0));
    }

    //IFJAVA8_START
    @Test
    public void testOptionalDbObject() throws Exception {
        ClassMeta<Optional<DbObject>> classMeta =
                ReflectionService.disableAsm().<Optional<DbObject>>getClassMeta(new TypeReference<Optional<DbObject>>() {}.getType());
        EnumerableMapper<Object[][], Optional<DbObject>, ?> mapper =
                new SampleMapperBuilder<Optional<DbObject>>(classMeta)
                        .addMapping("id")
                        .addMapping("name")
                        .mapper();

        Optional<DbObject> map = mapper.iterator(new Object[][]{{1l, "name1"}}).next();

        assertEquals(1l, map.get().getId());
        assertEquals("name1", map.get().getName());
    }
    //IFJAVA8_END

    @Test
    public void testArrayDbObject() throws Exception {
        ClassMeta<DbObject[]> classMeta =
                ReflectionService.disableAsm().<DbObject[]>getClassMeta(DbObject[].class);
        EnumerableMapper<Object[][], DbObject[], ?> mapper =
                new SampleMapperBuilder<DbObject[]>(classMeta)
                        .addMapping("1_id")
                        .addMapping("1_name")
                        .addMapping("2_id")
                        .addMapping("2_name")
                        .mapper();

        DbObject[] map = mapper.iterator(new Object[][]{{1l, "name1", 2l, "name2"}}).next();

        assertEquals(3, map.length);
        assertEquals(1l, map[1].getId());
        assertEquals("name1", map[1].getName());
        assertEquals(2l, map[2].getId());
        assertEquals("name2", map[2].getName());
    }
    @Test
    public void testListDbObject() throws Exception {
        ClassMeta<List<DbObject>> classMeta =
                ReflectionService.disableAsm().<List<DbObject>>getClassMeta(new TypeReference<List<DbObject>>() {}.getType());
        EnumerableMapper<Object[][], List<DbObject>, ?> mapper =
                new SampleMapperBuilder<List<DbObject>>(classMeta)
                        .addMapping("1_id")
                        .addMapping("1_name")
                        .addMapping("2_id")
                        .addMapping("2_name")
                        .mapper();

        List<DbObject> map = mapper.iterator(new Object[][]{{1l, "name1", 2l, "name2"}}).next();

        assertEquals(3, map.size());
        assertEquals(1l, map.get(1).getId());
        assertEquals("name1", map.get(1).getName());
        assertEquals(2l, map.get(2).getId());
        assertEquals("name2", map.get(2).getName());
    }

    @Test
    public void testSetDbObject() throws Exception {
        ClassMeta<Set<DbObject>> classMeta =
                ReflectionService.disableAsm().<Set<DbObject>>getClassMeta(new TypeReference<Set<DbObject>>() {}.getType());
        EnumerableMapper<Object[][], Set<DbObject>, ?> mapper =
                new SampleMapperBuilder<Set<DbObject>>(classMeta)
                        .addMapping("1_id")
                        .addMapping("1_name")
                        .addMapping("2_id")
                        .addMapping("2_name")
                        .mapper();

        Set<DbObject> map = mapper.iterator(new Object[][]{{1l, "name1", 2l, "name2"}}).next();

        assertEquals(2, map.size());

        Iterator<DbObject> it = map.iterator();
        DbObject o = it.next();
        if (o.getId() == 1l) {
            assertEquals("name1", o.getName());

            o = it.next();
            assertEquals(2l, o.getId());
            assertEquals("name2", o.getName());
        } else {
            assertEquals("name2", o.getName());

            o = it.next();
            assertEquals(1l, o.getId());
            assertEquals("name1", o.getName());

        }
    }

    @Test
    public void testMapDbObject() throws Exception {
        ClassMeta<Map<Long, DbObject>> classMeta =
                ReflectionService.disableAsm().<Map<Long, DbObject>>getClassMeta(new TypeReference<Map<Long, DbObject>>() {}.getType());
        EnumerableMapper<Object[][], Map<Long, DbObject>, ?> mapper =
                new SampleMapperBuilder<Map<Long, DbObject>>(classMeta)
                        .addMapping("1_id")
                        .addMapping("1_name")
                        .addMapping("2_id")
                        .addMapping("2_name")
                        .mapper();

        Map<Long, DbObject> map = mapper.iterator(new Object[][]{{1l, "name1", 2l, "name2"}}).next();

        assertEquals(2, map.size());
        assertEquals(1l, map.get(1l).getId());
        assertEquals("name1", map.get(1l).getName());
        assertEquals(2l, map.get(2l).getId());
        assertEquals("name2", map.get(2l).getName());
    }
    @Test
    public void testFinalObjectWithOneConstructor() throws Exception {
        ClassMeta<FinalObjectWith1ParamConstruction> classMeta = ReflectionService.newInstance().<FinalObjectWith1ParamConstruction>getClassMeta(FinalObjectWith1ParamConstruction.class);

        EnumerableMapper<Object[][], FinalObjectWith1ParamConstruction, ?> mapper =
                new SampleMapperBuilder<FinalObjectWith1ParamConstruction>(classMeta)
                        .addMapping("id")
                        .addMapping("o1p")
                        .addMapping("o2p")
                        .mapper();

        FinalObjectWith1ParamConstruction map = mapper.iterator(new Object[][]{{1l, "v1", "v2"}}).next();

        assertEquals(1l, map.id);
        assertEquals("v1", map.o1p.getValue());
        assertEquals("v2", map.o2p.getO1p().getValue());
    }


    @Test
    public void testObjectWithOneConstructor() throws Exception {
        ClassMeta<ObjectWith1ParamConstruction> classMeta = ReflectionService.newInstance().<ObjectWith1ParamConstruction>getClassMeta(ObjectWith1ParamConstruction.class);

        EnumerableMapper<Object[][], ObjectWith1ParamConstruction, ?> mapper =
                new SampleMapperBuilder<ObjectWith1ParamConstruction>(classMeta)
                        .addMapping("id")
                        .addMapping("o1p")
                        .addMapping("o2p")
                        .mapper();

        ObjectWith1ParamConstruction map = mapper.iterator(new Object[][]{{1l, "v1", "v2"}}).next();

        assertEquals(1l, map.id);
        assertEquals("v1", map.o1p.getValue());
        assertEquals("v2", map.o2p.getO1p().getValue());
    }

    @Test
    public void testObjectWithOneConstructorWithLoop() {

        try {
            ClassMeta<ObjectWith1ParamConstructionWithLoop> classMeta = ReflectionService.newInstance().<ObjectWith1ParamConstructionWithLoop>getClassMeta(ObjectWith1ParamConstructionWithLoop.class);
            EnumerableMapper<Object[][], ObjectWith1ParamConstructionWithLoop, ?> mapper =
                    new SampleMapperBuilder<ObjectWith1ParamConstructionWithLoop>(classMeta)
                            .addMapping("id")
                            .addMapping("o1p")
                            .mapper();
            fail();
        } catch (MapperBuildingException e) {}



        try {
            ClassMeta<FinalObjectWith1ParamConstructionWithLoop> classMeta = ReflectionService.newInstance().<FinalObjectWith1ParamConstructionWithLoop>getClassMeta(FinalObjectWith1ParamConstructionWithLoop.class);
            EnumerableMapper<Object[][], FinalObjectWith1ParamConstructionWithLoop, ?> mapper =
                    new SampleMapperBuilder<FinalObjectWith1ParamConstructionWithLoop>(classMeta)
                            .addMapping("id")
                            .addMapping("o1p")
                            .mapper();
            fail();
        } catch (MapperBuildingException e) {}
    }
    @Test
    public void testDbFinal1DeepObject() throws Exception {

        ClassMeta<DbFinal1DeepObject> classMeta = ReflectionService.newInstance().<DbFinal1DeepObject>getClassMeta(DbFinal1DeepObject.class);

        EnumerableMapper<Object[][], DbFinal1DeepObject, ?> mapper =
                new SampleMapperBuilder<DbFinal1DeepObject>(classMeta)
                        .addMapping("id")
                        .addMapping("value")
                        .addMapping("dbObject_id")
                        .addMapping("dbObject_name")
                        .mapper();

        DbFinal1DeepObject map = mapper.iterator(new Object[][]{{1, "vvv", 2l, "wwww"}}).next();

        assertEquals(1, map.getId());
        assertEquals("vvv", map.getValue());
        assertEquals(2l, map.getDbObject().getId());
        assertEquals("wwww", map.getDbObject().getName());

    }

    @Test
    public void testJoinDbListObject() throws Exception {
        ClassMeta<DbListObject> classMeta = ReflectionService.newInstance(false).<DbListObject>getClassMeta(DbListObject.class);

        SampleMapperBuilder<DbListObject> builder = new SampleMapperBuilder<DbListObject>(classMeta);
        JoinMapper<Object[], Object[][], DbListObject, ?> mapper =
                (JoinMapper<Object[], Object[][], DbListObject, ?>) builder
                        .addKey("id")
                        .addKey("objects_id")
                        .addMapping("objects_name")
                        .mapper();


        checkDbListJoinMapper(mapper);

    }

    @Test
    public void testJoinDbListObjectMissingKey() throws Exception {
        ClassMeta<DbListObject> classMeta = ReflectionService.newInstance().<DbListObject>getClassMeta(DbListObject.class);

        JoinMapper<Object[], Object[][], DbListObject, ?> mapper =
                (JoinMapper<Object[], Object[][], DbListObject, ?>) new SampleMapperBuilder<DbListObject>(classMeta)
                        .addKey("id")
                        .addMapping("objects_id")
                        .addMapping("objects_name")
                        .mapper();


        checkDbListJoinMapper(mapper);

    }

    private void checkDbListJoinMapper(JoinMapper<Object[], Object[][], DbListObject, ?> mapper) throws Exception {
        Iterator<DbListObject> iterator = mapper.iterator(new Object[][]{
                {1, 1l, "n1"},
                {1, 2l, "n2"},
                {2, 1l, "n1"}
        });

        DbListObject dbListObject = iterator.next();

        assertEquals(1, dbListObject.getId());
        assertEquals(2, dbListObject.getObjects().size());
        assertEquals(1l, dbListObject.getObjects().get(0).getId());
        assertEquals("n1", dbListObject.getObjects().get(0).getName());
        assertEquals(2l, dbListObject.getObjects().get(1).getId());
        assertEquals("n2", dbListObject.getObjects().get(1).getName());

        dbListObject = iterator.next();
        assertEquals(2, dbListObject.getId());
        assertEquals(1, dbListObject.getObjects().size());
        assertEquals(1l, dbListObject.getObjects().get(0).getId());
        assertEquals("n1", dbListObject.getObjects().get(0).getName());
    }


    @Test
    public void testDefaultValue() throws Exception {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().<DbObject>getClassMeta(DbObject.class);


        FieldMapperColumnDefinitionProviderImpl<SampleFieldKey> definitionProvider = new FieldMapperColumnDefinitionProviderImpl<SampleFieldKey>();
        definitionProvider.addColumnProperty("type_name", new DefaultValueProperty<DbObject.Type>(DbObject.Type.type4));

        MapperConfig<SampleFieldKey, Object[]> mapperConfig =
                MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().columnDefinitions(definitionProvider);
        EnumerableMapper<Object[][], DbObject, ?> mapper =
                new SampleMapperBuilder<DbObject>(classMeta, mapperConfig)
                        .addMapping("id")
                        .mapper();
        Object[][] data = new Object[][] {{3l}};

        DbObject dbObject = mapper.iterator(data).next();

        assertEquals(3l, dbObject.getId());
        assertEquals(DbObject.Type.type4, dbObject.getTypeName());
    }



    @Test
    public void testDbObject() throws Exception {
        testDbObjectxxxMapper(new Supplier<DbObject>() {
            @Override
            public DbObject get() {
                return DbObject.newInstance();
            }
        }, true);
    }

    @Test
    public void testDbObjectConstructorAndSetter() throws Exception {
        testDbObjectxxxMapper(new Supplier<DbObjectConstructorAndSetter>() {
            @Override
            public DbObjectConstructorAndSetter get() {
                return DbObjectConstructorAndSetter.newInstance();
            }
        }, true);
    }

    @Test
    public void testDbFinalObject() throws Exception {
        testDbObjectxxxMapper(new Supplier<DbFinalObject>() {
            @Override
            public DbFinalObject get() {
                return DbFinalObject.newInstance();
            }
        }, false);
    }

    @Test
    public void testDbPartialFinalObject() throws Exception {
        testDbObjectxxxMapper(new Supplier<DbPartialFinalObject>() {
            @Override
            public DbPartialFinalObject get() {
                return DbPartialFinalObject.newInstance();
            }
        }, false);
    }
    
    @Test
    public void testMandatoryProperty461() {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
        MapperConfig<SampleFieldKey, Object[]> mapperConfig = MapperConfig.fieldMapperConfig();

        mapperConfig = mapperConfig.columnDefinitions(new ColumnDefinitionProvider<SampleFieldKey>() {
            @Override
            public FieldMapperColumnDefinition<SampleFieldKey> getColumnDefinition(SampleFieldKey key) {
                if (key.getName().equals("email")) {
                    return FieldMapperColumnDefinition.<SampleFieldKey>identity().add(MandatoryProperty.class);
                }
                return FieldMapperColumnDefinition.<SampleFieldKey>identity();
            }

            @Override
            public <CP, BC extends BiConsumer<Predicate<? super SampleFieldKey>, CP>> BC forEach(Class<CP> propertyType, BC consumer) {
                if (MandatoryProperty.class.equals(propertyType)) {
                    consumer.accept(CaseInsensitiveFieldKeyNamePredicate.of("email"), (CP) MandatoryProperty.DEFAULT);
                }
                return consumer;
            }
        });

        try {
            new SampleMapperBuilder<DbObject>(classMeta, mapperConfig).addMapping("id").mapper();
            fail();
        } catch (MissingPropertyException e) {
            // expected
        }
    }
    

    @Test
    public void testCustomization() throws Exception {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);

        EnumerableMapper<Object[][], DbObject, ?> mapper =
                new SampleMapperBuilder<DbObject>(classMeta)
                    .addKey("id")
                    .addMapper(new FieldMapper<Object[], DbObject>() {
                        @Override
                        public void mapTo(Object[] source, DbObject target, MappingContext<? super Object[]> context) throws Exception {
                            target.setName("fieldMapper");
                        }
                    })
                    .addMapping("email", new GetterProperty(new Getter<Object, String>() {
                        @Override
                        public String get(Object target) throws Exception {
                            return "getterEmail";
                        }
                    })).mapper();

        DbObject dbObject = mapper.iterator(new Object[][] { {1l} }).next();
        assertEquals(1, dbObject.getId());
        assertEquals("fieldMapper", dbObject.getName());
        assertEquals("getterEmail", dbObject.getEmail());


        mapper =
                new SampleMapperBuilder<DbObject>(classMeta)
                        .addKey("id")
                        .addMapping("email",
                                (Object)FieldMapperColumnDefinition.<SampleFieldKey>identity().add(
                                new GetterProperty(new Getter<Object, String>() {
                            @Override
                            public String get(Object target) throws Exception {
                                return "getterEmail";
                            }
                        })))
                         .addMapping("name",
                            FieldMapperColumnDefinition.<SampleFieldKey>identity().add(
                                    new GetterProperty(new Getter<Object, String>() {
                                        @Override
                                        public String get(Object target) throws Exception {
                                            return "getterName";
                                        }
                        }))).mapper();
        dbObject = mapper.iterator(new Object[][] { {1l}}).next();
        assertEquals(1, dbObject.getId());
        assertEquals("getterEmail", dbObject.getEmail());
        assertEquals("getterName", dbObject.getName());



    }


    @Test
    public void testNewSeparatorChar712() throws Exception {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);

        EnumerableMapper<Object[][], DbObject, ?> mapper =
                new SampleMapperBuilder<DbObject>(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().propertyNameMatcherFactory(DefaultPropertyNameMatcherFactory.DEFAULT.addSeparators('/')))
                        .addKey("id")
                        .addMapping("na/me")
                        .mapper();

        DbObject dbObject = mapper.iterator(new Object[][] { {1l, "hello"} }).next();
        assertEquals(1, dbObject.getId());
        assertEquals("hello", dbObject.getName());
    }

    private <T> void testDbObjectxxxMapper(Supplier<T> supplier, boolean mapTo) throws Exception {
        testDbObjectxxxMapper(supplier, mapTo, DbObject.HEADERS);
    }
    private <T> void testDbObjectxxxMapper(Supplier<T> supplier,  boolean useAsm, String[] headers) throws Exception {
        T instance1 = supplier.get();
        T instance2 = supplier.get();
        ClassMeta<T> classMeta = ReflectionService.newInstance(useAsm).<T>getClassMeta(instance1.getClass());

        SampleMapperBuilder<T> builder = new SampleMapperBuilder<T>(classMeta);
        SampleMapperBuilder<T> builderIndexed = new SampleMapperBuilder<T>(classMeta);

        Object[] row = new Object[headers.length];

        for(int i = 0; i < headers.length; i++) {
            String str = headers[i];
            builder.addMapping(str);
            builderIndexed.addMapping(str, i);
            row[i] = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of(str), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter()).getGetter().get(instance1);

        }
        EnumerableMapper<Object[][], T, ?> mapper = builder.mapper();

        assertEquals(instance1, mapper.iterator(new Object[][] {row}).next());
        Assert.assertEquals(instance1, builderIndexed.mapper().iterator(new Object[][] {row}).next());

        assertNotEquals(instance1, instance2);
        
    }

    @Test
    public void testImmutableSetManual() throws Exception {
        ClassMeta<A.Builder> classMeta = ReflectionService.newInstance(false).getClassMeta(A.Builder.class);
        SampleMapperBuilder<A.Builder> builder = new SampleMapperBuilder<A.Builder>(classMeta);

        EnumerableMapper<Object[][], A, ?> mapper = new TransformEnumerableMapper<Object, Object[][], A.Builder, A, Exception>(builder.addKey("id").addMapping("bs_v").mapper(),
                new Function<A.Builder, A>() {
                    @Override
                    public A apply(A.Builder builder) {
                        return builder.build();
                    }
                });

        A a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);


    }
    
    @Test
    public void testImmutableSetWithBuilder() throws Exception {

        SampleMapperBuilder<A> builderA = new SampleMapperBuilder<A>(ReflectionService.newInstance(false).getClassMeta(A.class),  MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().assumeInjectionModifiesValues(true));
        
        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A, ?> mapper =
                builderA.mapper();

        A a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }


    @Test
    public void testImmutableSetWithBuilderAsm() throws Exception {

        SampleMapperBuilder<A> builderA = new SampleMapperBuilder<A>(ReflectionService.newInstance(true).getClassMeta(A.class),  MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().assumeInjectionModifiesValues(true));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A, ?> mapper =
                builderA.mapper();

        A a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }
    @Test
    public void testImmutableSetNoBuilder() throws Exception {

        SampleMapperBuilder<A2> builderA = new SampleMapperBuilder<A2>(ReflectionService.newInstance(false).getClassMeta(A2.class),  MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().assumeInjectionModifiesValues(true));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A2, ?> mapper =
                builderA.mapper();

        A2 a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }

    @Test
    public void testImmutableSetNoBuilderAsm() throws Exception {

        SampleMapperBuilder<A2> builderA = new SampleMapperBuilder<A2>(ReflectionService.newInstance(true).getClassMeta(A2.class), MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().assumeInjectionModifiesValues(true));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A2, ?> mapper =
                builderA.mapper();

        A2 a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }

    @Test
    public void testObjectWithMappingContext() throws Exception {
        SampleMapperBuilder<ObjectContext> builderA = new SampleMapperBuilder<ObjectContext>(ReflectionService.newInstance(true).getClassMeta(ObjectContext.class), MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig());

        SetRowMapper<Object[], Object[][], ObjectContext, Exception> mapper = builderA.addKey("foo").mapper();
        ObjectContext a = mapper.iterator(new Object[][]{{"v1"}}).next();

        assertEquals("v1", a.foo);

        builderA = new SampleMapperBuilder<ObjectContext>(ReflectionService.newInstance(true).getClassMeta(ObjectContext.class), MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig());

        mapper = builderA.addKey("bar").mapper();
        a = mapper.iterator(new Object[][]{{12234l}}).next();
        assertEquals(12234l, a.bar);
    }
    
    public static class ObjectContext {
        private final String foo;
        private final long bar;
        private final Context context;

        public ObjectContext(long bar) {
            this.foo = null;
            this.bar = bar;
            this.context = null;
        }
        public ObjectContext(String foo) {
            throw new NullPointerException();
        }

        public ObjectContext(String foo, Context context) {
            this.foo = foo;
            this.bar = -1;
            this.context = context;
        }
    }

    @Test
    public void testImmutableListNoBuilderAsm() throws Exception {

        SampleMapperBuilder<A3> builderA = new SampleMapperBuilder<A3>(ReflectionService.newInstance(true).getClassMeta(A3.class), MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().assumeInjectionModifiesValues(true));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A3, ?> mapper =
                builderA.mapper();

        A3 a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(Arrays.asList(new B("v1"), new B("v2")), a.bs);

    }

    @Test
    public void testImmutableListNoBuilderAsmAnnotation() throws Exception {

        SampleMapperBuilder<A4> builderA = new SampleMapperBuilder<A4>(ReflectionService.newInstance(true).getClassMeta(A4.class));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A4, ?> mapper =
                builderA.mapper();

        A4 a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(Arrays.asList(new B("v1"), new B("v2")), a.bs);

    }

    public static class A2 {
        private final int id;
        private final Set<B> bs;

        public A2(int id, Set<B> bs) {
            this.id = id;
            this.bs = Collections.unmodifiableSet(new HashSet<B>(bs));
        }

        public Set<B> getBs() {
            return bs;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "A2{" +
                    "id=" + id +
                    ", bs=" + bs +
                    '}';
        }

    }

    public static class A3 {
        private final int id;
        private final List<B> bs;

        public A3(int id, Set<B> bs) {
            this.id = id;
            this.bs = Collections.unmodifiableList(new ArrayList<B>(bs));
        }

        public List<B> getBs() {
            return bs;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "A3{" +
                    "id=" + id +
                    ", bs=" + bs +
                    '}';
        }

    }

    @ModifyInjectedParams
    public static class A4 {
        private final int id;
        private final List<B> bs;

        public A4(int id, Set<B> bs) {
            this.id = id;
            this.bs = Collections.unmodifiableList(new ArrayList<B>(bs));
        }

        public List<B> getBs() {
            return bs;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "A4{" +
                    "id=" + id +
                    ", bs=" + bs +
                    '}';
        }

    }
    
    public static class A {
        private final int id;
        private final Set<B> bs;

        private A(int id, Set<B> bs) {
            this.id = id;
            this.bs = Collections.unmodifiableSet(new HashSet<B>(bs));
        }

        public Set<B> getBs() {
            return bs;
        }

        public int getId() {
            return id;
        }

        @Override
        public String toString() {
            return "A{" +
                    "id=" + id +
                    ", bs=" + bs +
                    '}';
        }
        public static Builder builder() {
            return new Builder();
        }
        public static class Builder {
            private int id;
            private Set<B> bs;

            public void setId(int id) {
                this.id = id;
            }

            public void setBs(Set<B> bs) {
                this.bs = bs;
            }
            public A build() {
                return new A(id, bs);
            }
        }
    }
    public static class B {
        private final String v;

        public B(String v) {
            this.v = v;
        }


        @Override
        public String toString() {
            return "B{" +
                    "v='" + v + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            B b = (B) o;

            return v != null ? v.equals(b.v) : b.v == null;
        }

        @Override
        public int hashCode() {
            return v != null ? v.hashCode() : 0;
        }
    }


    @Test
    public void testJoinUnordere604() throws Exception {

        SampleMapperBuilder<Prof> builderA = 
                new SampleMapperBuilder<Prof>(
                        ReflectionService.newInstance().getClassMeta(Prof.class),
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().unorderedJoin(true));

        builderA.addKey("id");
        builderA.addMapping("name");
        builderA.addKey("students_id");
        builderA.addMapping("students_name");

        SetRowMapper<Object[], Object[][], Prof, Exception> mapper = builderA.mapper();

        List<Prof> profs = mapper.forEach(new Object[][]{
                {1l, "prof1", 1l, "S1"},
                {2l, "prof2", 3l, "S3"},
                {1l, "prof1", 2l, "S2"},
                {2l, "prof2", 4l, "S4"},
                {3l, "prof3", 4l, "S4"},
                {3l, "prof3", 4l, "S4"},
        }, new ListCollector<Prof>()).getList();

        
        boolean test = false;
        assertEquals(Arrays.asList(
                new Prof(1l, "prof1", Arrays.asList(new Student(1l, "S1", test), new Student(2l, "S2", test))),
                new Prof(2l, "prof2", Arrays.asList(new Student(3l, "S3", test), new Student(4l, "S4", test))),
                new Prof(3l, "prof3", Arrays.asList(new Student(4l, "S4", test)))
        ), profs);
        

    }

    @Test
    public void testJoinUnordere604_2() throws Exception {

        SampleMapperBuilder<Prof> builderA =
                new SampleMapperBuilder<Prof>(
                        ReflectionService.newInstance().getClassMeta(Prof.class),
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().unorderedJoin(true));

        builderA.addKey("id");
        builderA.addMapping("name");
        builderA.addKey("students_id");
        builderA.addMapping("students_name");

        SetRowMapper<Object[], Object[][], Prof, Exception> mapper = builderA.mapper();

        List<Prof> profs = mapper.forEach(new Object[][]{
                {1l, "prof1", 1l, "S1"},
                {2l, "prof2", 1l, "S1"},
        }, new ListCollector<Prof>()).getList();


        boolean test = false;
        assertEquals(Arrays.asList(
                new Prof(1l, "prof1", Arrays.asList(new Student(1l, "S1", test))),
                new Prof(2l, "prof2", Arrays.asList(new Student(1l, "S1", test)))
        ), profs);


    }

    @Test
    public void testJoinUnordere606() throws Exception {

        SampleMapperBuilder<Prof> builderA =
                new SampleMapperBuilder<Prof>(
                        ReflectionService.newInstance().getClassMeta(Prof.class),
                        MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().unorderedJoin(true));

        builderA.addKey("id");
        builderA.addMapping("name");
        builderA.addKey("students_id");
        builderA.addMapping("students_name");
        builderA.addMapping("students_test");

        SetRowMapper<Object[], Object[][], Prof, Exception> mapper = builderA.mapper();

        List<Prof> profs = mapper.forEach(new Object[][]{
                {1l, "prof1", 1l, "S1", true},
                {2l, "prof2", 1l, "S1", false},
        }, new ListCollector<Prof>()).getList();


        assertEquals(Arrays.asList(
                new Prof(1l, "prof1", Arrays.asList(new Student(1l, "S1", true))),
                new Prof(2l, "prof2", Arrays.asList(new Student(1l, "S1", false)))
        ), profs);


    }
    
    public static class Prof {
        public final long id;
        public final String name;
        public final List<Student> students;

        public Prof(long id, String name, List<Student> students) {
            this.id = id;
            this.name = name;
            this.students = students;
        }

        @Override
        public String toString() {
            return "Prof{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    ", students=" + students +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Prof prof = (Prof) o;

            if (id != prof.id) return false;
            if (name != null ? !name.equals(prof.name) : prof.name != null) return false;
            return students != null ? students.equals(prof.students) : prof.students == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (students != null ? students.hashCode() : 0);
            return result;
        }
    }
    
    public static class Student {
        public final long id;
        public final String name;
        public final boolean test;

        public Student(long id, String name, boolean test) {
            this.id = id;
            this.name = name;
            this.test = test;
        }

        @Override
        public String toString() {
            return "Student{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Student student = (Student) o;

            if (id != student.id) return false;
            if (test != student.test) return false;
            return name != null ? name.equals(student.name) : student.name == null;
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            result = 31 * result + (test ? 1 : 0);
            return result;
        }
    }

    @Test
    // https://github.com/arnaudroger/SimpleFlatMapper/issues/416
    public void testEmptyAndFullConstructor() {
        AbstractMapperFactoryTest.MapperFactory mapperFactory = new AbstractMapperFactoryTest.MapperFactory();


        testCanBuildMapper(mapperFactory.getClassMeta(new TypeReference<Tuple2<AA, String>>() { }));
        testCanBuildMapper(mapperFactory.getClassMeta(new TypeReference<List<AA>>() { }));
        //IFJAVA8_START
        testCanBuildMapper(mapperFactory.getClassMeta(new TypeReference<Optional<AA>>() { }));
        //IFJAVA8_END

        testCanBuildMapper(mapperFactory.getClassMeta(new TypeReference<Map<String, AA>>() { }), "aa_");

    }
    private <T> void testCanBuildMapper(ClassMeta<T> classMeta) {
        testCanBuildMapper(classMeta, "");
    }
    private <T> void testCanBuildMapper(ClassMeta<T> classMeta, String prefix) {
        SampleMapperBuilder<T> builderTuple = new SampleMapperBuilder<T>(classMeta);

        assertNotNull(
                builderTuple
                        .addMapping(prefix + "id")
                        .addMapping(prefix + "value")
                        .mapper()); // fails
    }
    
    

    public static class AA {
        private String value;
        private int id;

        public AA() {
        }
        public AA(int id, String value) {
            this.id = id;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }


    public static final GetterFactory<Object[], SampleFieldKey> GETTER_FACTORY = new GetterFactory<Object[], SampleFieldKey>() {
        @Override
        public <P> Getter<Object[], P> newGetter(Type target, final SampleFieldKey key, Object... properties) {
            Class<?> aClass = TypeHelper.toClass(target);
            Package p = aClass.getPackage();

            if(aClass.isArray()) {
                return new SampleGetter<P>(key);
            }
            if (aClass.equals(List.class)) return null;
            if (!Enum.class.isAssignableFrom(aClass) && !aClass.isPrimitive() &&(p == null || ! p.getName().startsWith("java"))) return null;
            
            if (aClass.isPrimitive()) {
                if (boolean.class.equals(aClass)) {
                    return (Getter<Object[], P>) new SampleBooleanGetter(key);
                }
                if (byte.class.equals(aClass)) {
                    return (Getter<Object[], P>) new SampleByteGetter(key);
                }
                if (short.class.equals(aClass)) {
                    return (Getter<Object[], P>) new SampleShortGetter(key);
                }
                if (int.class.equals(aClass)) {
                    return (Getter<Object[], P>) new SampleIntGetter(key);
                }
                if (long.class.equals(aClass)) {
                    return (Getter<Object[], P>) new SampleLongGetter(key);
                }
                if (char.class.equals(aClass)) {
                    return (Getter<Object[], P>) new SampleCharacterGetter(key);
                }
                if (float.class.equals(aClass)) {
                    return (Getter<Object[], P>) new SampleFloatGetter(key);
                }
                if (double.class.equals(aClass)) {
                    return (Getter<Object[], P>) new SampleDoubleGetter(key);
                }
            }
            return new SampleGetter<P>(key);
        }
    };

    public static final KeyFactory<SampleFieldKey> KEY_FACTORY = new KeyFactory<SampleFieldKey>() {
        @Override
        public SampleFieldKey newKey(String name, int i) {
            return new SampleFieldKey(name, i);
        }
    };
    public static class SampleMapperFactory extends AbstractMapperFactory<SampleFieldKey, SampleMapperFactory, Object[]> {

        public static SampleMapperFactory newInstance() {
            return new SampleMapperFactory();
        }

        public static SampleMapperFactory newInstance(
                AbstractMapperFactory<SampleFieldKey, ?, Object[]> config) {
            return new SampleMapperFactory(config);
        }
        
        public SampleMapperFactory() {
            super(new FieldMapperColumnDefinitionProviderImpl<SampleFieldKey>(), FieldMapperColumnDefinition.<SampleFieldKey>identity(), new ContextualGetterFactoryAdapter<Object[], SampleFieldKey>(GETTER_FACTORY));
        }

        public SampleMapperFactory(AbstractMapperFactory<SampleFieldKey, ?, Object[]> config) {
            super(config);
        }

        public <T> SampleMapperBuilder<T> newBuilder(ClassMeta<T> classMeta) {
            return new SampleMapperBuilder<T>(
                    classMeta, mapperConfig(classMeta.getType())
            );
        }
    }
    
    
    public static class SampleMapperBuilder<T> extends MapperBuilder<Object[], Object[][], T, SampleFieldKey, Exception, SetRowMapper<Object[], Object[][], T, Exception>, SetRowMapper<Object[], Object[][], T, Exception>, SampleMapperBuilder<T>> {

        public static final KeySourceGetter<SampleFieldKey, Object[]> KEY_SOURCE_GETTER = new KeySourceGetter<SampleFieldKey, Object[]>() {
            @Override
            public Object getValue(SampleFieldKey key, Object[] source) throws Exception {
                return source[key.getIndex()];
            }
        };

        public SampleMapperBuilder(ClassMeta<T> classMeta, MapperConfig<SampleFieldKey, Object[]> mapperConfig) {
            super(KEY_FACTORY, 
                    new DefaultSetRowMapperBuilder<Object[], Object[][], T, SampleFieldKey, Exception>(
                            classMeta,
                            new MappingContextFactoryBuilder<Object[], SampleFieldKey>(KEY_SOURCE_GETTER, !mapperConfig.unorderedJoin()), 
                            mapperConfig,
                            new MapperSourceImpl<Object[], SampleFieldKey>(Object[].class, new ContextualGetterFactoryAdapter<Object[], SampleFieldKey>(GETTER_FACTORY)),
                            KEY_FACTORY,
                            new UnaryFactory<Object[][], Enumerable<Object[]>>() {
                                @Override
                                public Enumerable<Object[]> newInstance(final Object[][] objects) {
                                    return new Enumerable<Object[]>() {
                                        int i = -1;

                                        @Override
                                        public boolean next() {
                                            int n = i + 1;
                                            if (n < objects.length) {
                                                i = n;
                                                return true;

                                            }
                                            return false;
                                        }

                                        @Override
                                        public Object[] currentValue() {
                                            return objects[i];
                                        }
                                    };
                                }
                            },

                            KEY_SOURCE_GETTER),
                    new BiFunction<SetRowMapper<Object[], Object[][], T, Exception>, List<SampleFieldKey>, SetRowMapper<Object[], Object[][], T, Exception>>() {
                        @Override
                        public SetRowMapper<Object[], Object[][], T, Exception> apply(SetRowMapper<Object[], Object[][], T, Exception> setRowMapper, List<SampleFieldKey> keys) {
                            return setRowMapper;
                        }
                    },
                    FieldMapperColumnDefinition.<SampleFieldKey>factory(), 0);
        }
        public SampleMapperBuilder(ClassMeta<T> classMeta) {
            this(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig());
        }

    }

    private static class SampleGetter<P> implements Getter<Object[], P> {
        private final SampleFieldKey key;

        public SampleGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public P get(Object[] target) throws Exception {
            return (P) target[key.getIndex()];
        }
    }

    private static class SampleBooleanGetter implements Getter<Object[], Boolean>, BooleanGetter<Object[]> {
        private final SampleFieldKey key;

        public SampleBooleanGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public Boolean get(Object[] target) throws Exception {
            return (Boolean) target[key.getIndex()];
        }

        @Override
        public boolean getBoolean(Object[] target) throws Exception {
            return (Boolean) target[key.getIndex()];
        }
    }

    private static class SampleByteGetter implements Getter<Object[], Byte>, ByteGetter<Object[]> {
        private final SampleFieldKey key;

        public SampleByteGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public Byte get(Object[] target) throws Exception {
            return (Byte) target[key.getIndex()];
        }

        @Override
        public byte getByte(Object[] target) throws Exception {
            return (Byte) target[key.getIndex()];
        }
    }

    private static class SampleCharacterGetter implements Getter<Object[], Character>, CharacterGetter<Object[]> {
        private final SampleFieldKey key;

        public SampleCharacterGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public Character get(Object[] target) throws Exception {
            return (Character) target[key.getIndex()];
        }

        @Override
        public char getCharacter(Object[] target) throws Exception {
            return (Character) target[key.getIndex()];
        }
    }
    private static class SampleShortGetter implements Getter<Object[], Short>, ShortGetter<Object[]> {
        private final SampleFieldKey key;

        public SampleShortGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public Short get(Object[] target) throws Exception {
            return (Short) target[key.getIndex()];
        }

        @Override
        public short getShort(Object[] target) throws Exception {
            return (Short) target[key.getIndex()];
        }
    }
    private static class SampleIntGetter implements Getter<Object[], Integer>, IntGetter<Object[]> {
        private final SampleFieldKey key;

        public SampleIntGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public Integer get(Object[] target) throws Exception {
            return (Integer) target[key.getIndex()];
        }

        @Override
        public int getInt(Object[] target) throws Exception {
            return (Integer) target[key.getIndex()];
        }
    }
    private static class SampleLongGetter implements Getter<Object[], Long>, LongGetter<Object[]> {
        private final SampleFieldKey key;

        public SampleLongGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public Long get(Object[] target) throws Exception {
            return (Long) target[key.getIndex()];
        }

        @Override
        public long getLong(Object[] target) throws Exception {
            return (Long) target[key.getIndex()];
        }
    }
    private static class SampleFloatGetter implements Getter<Object[], Float>, FloatGetter<Object[]> {
        private final SampleFieldKey key;

        public SampleFloatGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public Float get(Object[] target) throws Exception {
            return (Float) target[key.getIndex()];
        }

        @Override
        public float getFloat(Object[] target) throws Exception {
            return (Float) target[key.getIndex()];
        }
    }
    private static class SampleDoubleGetter implements Getter<Object[], Double>, DoubleGetter<Object[]> {
        private final SampleFieldKey key;

        public SampleDoubleGetter(SampleFieldKey key) {
            this.key = key;
        }

        @Override
        public Double get(Object[] target) throws Exception {
            return (Double) target[key.getIndex()];
        }

        @Override
        public double getDouble(Object[] target) throws Exception {
            return (Double) target[key.getIndex()];
        }
    }
    
    
    @Test
    public void testPrimitive() {

        Object[] data = {true, (byte) 1, (char) 2, (short) 3, 4, (long) 5, (float) 6, (double) 7};


        SetRowMapper<Object[], Object[][], DbPrimitiveObject, Exception> mapper;
        DbPrimitiveObject o;
        
        mapper = addPrimitiveFields(new SampleMapperBuilder<DbPrimitiveObject>(ReflectionService.newInstance().getClassMeta(DbPrimitiveObject.class)));
        o = mapper.map(data);
        validatePrimitiveData(o);


        final List<SampleFieldKey> errors = new ArrayList<SampleFieldKey>();
        MapperConfig<SampleFieldKey, Object[]> mapperConfig = MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().fieldMapperErrorHandler(new FieldMapperErrorHandler<SampleFieldKey>() {
            @Override
            public void errorMappingField(SampleFieldKey key, Object source, Object target, Exception error, Context mappingContext) throws MappingException {
                errors.add(key);
            }
        });
        ReflectionService reflectionService = ReflectionService.newInstance(false);
        mapper = addPrimitiveFields(new SampleMapperBuilder<DbPrimitiveObject>(reflectionService.getClassMeta(DbPrimitiveObject.class), mapperConfig));
        o = mapper.map(data);
        validatePrimitiveData(o);
        assertTrue(errors.isEmpty());
        
        o = mapper.map(new Object[0]);
        assertEquals(8, errors.size());
        assertNotNull(o);

    }

    private void validatePrimitiveData(DbPrimitiveObject map) {
        assertTrue(map.ispBoolean());
        assertEquals(1, map.getpByte());
        assertEquals(2, map.getpCharacter());
        assertEquals(3, map.getpShort());
        assertEquals(4, map.getpInt());
        assertEquals(5, map.getpLong());
        assertEquals(6, map.getpFloat(), 0.00001);
        assertEquals(7, map.getpDouble(), 0.000001);
    }

    private SetRowMapper<Object[], Object[][], DbPrimitiveObject, Exception> addPrimitiveFields(SampleMapperBuilder<DbPrimitiveObject> builderTuple) {
        builderTuple.addMapping("pBoolean");
        builderTuple.addMapping("pByte");
        builderTuple.addMapping("pCharacter");
        builderTuple.addMapping("pShort");
        builderTuple.addMapping("pInt");
        builderTuple.addMapping("pLong");
        builderTuple.addMapping("pFloat");
        builderTuple.addMapping("pDouble");
        return builderTuple.mapper();
    }
    
    @Test
    public void testFieldMapperProperty() {
        SampleMapperBuilder<DbObject> mapperBuilder = new SampleMapperBuilder<DbObject>(ReflectionService.newInstance().getClassMeta(DbObject.class));
        
        mapperBuilder.addMapping("id");
        mapperBuilder.addMapping("name", new FieldMapperProperty(new FieldMapper<Object[], DbObject>() {
            @Override
            public void mapTo(Object[] source, DbObject target, MappingContext<? super Object[]> context) throws Exception {
                target.setName("n" + source[0]);                
            }
        }));

        SetRowMapper<Object[], Object[][], DbObject, Exception> mapper = mapperBuilder.mapper();

        DbObject map = mapper.map(new Object[]{1l});
        
        assertEquals(1l, map.getId());
        assertEquals("n1", map.getName());


    }




    @Test
    public void test543() throws Exception {
        final String VALUES = "values";

        SampleFieldKey valuesKeys = new SampleFieldKey(VALUES, 1);
        SetRowMapper<Object[], Object[][], C543, Exception> mapper = new SampleMapperBuilder<C543>(ReflectionService.newInstance().getClassMeta(C543.class)).addMapping(valuesKeys).mapper();
        List<C543> list = mapper.forEach(new Object[][]{{"", "v1"}}, new ListCollector<C543>()).getList();
        assertEquals(1, list.size());
        assertEquals("v1", list.get(0).values.get(0));
    }

    @Test
    public void test543_NamedParam() throws Exception {
        final String VALUES = "values";

        SampleFieldKey valuesKeys = new SampleFieldKey(VALUES, 1);

        SetRowMapper<Object[], Object[][], C543_NamedParam, Exception> mapper = new SampleMapperBuilder<C543_NamedParam>(ReflectionService.newInstance().getClassMeta(C543_NamedParam.class)).addMapping(valuesKeys).mapper();
        List<C543_NamedParam> list = mapper.forEach(new Object[][]{{"", "v1"}}, new ListCollector<C543_NamedParam>()).getList();
        assertEquals(1, list.size());
        assertEquals("v1", list.get(0).values.get(0).name);
    }

    @Test
    public void testGenericBuilderWithSubMapper() throws Exception {
        SampleMapperBuilder<C543_NamedParam> builder = new SampleMapperBuilder<C543_NamedParam>(ReflectionService.newInstance().disableAsm().getClassMeta(C543_NamedParam.class), MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().assumeInjectionModifiesValues(true));
        builder.addMapping("id_id", KeyProperty.DEFAULT);
        builder.addMapping("values_name", KeyProperty.DEFAULT);

        SetRowMapper<Object[], Object[][], C543_NamedParam, Exception> mapper = builder.mapper();

        C543_NamedParam c = mapper.iterator(new Object[][]{{1, "n"}}).next();
        
        assertEquals(1, c.id.getId());
        assertEquals("n", c.values.get(0).name);
    }

    @Test
    public void test624() {
        SampleMapperBuilder<C543_NamedParam> builder = new SampleMapperBuilder<C543_NamedParam>(ReflectionService.newInstance().disableAsm().getClassMeta(C543_NamedParam.class), MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().assumeInjectionModifiesValues(true));
        builder.addMapping("id_id", KeyProperty.DEFAULT);
        builder.addMapping("values_name", KeyProperty.DEFAULT);

        SetRowMapper<Object[], Object[][], C543_NamedParam, Exception> mapper = builder.mapper();

        C543_NamedParam c = mapper.map(new Object[]{1, "n"});
        assertEquals(1, c.id.getId());
        assertEquals("n", c.values.get(0).name);
    }
    
    public static class C543 {
        private final List<String> values;

        public C543(List<String> values) {
            this.values = values;
        }
    }

    public static class C543_NamedParam {
        private final C542_Id id;
        private final List<C543Elt> values;

        public C543_NamedParam(Context context, C542_Id id, List<C543Elt> values) {
            if (context == null ) throw new NullPointerException();
            this.id = id;
            this.values = values;
        }
    }

    public static class C543Elt {
        private final String name;

        public C543Elt(String name) {
            this.name = name;
        }
    }

    public static class C542_Id {
        private final int id;

        public C542_Id(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }
}