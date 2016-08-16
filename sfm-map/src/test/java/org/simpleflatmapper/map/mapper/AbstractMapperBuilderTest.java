package org.simpleflatmapper.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.SampleFieldKey;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.ArrayEnumarable;
import org.simpleflatmapper.util.Enumarable;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.UnaryFactory;

import java.lang.reflect.Type;

import static org.junit.Assert.*;

public class AbstractMapperBuilderTest {


    @Test
    public void testDbObject() throws Exception {
        testMapper(new Supplier<DbObject>() {
            @Override
            public DbObject get() {
                return DbObject.newInstance();
            }
        }, true);
    }

    @Test
    public void testDbFinalObject() throws Exception {
        testMapper(new Supplier<DbFinalObject>() {
            @Override
            public DbFinalObject get() {
                return DbFinalObject.newInstance();
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

    private <T> void testMapper(Supplier<T> supplier, boolean mapTo) throws Exception {
        T instance1 = supplier.get();
        T instance2 = supplier.get();
        ClassMeta<T> classMeta = ReflectionService.newInstance().<T>getClassMeta(instance1.getClass());

        SampleMapperBuilder<T> builder = new SampleMapperBuilder<T>(classMeta);
        SampleMapperBuilder<T> builderIndexed = new SampleMapperBuilder<T>(classMeta);

        String[] headers = classMeta.generateHeaders();

        Object[] row = new Object[headers.length];

        for(int i = 0; i < headers.length; i++) {
            String str = headers[i];
            builder.addMapping(str);
            builderIndexed.addMapping(str, i);
            row[i] = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of(str)).getGetter().get(instance1);

        }
        Mapper<Object[], T> mapper = builder.mapper();

        assertEquals(instance1, mapper.map(row));
        assertEquals(instance1, builderIndexed.mapper().map(row));

        assertNotEquals(instance1, instance2);

        if (mapTo) {
            mapper.mapTo(row, instance2, null);
            assertEquals(instance1, instance2);
        }
    }


    public static final GetterFactory<Object[], SampleFieldKey> GETTER_FACTORY = new GetterFactory<Object[], SampleFieldKey>() {
        @Override
        public <P> Getter<Object[], P> newGetter(Type target, final SampleFieldKey key, Object... properties) {
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

        public SampleMapperBuilder(ClassMeta<T> classMeta) {
            super(classMeta, new MappingContextFactoryBuilder<Object[], SampleFieldKey>(new KeySourceGetter<SampleFieldKey, Object[]>() {
                        @Override
                        public Object getValue(SampleFieldKey key, Object[] source) throws Exception {
                            return source[key.getIndex()];
                        }
                    }), MapperConfig.<SampleFieldKey>fieldMapperConfig().failOnAsm(true), new MapperSourceImpl<Object[], SampleFieldKey>(Object[].class, GETTER_FACTORY),
                    KEY_FACTORY, 0);
        }

        @Override
        protected Mapper<Object[], T> newJoinJdbcMapper(Mapper<Object[], T> mapper) {
            return new JoinMapperImpl<Object[], Object[][], T, RuntimeException>(mapper,
                    mapperConfig.rowHandlerErrorHandler(),
                    mappingContextFactoryBuilder.newFactory(),
                    new UnaryFactory<Object[][], Enumarable<Object[]>>() {
                @Override
                public Enumarable<Object[]> newInstance(Object[][] objects) {
                    return new ArrayEnumarable<Object[]>(objects);
                }
            });
        }

        @Override
        protected Mapper<Object[], T> newStaticJdbcMapper(Mapper<Object[], T> mapper) {
            return mapper;
        }
    }

}