package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.ContextualSourceMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.SourceMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.test.map.SampleFieldKeyMapperKeyComparator;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.mapper.DynamicSetRowMapper;
import org.simpleflatmapper.map.mapper.MapperKey;
import org.simpleflatmapper.map.mapper.StaticSetRowMapper;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.ArrayEnumerable;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.UnaryFactory;
import org.simpleflatmapper.util.UnaryFactoryWithException;

import java.util.Iterator;
import java.util.List;

//IFJAVA8_START
import java.util.stream.Collectors;
//IFJAVA8_END

import static org.junit.Assert.*;

public class SetRowMapperTest {

    public static final Object[][] ID_NAME_DATA = {{1l, "name1"}, {2l, "name2"}};
    public static final Object[][] ID_NAME_EMAIL_DATA = {{1l, "name1", "email1"}, {2l, "name2", "email2"}};

    public static final UnaryFactory<Object[][], Enumerable<Object[]>> ENUMERABLE_UNARY_FACTORY = new UnaryFactory<Object[][], Enumerable<Object[]>>() {
        @Override
        public Enumerable<Object[]> newInstance(Object[][] objects) {
            return new ArrayEnumerable<Object[]>(objects);
        }
    };
    public static final ContextualSourceFieldMapper<Object[], DbObject> ID_NAME_MAPPER =  new ContextualSourceFieldMapper<Object[], DbObject>() {
        @Override
        public DbObject map(Object[] source) throws MappingException {
            return map(source, null);
        }

        @Override
        public DbObject map(Object[] source, MappingContext<? super Object[]> context) throws MappingException {
            DbObject dbObject = new DbObject();
            try {
                mapTo(source, dbObject, null);
            } catch (Exception e) {
                ErrorHelper.rethrow(e);
            }
            return dbObject;
        }

        @Override
        public void mapTo(Object[] source, DbObject target, MappingContext<? super Object[]> context) throws Exception {
            target.setId((Long) source[0]);
            target.setName((String) source[1]);
        }
    };

    public static final ContextualSourceFieldMapper<Object[], DbObject> ID_NAME_EMIL_MAPPER =  new ContextualSourceFieldMapper<Object[], DbObject>() {
        @Override
        public DbObject map(Object[] source) throws MappingException {
            return map(source, null);
        }

        @Override
        public DbObject map(Object[] source, MappingContext<? super Object[]> context) throws MappingException {
            DbObject dbObject = new DbObject();
            try {
                mapTo(source, dbObject, null);
            } catch (Exception e) {
                ErrorHelper.rethrow(e);
            }
            return dbObject;
        }

        @Override
        public void mapTo(Object[] source, DbObject target, MappingContext<? super Object[]> context) throws Exception {
            target.setId((Long) source[0]);
            target.setName((String) source[1]);
            target.setEmail((String) source[2]);
        }
    };

    public static final MapperKey<SampleFieldKey> ID_NAME_MAPPER_KEY = new MapperKey<SampleFieldKey>(new SampleFieldKey("id", 0), new SampleFieldKey("name", 1));
    public static final MapperKey<SampleFieldKey> ID_NAME_EMAIL_MAPPER_KEY = new MapperKey<SampleFieldKey>(new SampleFieldKey("id", 0), new SampleFieldKey("name", 1), new SampleFieldKey("email", 2));
    @Test
    public void testStatic() throws Exception {

        StaticSetRowMapper<Object[], Object[][], DbObject, RuntimeException> staticSetRowMapper =
                new StaticSetRowMapper<Object[], Object[][], DbObject, RuntimeException>(ID_NAME_MAPPER,
                        RethrowConsumerErrorHandler.INSTANCE, MappingContext.EMPTY_FACTORY, ENUMERABLE_UNARY_FACTORY);


        checkSetRowMapperIdName(staticSetRowMapper);

    }

    private void checkSetRowMapperIdName(SetRowMapper<Object[], Object[][], DbObject, RuntimeException> staticSetRowMapper) throws Exception {
        checkIdNameResult(staticSetRowMapper.forEach(ID_NAME_DATA, new ListCollector<DbObject>()).getList());
        checkIdNameResult(staticSetRowMapper.iterator(ID_NAME_DATA));
        //IFJAVA8_START
        checkIdNameResult(staticSetRowMapper.stream(ID_NAME_DATA).collect(Collectors.<DbObject>toList()));
        //IFJAVA8_END
        checkIdNameRow(1l, staticSetRowMapper.map(ID_NAME_DATA[0]));
        checkIdNameRow(1l, staticSetRowMapper.map(ID_NAME_DATA[0], null));
    }

    private void checkIdNameResult(Iterator<DbObject> it) {

        assertTrue(it.hasNext());
        checkIdNameRow(1l, it.next());
        assertTrue(it.hasNext());
        checkIdNameRow(2l, it.next());
        assertFalse(it.hasNext());

    }

    private void checkIdNameResult(List<DbObject> list) {
        assertEquals(2, list.size());

        checkIdNameRow(1l, list.get(0));
        checkIdNameRow(2l, list.get(1));
    }

    private void checkIdNameRow(long id, DbObject o) {
        assertEquals(id, o.getId());
        assertEquals("name" + id, o.getName());
        assertNull(o.getEmail());
    }


    private void checkSetRowMapperIdNameEmail(SetRowMapper<Object[], Object[][], DbObject, RuntimeException> staticSetRowMapper) throws Exception {
        checkIdNameEmailResult(staticSetRowMapper.forEach(ID_NAME_EMAIL_DATA, new ListCollector<DbObject>()).getList());
        checkIdNameEmailResult(staticSetRowMapper.iterator(ID_NAME_EMAIL_DATA));
        //IFJAVA8_START
        checkIdNameEmailResult(staticSetRowMapper.stream(ID_NAME_EMAIL_DATA).collect(Collectors.<DbObject>toList()));
        //IFJAVA8_END
        checkIdNameEmailRow(1l, staticSetRowMapper.map(ID_NAME_EMAIL_DATA[0]));
        checkIdNameEmailRow(1l, staticSetRowMapper.map(ID_NAME_EMAIL_DATA[0], null));
    }

    private void checkIdNameEmailResult(Iterator<DbObject> it) {

        assertTrue(it.hasNext());
        checkIdNameEmailRow(1l, it.next());
        assertTrue(it.hasNext());
        checkIdNameEmailRow(2l, it.next());
        assertFalse(it.hasNext());

    }

    private void checkIdNameEmailResult(List<DbObject> list) {
        assertEquals(2, list.size());

        checkIdNameEmailRow(1l, list.get(0));
        checkIdNameEmailRow(2l, list.get(1));
    }

    private void checkIdNameEmailRow(long id, DbObject o) {
        assertEquals(id, o.getId());
        assertEquals("name" + id, o.getName());
        assertEquals("email" + id, o.getEmail());
    }

    @Test
    public void testDynamic() throws Exception {
        UnaryFactory<MapperKey<SampleFieldKey>, SetRowMapper<Object[], Object[][], DbObject, RuntimeException>> mapperFactory =
                new UnaryFactory<MapperKey<SampleFieldKey>, SetRowMapper<Object[], Object[][], DbObject, RuntimeException>>() {
                    @Override
                    public SetRowMapper<Object[], Object[][], DbObject, RuntimeException> newInstance(MapperKey<SampleFieldKey> sampleFieldKeyMapperKey) {
                        ContextualSourceMapper<Object[], DbObject> mapper = sampleFieldKeyMapperKey.getColumns().length == 2 ? ID_NAME_MAPPER : ID_NAME_EMIL_MAPPER;
                        return new StaticSetRowMapper<Object[], Object[][], DbObject, RuntimeException>(mapper,
                                RethrowConsumerErrorHandler.INSTANCE, MappingContext.EMPTY_FACTORY, ENUMERABLE_UNARY_FACTORY);
                    }
                };
        UnaryFactoryWithException<Object[], MapperKey<SampleFieldKey>, RuntimeException> mapperKeyFromRow = new UnaryFactoryWithException<Object[], MapperKey<SampleFieldKey>, RuntimeException>() {
            @Override
            public MapperKey<SampleFieldKey> newInstance(Object[] objects) throws RuntimeException {
                return objects.length == 2 ? ID_NAME_MAPPER_KEY : ID_NAME_EMAIL_MAPPER_KEY;
            }
        };
        UnaryFactoryWithException<Object[][], MapperKey<SampleFieldKey>, RuntimeException> mapperKeyFromSet = new UnaryFactoryWithException<Object[][], MapperKey<SampleFieldKey>, RuntimeException>() {
            @Override
            public MapperKey<SampleFieldKey> newInstance(Object[][] objects) throws RuntimeException {
                return objects[0].length == 2 ? ID_NAME_MAPPER_KEY : ID_NAME_EMAIL_MAPPER_KEY;
            }
        };


        DynamicSetRowMapper<Object[], Object[][], DbObject, RuntimeException, SampleFieldKey> dynamicSetRowMapper =
                new DynamicSetRowMapper<Object[], Object[][], DbObject, RuntimeException, SampleFieldKey>(mapperFactory, mapperKeyFromRow, mapperKeyFromSet, SampleFieldKeyMapperKeyComparator.INSTANCE);

        checkSetRowMapperIdName(dynamicSetRowMapper);
        checkSetRowMapperIdNameEmail(dynamicSetRowMapper);
    }

}