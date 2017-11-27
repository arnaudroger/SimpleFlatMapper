package org.simpleflatmapper.test.map.mapper;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
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
import org.simpleflatmapper.util.ArrayEnumarable;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.ConstantUnaryFactory;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;
import java.util.Date;
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
    public void testConversionDateToJodaTime() {
        ClassMeta<List<DateTime>> classMeta =
                ReflectionService.disableAsm().<List<DateTime>>getClassMeta(new TypeReference<List<DateTime>>() {}.getType());

        Mapper<Object[] , List<DateTime>> mapper =
                new SampleMapperBuilder<List<DateTime>>(classMeta)
                    .addMapping(new SampleFieldKey("0", 0, new Class[0], Date.class)).mapper();

        Object[] objects = new Object[] { new Date() };
        List<DateTime> map = mapper.map(objects);
        assertEquals(objects[0], map.get(0).toDate());
    }

    @Test
    public void testConversionCharacterToJodaTime() {
        ClassMeta<List<DateTime>> classMeta =
                ReflectionService.disableAsm().<List<DateTime>>getClassMeta(new TypeReference<List<DateTime>>() {}.getType());

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMdd HH:mm:ss.SSSS Z");

        Mapper<Object[] , List<DateTime>> mapper =
                new SampleMapperBuilder<List<DateTime>>(classMeta)
                        .addMapping(new SampleFieldKey("0", 0, new Class[0], String.class), dateTimeFormatter).mapper();

        DateTime now = DateTime.now();
        Object[] objects = new Object[] { dateTimeFormatter.print(now) };
        List<DateTime> map = mapper.map(objects);
        assertEquals(now, map.get(0));
    }

    //IFJAVA8_START
    @Test
    public void testOptionalDbObject() {
        ClassMeta<Optional<DbObject>> classMeta =
                ReflectionService.disableAsm().<Optional<DbObject>>getClassMeta(new TypeReference<Optional<DbObject>>() {}.getType());
        Mapper<Object[], Optional<DbObject>> mapper =
                new SampleMapperBuilder<Optional<DbObject>>(classMeta)
                        .addMapping("id")
                        .addMapping("name")
                        .mapper();

        Optional<DbObject> map = mapper.map(new Object[]{1l, "name1"});

        assertEquals(1l, map.get().getId());
        assertEquals("name1", map.get().getName());
    }
    //IFJAVA8_END

    @Test
    public void testArrayDbObject() {
        ClassMeta<DbObject[]> classMeta =
                ReflectionService.disableAsm().<DbObject[]>getClassMeta(DbObject[].class);
        Mapper<Object[], DbObject[]> mapper =
                new SampleMapperBuilder<DbObject[]>(classMeta)
                        .addMapping("1_id")
                        .addMapping("1_name")
                        .addMapping("2_id")
                        .addMapping("2_name")
                        .mapper();

        DbObject[] map = mapper.map(new Object[]{1l, "name1", 2l, "name2"});

        assertEquals(3, map.length);
        assertEquals(1l, map[1].getId());
        assertEquals("name1", map[1].getName());
        assertEquals(2l, map[2].getId());
        assertEquals("name2", map[2].getName());
    }
    @Test
    public void testListDbObject() {
        ClassMeta<List<DbObject>> classMeta =
                ReflectionService.disableAsm().<List<DbObject>>getClassMeta(new TypeReference<List<DbObject>>() {}.getType());
        Mapper<Object[], List<DbObject>> mapper =
                new SampleMapperBuilder<List<DbObject>>(classMeta)
                        .addMapping("1_id")
                        .addMapping("1_name")
                        .addMapping("2_id")
                        .addMapping("2_name")
                        .mapper();

        List<DbObject> map = mapper.map(new Object[]{1l, "name1", 2l, "name2"});

        assertEquals(3, map.size());
        assertEquals(1l, map.get(1).getId());
        assertEquals("name1", map.get(1).getName());
        assertEquals(2l, map.get(2).getId());
        assertEquals("name2", map.get(2).getName());
    }

    @Test
    public void testSetDbObject() {
        ClassMeta<Set<DbObject>> classMeta =
                ReflectionService.disableAsm().<Set<DbObject>>getClassMeta(new TypeReference<Set<DbObject>>() {}.getType());
        Mapper<Object[], Set<DbObject>> mapper =
                new SampleMapperBuilder<Set<DbObject>>(classMeta)
                        .addMapping("1_id")
                        .addMapping("1_name")
                        .addMapping("2_id")
                        .addMapping("2_name")
                        .mapper();

        Set<DbObject> map = mapper.map(new Object[]{1l, "name1", 2l, "name2"});

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
    public void testMapDbObject() {
        ClassMeta<Map<Long, DbObject>> classMeta =
                ReflectionService.disableAsm().<Map<Long, DbObject>>getClassMeta(new TypeReference<Map<Long, DbObject>>() {}.getType());
        Mapper<Object[], Map<Long, DbObject>> mapper =
                new SampleMapperBuilder<Map<Long, DbObject>>(classMeta)
                        .addMapping("1_id")
                        .addMapping("1_name")
                        .addMapping("2_id")
                        .addMapping("2_name")
                        .mapper();

        Map<Long, DbObject> map = mapper.map(new Object[]{1l, "name1", 2l, "name2"});

        assertEquals(2, map.size());
        assertEquals(1l, map.get(1l).getId());
        assertEquals("name1", map.get(1l).getName());
        assertEquals(2l, map.get(2l).getId());
        assertEquals("name2", map.get(2l).getName());
    }
    @Test
    public void testFinalObjectWithOneConstructor() {
        ClassMeta<FinalObjectWith1ParamConstruction> classMeta = ReflectionService.newInstance().<FinalObjectWith1ParamConstruction>getClassMeta(FinalObjectWith1ParamConstruction.class);

        Mapper<Object[], FinalObjectWith1ParamConstruction> mapper =
                new SampleMapperBuilder<FinalObjectWith1ParamConstruction>(classMeta)
                        .addMapping("id")
                        .addMapping("o1p")
                        .addMapping("o2p")
                        .mapper();

        FinalObjectWith1ParamConstruction map = mapper.map(new Object[]{1l, "v1", "v2"});

        assertEquals(1l, map.id);
        assertEquals("v1", map.o1p.getValue());
        assertEquals("v2", map.o2p.getO1p().getValue());
    }


    @Test
    public void testObjectWithOneConstructor() {
        ClassMeta<ObjectWith1ParamConstruction> classMeta = ReflectionService.newInstance().<ObjectWith1ParamConstruction>getClassMeta(ObjectWith1ParamConstruction.class);

        Mapper<Object[], ObjectWith1ParamConstruction> mapper =
                new SampleMapperBuilder<ObjectWith1ParamConstruction>(classMeta)
                        .addMapping("id")
                        .addMapping("o1p")
                        .addMapping("o2p")
                        .mapper();

        ObjectWith1ParamConstruction map = mapper.map(new Object[]{1l, "v1", "v2"});

        assertEquals(1l, map.id);
        assertEquals("v1", map.o1p.getValue());
        assertEquals("v2", map.o2p.getO1p().getValue());
    }

    @Test
    public void testObjectWithOneConstructorWithLoop() {

        try {
            ClassMeta<ObjectWith1ParamConstructionWithLoop> classMeta = ReflectionService.newInstance().<ObjectWith1ParamConstructionWithLoop>getClassMeta(ObjectWith1ParamConstructionWithLoop.class);
            Mapper<Object[], ObjectWith1ParamConstructionWithLoop> mapper =
                    new SampleMapperBuilder<ObjectWith1ParamConstructionWithLoop>(classMeta)
                            .addMapping("id")
                            .addMapping("o1p")
                            .mapper();
            fail();
        } catch (MapperBuildingException e) {}



        try {
            ClassMeta<FinalObjectWith1ParamConstructionWithLoop> classMeta = ReflectionService.newInstance().<FinalObjectWith1ParamConstructionWithLoop>getClassMeta(FinalObjectWith1ParamConstructionWithLoop.class);
            Mapper<Object[], FinalObjectWith1ParamConstructionWithLoop> mapper =
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

        Mapper<Object[], DbFinal1DeepObject> mapper =
                new SampleMapperBuilder<DbFinal1DeepObject>(classMeta)
                        .addMapping("id")
                        .addMapping("value")
                        .addMapping("dbObject_id")
                        .addMapping("dbObject_name")
                        .mapper();

        DbFinal1DeepObject map = mapper.map(new Object[]{1, "vvv", 2l, "wwww"});

        assertEquals(1, map.getId());
        assertEquals("vvv", map.getValue());
        assertEquals(2l, map.getDbObject().getId());
        assertEquals("wwww", map.getDbObject().getName());

    }

    @Test
    public void testJoinDbListObject() throws Exception {
        ClassMeta<DbListObject> classMeta = ReflectionService.newInstance(false).<DbListObject>getClassMeta(DbListObject.class);

        SampleMapperBuilder<DbListObject> builder = new SampleMapperBuilder<DbListObject>(classMeta);
        JoinMapper<Object[], Object[][], DbListObject, RuntimeException> mapper =
                (JoinMapper<Object[], Object[][], DbListObject, RuntimeException>) builder
                        .addKey("id")
                        .addKey("objects_id")
                        .addMapping("objects_name")
                        .mapper();


        checkDbListJoinMapper(mapper);

    }

    @Test
    public void testJoinDbListObjectMissingKey() throws Exception {
        ClassMeta<DbListObject> classMeta = ReflectionService.newInstance().<DbListObject>getClassMeta(DbListObject.class);

        JoinMapper<Object[], Object[][], DbListObject, RuntimeException> mapper =
                (JoinMapper<Object[], Object[][], DbListObject, RuntimeException>) new SampleMapperBuilder<DbListObject>(classMeta)
                        .addKey("id")
                        .addMapping("objects_id")
                        .addMapping("objects_name")
                        .mapper();


        checkDbListJoinMapper(mapper);

    }

    private void checkDbListJoinMapper(JoinMapper<Object[], Object[][], DbListObject, RuntimeException> mapper) {
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

        MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> mapperConfig =
                MapperConfig.<SampleFieldKey>fieldMapperConfig().columnDefinitions(definitionProvider);
        Mapper<Object[], DbObject> mapper =
                new SampleMapperBuilder<DbObject>(classMeta, mapperConfig)
                        .addMapping("id")
                        .mapper();
        Object[] data = new Object[] {3l};

        DbObject dbObject = mapper.map(data);

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
    public void testCustomization() throws Exception {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);

        Mapper<Object[], DbObject> mapper =
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

        DbObject dbObject = mapper.map(new Object[] { 1l });
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
        dbObject = mapper.map(new Object[] { 1l});
        assertEquals(1, dbObject.getId());
        assertEquals("getterEmail", dbObject.getEmail());
        assertEquals("getterName", dbObject.getName());



    }
    private <T> void testDbObjectxxxMapper(Supplier<T> supplier, boolean mapTo) throws Exception {
        testDbObjectxxxMapper(supplier, mapTo, false, DbObject.HEADERS);
        testDbObjectxxxMapper(supplier, mapTo, true, DbObject.HEADERS);
    }
    private <T> void testDbObjectxxxMapper(Supplier<T> supplier, boolean mapTo, boolean useAsm, String[] headers) throws Exception {
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
            row[i] = classMeta.newPropertyFinder(ConstantPredicate.<PropertyMeta<?, ?>>truePredicate()).findProperty(DefaultPropertyNameMatcher.of(str), new Object[0]).getGetter().get(instance1);

        }
        Mapper<Object[], T> mapper = builder.mapper();

        assertEquals(instance1, mapper.map(row));
        Assert.assertEquals(instance1, builderIndexed.mapper().map(row));

        assertNotEquals(instance1, instance2);

        if (mapTo) {
            mapper.mapTo(row, instance2, null);
            assertEquals(instance1, instance2);
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

    public static class SampleMapperBuilder<T> extends AbstractMapperBuilder<Object[], T, SampleFieldKey, Mapper<Object[], T>, SampleMapperBuilder<T>> {

        public SampleMapperBuilder(ClassMeta<T> classMeta, MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> mapperConfig) {
            super(classMeta, new MappingContextFactoryBuilder<Object[], SampleFieldKey>(new KeySourceGetter<SampleFieldKey, Object[]>() {
                        @Override
                        public Object getValue(SampleFieldKey key, Object[] source) throws Exception {
                            return source[key.getIndex()];
                        }
                    }), mapperConfig.failOnAsm(true), new MapperSourceImpl<Object[], SampleFieldKey>(Object[].class, GETTER_FACTORY),
                    KEY_FACTORY, 0);
        }
        public SampleMapperBuilder(ClassMeta<T> classMeta) {
            this(classMeta, MapperConfig.<SampleFieldKey>fieldMapperConfig());
        }

        @Override
        protected Mapper<Object[], T> newJoinMapper(Mapper<Object[], T> mapper) {
            return new JoinMapper<Object[], Object[][], T, RuntimeException>(mapper,
                    mapperConfig.consumerErrorHandler(),
                    mappingContextFactoryBuilder.newFactory(),
                    new UnaryFactory<Object[][], Enumarable<Object[]>>() {
                @Override
                public Enumarable<Object[]> newInstance(Object[][] objects) {
                    return new ArrayEnumarable<Object[]>(objects);
                }
            });
        }

        @Override
        protected Mapper<Object[], T> newStaticMapper(Mapper<Object[], T> mapper) {
            return new StaticSetRowMapper<Object[], Object[][], T, Exception>(mapper,
                    mapperConfig.consumerErrorHandler(),
                    mappingContextFactoryBuilder.newFactory(),
                    new UnaryFactory<Object[][], Enumarable<Object[]>>() {
                        @Override
                        public Enumarable<Object[]> newInstance(Object[][] objects) {
                            return new ArrayEnumarable<Object[]>(objects);
                        }
                    });
        }
    }

}