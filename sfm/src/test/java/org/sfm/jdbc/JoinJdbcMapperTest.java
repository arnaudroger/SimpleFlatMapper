package org.sfm.jdbc;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.sfm.map.MappingContext;
import org.sfm.utils.ListHandler;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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


    @Test
    public void testJoinTableFields() throws Exception {
        validateMapper(asmJdbcMapperFactory.newMapper(JoinTest.ProfessorField.class));
    }

    @Test
    public void testJoinTableGSNoAsm() throws Exception {
        validateMapper(noAsmJdbcMapperFactory.newMapper(JoinTest.ProfessorGS.class));
    }


    @Test
    public void testJoinTableGS() throws Exception {
        validateMapper(asmJdbcMapperFactory.newMapper(JoinTest.ProfessorGS.class));
    }


    @Test
    public void testJoinTableGS2Joins() throws Exception {
        validateMapper(asmJdbcMapperFactory.newMapper(JoinTest.ProfessorGS.class));
    }

    @Test
    public void testJoinTableC() throws Exception {
        validateMapper(asmJdbcMapperFactory.newMapper(JoinTest.ProfessorC.class));
    }


    @Test
    public void testJoinTableCNoAsm() throws Exception {
        final JdbcMapper<JoinTest.ProfessorC> mapper = noAsmJdbcMapperFactory.newMapper(JoinTest.ProfessorC.class);
        validateMapper(mapper);

        assertNotNull(mapper.toString());
    }

    @Test
    public void testJoinTableCNoAsmMultiThread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            final JdbcMapper<JoinTest.ProfessorC> mapper = noAsmJdbcMapperFactory.newMapper(JoinTest.ProfessorC.class);

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
        JdbcMapper<JoinTest.ProfessorGS> mapper = JdbcMapperFactoryHelper.asm()
                .newBuilder(JoinTest.ProfessorGS.class)
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


    private <T extends JoinTest.Professor<?>> void validateMapper(JdbcMapper<T> mapper) throws Exception {
        List<T> professors = mapper.forEach(setUpResultSetMock(), new ListHandler<T>()).getList();
        JoinTest.validateProfessors(professors);

        //IFJAVA8_START
        JoinTest.validateProfessors(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        JoinTest.validateProfessors(mapper.stream(setUpResultSetMock()).limit(3).collect(Collectors.toList()));
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
        mappingContext.handle(rs);
        final T professor = mapper.map(rs, mappingContext);
        JoinTest.validateProfessorMap(professor);
        rs.next();
        mappingContext.handle(rs);
        rs.next();
        mappingContext.handle(rs);
        mapper.mapTo(rs, professor, mappingContext);

        JoinTest.validateProfessorMapTo(professor);



    }


}
