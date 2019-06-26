package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.*;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.map.property.OptionalProperty;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TypeHelper;
import org.simpleflatmapper.util.TypeReference;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class AbstractConstantTargetMapperBuilderTest {

    @Test
    public void testDbObject() throws Exception {
        testWriter(new Supplier<DbObject>() {
            @Override
            public DbObject get() {
                return DbObject.newInstance();
            }
        }, DbObject.HEADERS);
    }


    @Test
    public void testConstantValue() throws Exception {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().<DbObject>getClassMeta(DbObject.class);

        Writerbuilder<DbObject> builder = new Writerbuilder<DbObject>(classMeta);
        builder.addColumn("id");
        builder.addColumn("blop", new ConstantValueProperty<String>("blop", String.class));

        DbObject dbObject = DbObject.newInstance();
        List<Object> list = new ArrayList<Object>();

        builder.mapper().mapTo(dbObject, list, MappingContext.EMPTY_CONTEXT);

        assertEquals(list.get(0), dbObject.getId());
        assertEquals("blop", list.get(1));

    }


    @Test
    public void testNonMappedValue() throws Exception {
        ClassMeta<DbObject> classMeta = ReflectionService.newInstance().<DbObject>getClassMeta(DbObject.class);

        Writerbuilder<DbObject> builder = new Writerbuilder<DbObject>(classMeta);
        builder.addColumn("id");
        builder.addColumn("blop", OptionalProperty.INSTANCE, KeyProperty.DEFAULT);

        DbObject dbObject = DbObject.newInstance();
        List<Object> list = new ArrayList<Object>();

        ContextualSourceFieldMapperImpl<DbObject, List<Object>> mapper = builder.mapper();
        mapper.mapTo(dbObject, list, MappingContext.EMPTY_CONTEXT);

        assertEquals(1, list.size());
        assertEquals(list.get(0), dbObject.getId());

    }

    private <T> void testWriter(Supplier<T> supplier, String[] headers) throws Exception {
        T instance1 = supplier.get();
        ClassMeta<T> classMeta = ReflectionService.newInstance().<T>getClassMeta(instance1.getClass());

        Writerbuilder<T> builder = new Writerbuilder<T>(classMeta);

        Object[] row = new Object[headers.length];

        for(int i = 0; i < headers.length; i++) {
            String str = headers[i];
            builder.addColumn(str);
            row[i] = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of(str), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter()).getGetter().get(instance1);

        }

        FieldMapper<T, List<Object>> mapper = builder.mapper();

        List<Object> objects = new ArrayList<Object>();
        mapper.mapTo(instance1, objects, MappingContext.EMPTY_CONTEXT);
        assertArrayEquals(row, objects.toArray());
    }

    private static final SetterFactory<List<Object>, PropertyMapping<?, ?, SampleFieldKey>> SETTER_FACTORY = new SetterFactory<List<Object>, PropertyMapping<?, ?, SampleFieldKey>>() {
        @Override
        public <P> Setter<List<Object>, P> getSetter(final PropertyMapping<?, ?, SampleFieldKey> arg) {
            return new Setter<List<Object>, P>() {
                @Override
                public void set(final List<Object> target, final P v) throws Exception {
                    int i = arg.getColumnKey().getIndex();
                    while (i >= target.size()) {
                        target.add(null);
                    }
                    target.set(i, v);
                }
            };
        }
    };

    public static class Writerbuilder<T> extends AbstractConstantTargetMapperBuilder<List<Object>, T, SampleFieldKey, Writerbuilder<T>> {

        public Writerbuilder(ClassMeta<T> classMeta) {
            this(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().failOnAsm(true));
        }
        public Writerbuilder(ClassMeta<T> classMeta, MapperConfig<SampleFieldKey, Object[]> mapperConfig) {
            super(classMeta, TypeHelper.<List<Object>>toClass(new TypeReference<List<Object>>(){}.getType()), mapperConfig,
                    ConstantTargetFieldMapperFactoryImpl.<List<Object>, SampleFieldKey>newInstance(SETTER_FACTORY, List.class));
        }

        @Override
        protected BiInstantiator<T, MappingContext<? super T>, List<Object>> getInstantiator() {
            return new BiInstantiator<T, MappingContext<? super T>, List<Object>>() {
                @Override
                public List<Object> newInstance(T t, MappingContext<? super T> context) throws Exception {
                    return new ArrayList<Object>();
                }
            };
        }

        @Override
        protected SampleFieldKey newKey(String column, int i, FieldMapperColumnDefinition<SampleFieldKey> columnDefinition) {
            return new SampleFieldKey(column, i);
        }
    }
}