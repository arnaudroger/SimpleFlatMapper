package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.test.beans.Professor;
import org.simpleflatmapper.test.jdbc.JoinTest;
import org.simpleflatmapper.test.beans.ProfessorC;
import org.simpleflatmapper.test.beans.ProfessorField;
import org.simpleflatmapper.test.beans.ProfessorGS;
import org.simpleflatmapper.util.ListCollector;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
//IFJAVA8_START
import java.util.stream.Collectors;
//IFJAVA8_END

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JoinJdbcMapperTest {

    private JdbcMapperFactory asmJdbcMapperFactory = JdbcMapperFactoryHelper.asm().addKeys("id", "students_id");
    private JdbcMapperFactory noAsmJdbcMapperFactory = JdbcMapperFactoryHelper.noAsm().addKeys("id", "students_id");
    private JdbcMapperFactory userJdbcMapperFactory = JdbcMapperFactoryHelper.noAsm().addKeys("uuid");

    @Test
    public void testJoinTableFields() throws Exception {
        validateMapper(asmJdbcMapperFactory.newMapper(ProfessorField.class));
    }

    @Test
    public void testJoinTableGSNoAsm() throws Exception {
        validateMapper(noAsmJdbcMapperFactory.newMapper(ProfessorGS.class));
    }


    @Test
    public void testJoinTableGS() throws Exception {
        validateMapper(asmJdbcMapperFactory.newMapper(ProfessorGS.class));
    }


    @Test
    public void testJoinTableGS2Joins() throws Exception {
        validateMapper(asmJdbcMapperFactory.newMapper(ProfessorGS.class));
    }

    @Test
    public void testJoinTableC() throws Exception {
        validateMapper(asmJdbcMapperFactory.newMapper(ProfessorC.class));
    }


    @Test
    public void testJoinTableCNoAsm() throws Exception {
        final JdbcMapper<ProfessorC> mapper = noAsmJdbcMapperFactory.newMapper(ProfessorC.class);
        validateMapper(mapper);

        assertNotNull(mapper.toString());
    }

    @Test
    public void testJoinTableCNoAsmMultiThread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            final JdbcMapper<ProfessorC> mapper = noAsmJdbcMapperFactory.newMapper(ProfessorC.class);

            List<Future<Object>> futures  = new ArrayList<Future<Object>>(100);
            for (int i = 0; i <300; i++) {
                futures.add(executor.submit(new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        validateMapper(mapper);
                        return null;
                    }
                }));
            }

            for(Future<Object> f : futures) {
                f.get();
            }
        } finally {
            executor.shutdown();
        }
    }

        @Test
    public void testJoinTableGSManualMapping() throws Exception {
        JdbcMapper<ProfessorGS> mapper = JdbcMapperFactoryHelper.asm()
                .newBuilder(ProfessorGS.class)
                .addKey("id")
                .addMapping("name")
                .addKey("students_id")
                .addMapping("students_name")
                .addMapping("students_phones_value")
                .mapper();

        validateMapper(mapper);
    }


    private ResultSet setUpResultSetMock() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);


        final String[] columns = new String[] { "id", "name", "students_id", "students_name", "students_phones_value"};

        when(metaData.getColumnCount()).thenReturn(columns.length);
        when(metaData.getColumnLabel(anyInt())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return columns[-1 + (Integer)invocationOnMock.getArguments()[0]];
            }
        });

        when(rs.getMetaData()).thenReturn(metaData);

        final AtomicInteger ai = new AtomicInteger();



        when(rs.next()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ai.getAndIncrement() < JoinTest.ROWS.length;
            }
        });
        final Answer<Object> getValue = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] row = JoinTest.ROWS[ai.get() - 1];
                final Integer col = -1 + (Integer) invocationOnMock.getArguments()[0];
                return (row[col]);
            }
        };

        when(rs.getInt(anyInt())).then(getValue);
        when(rs.getString(anyInt())).then(getValue);
        when(rs.getObject(anyInt())).then(getValue);

        return rs;
    }


    private <T extends Professor<?>> void validateMapper(JdbcMapper<T> mapper) throws Exception {
        List<T> professors = mapper.forEach(setUpResultSetMock(), new ListCollector<T>()).getList();
        JoinTest.validateProfessors(professors);

        //IFJAVA8_START
        JoinTest.validateProfessors(mapper.stream(setUpResultSetMock()).collect(Collectors.<T>toList()));

        JoinTest.validateProfessors(mapper.stream(setUpResultSetMock()).limit(3).collect(Collectors.<T>toList()));
        //IFJAVA8_END

        Iterator<T> iterator = mapper.iterator(setUpResultSetMock());
        professors = new ArrayList<T>();
        while(iterator.hasNext()) {
            professors.add(iterator.next());
        }
        JoinTest.validateProfessors(professors);

        final ResultSet rs = setUpResultSetMock();


        rs.next();
        MappingContext<? super ResultSet> mappingContext = mapper.newMappingContext(rs);
        mappingContext.broke(rs);
        final T professor = mapper.map(rs, mappingContext);
        JoinTest.validateProfessorMap(professor);
        rs.next();
        mappingContext.broke(rs);
        rs.next();
        mappingContext.broke(rs);
        
//        mapper.mapTo(rs, professor, mappingContext);
//
//        JoinTest.validateProfessorMapTo(professor);



    }

    @Test
    public void testUser() throws SQLException {
        JdbcMapper<User> mapper = userJdbcMapperFactory.newBuilder(User.class).addKey("id").addMapping("name").addMapping("roles_name").mapper();

        ResultSet rs = mock(ResultSet.class);

        when(rs.next()).thenReturn(true, true, false);

        when(rs.getInt(1)).thenReturn(1, 1);
        when(rs.getString(2)).thenReturn("n1", "n1");
        when(rs.getString(3)).thenReturn("r1", "r2");

        Iterator<User> iterator = mapper.iterator(rs);

        User u = iterator.next();

        assertEquals(1, u.id);
        assertEquals("n1", u.name);
        assertEquals(2, u.roles.size());

    }


    public static class User {
        public int id;
        public String name;
        public Set<Role> roles;
    }

    public static class Role {
        public String name;
    }

}
