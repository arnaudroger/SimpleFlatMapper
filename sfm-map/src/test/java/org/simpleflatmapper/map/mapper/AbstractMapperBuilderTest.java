package org.simpleflatmapper.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SampleFieldKey;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.getter.GetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
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
        });
    }

    @Test
    public void testDbFinalObject() throws Exception {
        testMapper(new Supplier<DbObject>() {
            @Override
            public DbObject get() {
                return DbObject.newInstance();
            }
        });
    }

    private <T> void testMapper(Supplier<T> supplier) throws Exception {
        T instance1 = supplier.get();
        T instance2 = supplier.get();
        ClassMeta<T> classMeta = ReflectionService.newInstance().<T>getClassMeta(instance1.getClass());

        SampleMapperBuilder<T> builder = new SampleMapperBuilder<T>(classMeta);

        String[] headers = classMeta.generateHeaders();

        Object[] row = new Object[headers.length];

        for(int i = 0; i < headers.length; i++) {
            String str = headers[i];
            builder.addMapping(str);
            row[i] = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of(str)).getGetter().get(instance1);

        }
        Mapper<Object[], T> mapper = builder.mapper();

        assertEquals(instance1, mapper.map(row));

        assertNotEquals(instance1, instance2);
        mapper.mapTo(row, instance2, null);
        assertEquals(instance1, instance2);
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
                    }), MapperConfig.<SampleFieldKey>fieldMapperConfig(), new MapperSourceImpl<Object[], SampleFieldKey>(Object[].class, GETTER_FACTORY),
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