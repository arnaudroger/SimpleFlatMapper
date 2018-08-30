package org.simpleflatmapper.test.map.mapper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.map.CaseInsensitiveFieldKeyNamePredicate;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.getter.ContextualGetterFactoryAdapter;
import org.simpleflatmapper.map.property.MandatoryProperty;
import org.simpleflatmapper.reflect.ModifyInjectedParams;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.test.beans.DbListObject;
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
import org.simpleflatmapper.util.BiConsumer;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.UnaryFactory;

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

        MapperConfig<SampleFieldKey> mapperConfig =
                MapperConfig.<SampleFieldKey>fieldMapperConfig().columnDefinitions(definitionProvider);
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
        MapperConfig<SampleFieldKey> mapperConfig = MapperConfig.fieldMapperConfig();

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
            row[i] = classMeta.newPropertyFinder(ConstantPredicate.<PropertyMeta<?, ?>>truePredicate()).findProperty(DefaultPropertyNameMatcher.of(str), new Object[0], (TypeAffinity)null).getGetter().get(instance1);

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

        SampleMapperBuilder<A> builderA = new SampleMapperBuilder<A>(ReflectionService.newInstance(false).getClassMeta(A.class),  MapperConfig.<SampleFieldKey>fieldMapperConfig().assumeInjectionModifiesValues(true));
        
        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A, ?> mapper =
                builderA.mapper();

        A a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }


    @Test
    public void testImmutableSetWithBuilderAsm() throws Exception {

        SampleMapperBuilder<A> builderA = new SampleMapperBuilder<A>(ReflectionService.newInstance(true).getClassMeta(A.class),  MapperConfig.<SampleFieldKey>fieldMapperConfig().assumeInjectionModifiesValues(true));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A, ?> mapper =
                builderA.mapper();

        A a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }
    @Test
    public void testImmutableSetNoBuilder() throws Exception {

        SampleMapperBuilder<A2> builderA = new SampleMapperBuilder<A2>(ReflectionService.newInstance(false).getClassMeta(A2.class),  MapperConfig.<SampleFieldKey>fieldMapperConfig().assumeInjectionModifiesValues(true));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A2, ?> mapper =
                builderA.mapper();

        A2 a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }

    @Test
    public void testImmutableSetNoBuilderAsm() throws Exception {

        SampleMapperBuilder<A2> builderA = new SampleMapperBuilder<A2>(ReflectionService.newInstance(true).getClassMeta(A2.class), MapperConfig.<SampleFieldKey>fieldMapperConfig().assumeInjectionModifiesValues(true));

        builderA.addKey("id").addMapping("bs_v");

        EnumerableMapper<Object[][], A2, ?> mapper =
                builderA.mapper();

        A2 a = mapper.iterator(new Object[][]{{1, "v1"}, {1, "v2"}}).next();

        assertEquals(new HashSet<B>(Arrays.asList(new B("v1"), new B("v2"))), a.bs);

    }

    @Test
    public void testImmutableListNoBuilderAsm() throws Exception {

        SampleMapperBuilder<A3> builderA = new SampleMapperBuilder<A3>(ReflectionService.newInstance(true).getClassMeta(A3.class), MapperConfig.<SampleFieldKey>fieldMapperConfig().assumeInjectionModifiesValues(true));

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
            if (!Enum.class.isAssignableFrom(aClass) && !aClass.isPrimitive() &&(p == null || ! p.getName().startsWith("java"))) return null;
            return new Getter<Object[], P>() {
                @Override
                public P get(Object[] target) throws Exception {
                    return (P) target[key.getIndex()];
                }
            };
        }
    };

    public static final KeyFactory<SampleFieldKey> KEY_FACTORY = new KeyFactory<SampleFieldKey>() {
        @Override
        public SampleFieldKey newKey(String name, int i) {
            return new SampleFieldKey(name, i);
        }
    };

    public static class SampleMapperBuilder<T> extends MapperBuilder<Object[], Object[][], T, SampleFieldKey, Exception, SetRowMapper<Object[], Object[][], T, Exception>, SetRowMapper<Object[], Object[][], T, Exception>, SampleMapperBuilder<T>> {

        public static final KeySourceGetter<SampleFieldKey, Object[]> KEY_SOURCE_GETTER = new KeySourceGetter<SampleFieldKey, Object[]>() {
            @Override
            public Object getValue(SampleFieldKey key, Object[] source) throws Exception {
                return source[key.getIndex()];
            }
        };

        public SampleMapperBuilder(ClassMeta<T> classMeta, MapperConfig<SampleFieldKey> mapperConfig) {
            super(KEY_FACTORY, 
                    new DefaultSetRowMapperBuilder<Object[], Object[][], T, SampleFieldKey, Exception>(
                            classMeta,
                            new MappingContextFactoryBuilder<Object[], SampleFieldKey>(KEY_SOURCE_GETTER), 
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
            this(classMeta, MapperConfig.<SampleFieldKey>fieldMapperConfig());
        }

    }

}