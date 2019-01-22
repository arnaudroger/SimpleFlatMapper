package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.ContextualSourceFieldMapper;
import org.simpleflatmapper.map.SourceFieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.context.KeyAndPredicate;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.KeyDefinition;
import org.simpleflatmapper.map.context.impl.BreakDetectorMappingContextFactory;
import org.simpleflatmapper.map.error.RethrowConsumerErrorHandler;
import org.simpleflatmapper.map.mapper.JoinMapper;
import org.simpleflatmapper.test.beans.DbListObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.ListCollector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
//IFJAVA8_START
import java.util.Set;
import java.util.stream.Collectors;
//IFJAVA8_END

import static org.junit.Assert.*;

public class JoinMapperTest {

    private final ContextualSourceFieldMapper<Object[], DbListObject> dbListObjectMapper = new ContextualSourceFieldMapper<Object[], DbListObject>() {

        @Override
        public DbListObject map(Object[] source) throws MappingException {
            return map(source, null);
        }

        @Override
        public DbListObject map(Object[] source, MappingContext<? super Object[]> context) throws MappingException {
            DbListObject dbListObject = new DbListObject();
            try {
                mapTo(source, dbListObject, context);
            } catch (Exception e) {
                ErrorHelper.rethrow(e);
            }
            return dbListObject;
        }

        @Override
        public void mapTo(Object[] source, DbListObject target, MappingContext<? super Object[]> context) throws Exception {
            target.setId((Integer) source[0]);
            List<DbObject> objects = target.getObjects();
            if (objects == null) {
                objects = new ArrayList<DbObject>();
                target.setObjects(objects);
            }
            DbObject o = new DbObject();
            o.setId((Long) source[1]);
            o.setName((String) source[2]);
            objects.add(o);

        }
    };
    private final KeyDefinition<Object[], SampleFieldKey> keyDefinition = 
            new KeyDefinition<Object[], SampleFieldKey>(
                    new KeyAndPredicate[] { new KeyAndPredicate<Object[], SampleFieldKey>(new SampleFieldKey("id", 0), null) },
                    new SampleFieldKeyObjectKeySourceGetter(), 
                    0);

    @SuppressWarnings("unchecked")
    @Test
    public void testJoinWithKey() {
        JoinMapper<Object[], Object[][], DbListObject, RuntimeException> joinMapper =
                new JoinMapper<Object[], Object[][], DbListObject, RuntimeException>(
                        dbListObjectMapper, RethrowConsumerErrorHandler.INSTANCE,
                        new BreakDetectorMappingContextFactory<Object[]>(keyDefinition, new KeyDefinition[] {keyDefinition}, MappingContext.EMPTY_FACTORY),
                        SetRowMapperTest.ENUMERABLE_UNARY_FACTORY
                        );


        checkJoins(joinMapper);
    }
    
    
    
    


    private void checkJoins(JoinMapper<Object[], Object[][], DbListObject, RuntimeException> joinMapper) {
        Object[][] data = new Object[][] {
                {1, 1l, "name1"},
                {1, 2l, "name2"},
                {2, 3l, "name3"}
        };

        checkList(joinMapper.forEach(data, new ListCollector<DbListObject>()).getList());

        //IFJAVA8_START
        checkList(joinMapper.stream(data).collect(Collectors.<DbListObject>toList()));
        //IFJAVA8_END

        List<DbListObject> list = new ArrayList<DbListObject>();

        Iterator<DbListObject> iterator = joinMapper.iterator(data);
        while(iterator.hasNext()) {
            list.add(iterator.next());
        }
        checkList(list);
    }

    private void checkList(List<DbListObject> list) {
        assertEquals(2, list.size());
        assertEquals(1, list.get(0).getId());
        assertEquals(1l, list.get(0).getObjects().get(0).getId());
        assertEquals("name1", list.get(0).getObjects().get(0).getName());
        assertEquals(2l, list.get(0).getObjects().get(1).getId());
        assertEquals("name2", list.get(0).getObjects().get(1).getName());

        assertEquals(2, list.get(1).getId());
        assertEquals(3l, list.get(1).getObjects().get(0).getId());
        assertEquals("name3", list.get(1).getObjects().get(0).getName());
    }

    private static class SampleFieldKeyObjectKeySourceGetter implements KeySourceGetter<SampleFieldKey, Object[]> {
        @Override
        public Object getValue(SampleFieldKey key, Object[] source) throws Exception {
            return source[key.getIndex()];
        }
    }
}