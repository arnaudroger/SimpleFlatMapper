package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.mapper.AbstractConstantTargetMapperBuilder;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.ConstantTargetFieldMapperFactoryImpl;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.property.ConstantValueProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.SetterFactory;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Predicate;
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
        List<Object> list = builder.mapper().map(dbObject);

        assertEquals(list.get(0), dbObject.getId());
        assertEquals("blop", list.get(1));

    }

    private <T> void testWriter(Supplier<T> supplier, String[] headers) throws Exception {
        T instance1 = supplier.get();
        ClassMeta<T> classMeta = ReflectionService.newInstance().<T>getClassMeta(instance1.getClass());

        Writerbuilder<T> builder = new Writerbuilder<T>(classMeta);

        Object[] row = new Object[headers.length];

        for(int i = 0; i < headers.length; i++) {
            String str = headers[i];
            builder.addColumn(str);
            row[i] = classMeta.newPropertyFinder(ConstantPredicate.<PropertyMeta<?, ?>>truePredicate()).findProperty(DefaultPropertyNameMatcher.of(str)).getGetter().get(instance1);

        }

        Mapper<T, List<Object>> mapper = builder.mapper();

        assertArrayEquals(row, mapper.map(instance1).toArray());
    }

    private static final SetterFactory<List<Object>, PropertyMapping<?, ?, SampleFieldKey, ? extends ColumnDefinition<SampleFieldKey, ?>>> SETTER_FACTORY = new SetterFactory<List<Object>, PropertyMapping<?, ?, SampleFieldKey, ? extends ColumnDefinition<SampleFieldKey, ?>>>() {
        @Override
        public <P> Setter<List<Object>, P> getSetter(final PropertyMapping<?, ?, SampleFieldKey, ? extends ColumnDefinition<SampleFieldKey, ?>> arg) {
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
            this(classMeta, MapperConfig.<SampleFieldKey>fieldMapperConfig().failOnAsm(true));
        }
        public Writerbuilder(ClassMeta<T> classMeta, MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> mapperConfig) {
            super(classMeta, TypeHelper.<List<Object>>toClass(new TypeReference<List<Object>>(){}.getType()), mapperConfig,
                    ConstantTargetFieldMapperFactoryImpl.<List<Object>, SampleFieldKey>newInstance(SETTER_FACTORY, List.class));
        }

        @Override
        protected Instantiator<T, List<Object>> getInstantiator() {
            return new Instantiator<T, List<Object>>() {
                @Override
                public List<Object> newInstance(T t) throws Exception {
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