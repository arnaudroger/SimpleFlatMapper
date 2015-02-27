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
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
//IFJAVA8_START
import java.util.stream.Collectors;
import java.util.stream.Stream;
//IFJAVA8_END

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscriminatorJdbcMapperTest {


    @Test
    public void testDiscriminator() throws Exception {
        JdbcMapper<JoinJdbcMapperTest.Person> mapper =
                JdbcMapperFactoryHelper.asm()
                .addKeys("id", "students_id")
                .<JoinJdbcMapperTest.Person>newDiscriminator("person_type")
                .when("student", JoinJdbcMapperTest.StudentGS.class)
                        .addMapping("person_type")
                        .addMapping("id")
                        .addMapping("name")
                .when("professor", JoinJdbcMapperTest.ProfessorGS.class)
                .mapper();



        validateMapper(mapper);


    }

    @Test
    public void testDiscriminatorNoAsm() throws Exception {
        JdbcMapper<JoinJdbcMapperTest.Person> mapper =
                JdbcMapperFactoryHelper.noAsm()
                        .addKeys("id", "students_id")
                        .<JoinJdbcMapperTest.Person>newDiscriminator("person_type")
                        .when("student", JoinJdbcMapperTest.StudentGS.class)
                        .when("professor", JoinJdbcMapperTest.ProfessorGS.class)
                        .mapper();



        validateMapper(mapper);

        assertEquals("DiscriminatorJdbcMapper{" +
                "discriminatorColumn='person_type', " +
                "mappers=[" +
                "Tuple2{" +
                    "element0=DiscriminatorPredicate{value='student'}, " +
                    "element1=DynamicJdbcMapper{target=class org.sfm.jdbc.JoinJdbcMapperTest$StudentGS, " +
                    "MapperCache{[{ColumnsMapperKey{[person_type, id, name, students_id, students_name, students_phones_value]}," +
                    "JoinJdbcMapper{mapper=JdbcMapperImpl{instantiator=StaticConstructorInstantiator{constructor=public org.sfm.jdbc.JoinJdbcMapperTest$StudentGS(), args=[]}, " +
                    "fieldMappers=[IntFieldMapper{getter=IntResultSetGetter{column=2}, setter=IntMethodSetter{method=public void org.sfm.jdbc.JoinJdbcMapperTest$StudentGS.setId(int)}}, " +
                    "FieldMapperImpl{getter=StringResultSetGetter{column=3}, setter=MethodSetter{method=public void org.sfm.jdbc.JoinJdbcMapperTest$StudentGS.setName(java.lang.String)}}]}}}]}}}, " +
                "Tuple2{" +
                    "element0=DiscriminatorPredicate{value='professor'}, " +
                    "element1=DynamicJdbcMapper{target=class org.sfm.jdbc.JoinJdbcMapperTest$ProfessorGS, " +
                    "MapperCache{[{ColumnsMapperKey{[person_type, id, name, students_id, students_name, students_phones_value]}," +
                    "JoinJdbcMapper{mapper=JdbcMapperImpl{instantiator=StaticConstructorInstantiator{constructor=public org.sfm.jdbc.JoinJdbcMapperTest$ProfessorGS(), args=[]}, " +
                    "fieldMappers=[IntFieldMapper{getter=IntResultSetGetter{column=2}, setter=IntMethodSetter{method=public void org.sfm.jdbc.JoinJdbcMapperTest$ProfessorGS.setId(int)}}, " +
                    "FieldMapperImpl{getter=StringResultSetGetter{column=3}, setter=MethodSetter{method=public void org.sfm.jdbc.JoinJdbcMapperTest$ProfessorGS.setName(java.lang.String)}}, " +
                    "MapperFieldMapper{" +
                        "mapper=JdbcMapperImpl{instantiator=StaticConstructorInstantiator{constructor=public java.util.ArrayList(), args=[]}, " +
                        "fieldMappers=[MapperFieldMapper{mapper=JoinJdbcMapper{mapper=JdbcMapperImpl{instantiator=StaticConstructorInstantiator{constructor=public org.sfm.jdbc.JoinJdbcMapperTest$StudentGS(), args=[]}, " +
                            "fieldMappers=[IntFieldMapper{getter=IntResultSetGetter{column=4}, setter=IntMethodSetter{method=public void org.sfm.jdbc.JoinJdbcMapperTest$StudentGS.setId(int)}}, " +
                            "FieldMapperImpl{getter=StringResultSetGetter{column=5}, setter=MethodSetter{method=public void org.sfm.jdbc.JoinJdbcMapperTest$StudentGS.setName(java.lang.String)}}, " +
                            "MapperFieldMapper{mapper=JdbcMapperImpl{instantiator=StaticConstructorInstantiator{constructor=public java.util.ArrayList(), args=[]}, " +
                            "fieldMappers=[FieldMapperImpl{getter=StringResultSetGetter{column=6}, setter=AppendListSetter{}}]}, propertySetter=MethodSetter{method=public void org.sfm.jdbc.JoinJdbcMapperTest$StudentGS.setPhones(java.util.List)}, propertyGetter=MethodGetter{method=public java.util.List org.sfm.jdbc.JoinJdbcMapperTest$StudentGS.getPhones()}}]}}, " +
                    "propertySetter=AppendListSetter{}, propertyGetter=LastIndexListGetter{}}]}, propertySetter=MethodSetter{method=public void org.sfm.jdbc.JoinJdbcMapperTest$ProfessorGS.setStudents(java.util.List)}, propertyGetter=MethodGetter{method=public java.util.List org.sfm.jdbc.JoinJdbcMapperTest$ProfessorGS.getStudents()}}]}}}]}}}]}",
                mapper.toString());

    }

    private <T extends JoinJdbcMapperTest.Person> void validateMapper(JdbcMapper<T> mapper) throws Exception {
        List<T> persons = mapper.forEach(setUpResultSetMock(), new ListHandler<T>()).getList();
        validatePersons(persons);

        //IFJAVA8_START
        validatePersons(mapper.stream(setUpResultSetMock()).collect(Collectors.toList()));

        final Stream<T> stream = mapper.stream(setUpResultSetMock());
        validatePersons(stream.limit(10).collect(Collectors.toList()));
        //IFJAVA8_END

        Iterator<T> iterator = mapper.iterator(setUpResultSetMock());
        persons = new ArrayList<T>();
        while(iterator.hasNext()) {
            persons.add(iterator.next());
        }
        validatePersons(persons);

        ResultSet rs = setUpResultSetMock();

        rs.next();
        MappingContext<ResultSet> mappingContext = mapper.newMappingContext(rs);
        mappingContext.handle(rs);
        final T professor = mapper.map(rs, mappingContext);
        validateProfessorMap((JoinJdbcMapperTest.Professor)professor);
        rs.next();
        mappingContext.handle(rs);
        rs.next();
        mappingContext.handle(rs);
        mapper.mapTo(rs, professor, mappingContext);

        validateProfessorMapTo((JoinJdbcMapperTest.Professor)professor);
    }


    private void validatePersons(List<? extends JoinJdbcMapperTest.Person> persons) {
        assertEquals("we get 4 persons from the resultset", 4, persons.size());
        final JoinJdbcMapperTest.Professor<?> professor0 = (JoinJdbcMapperTest.Professor<?>)persons.get(0);

        assertPersonEquals(1, "professor1", professor0);
        assertEquals("has 2 students", 2, professor0.getStudents().size());
        assertPersonEquals(3, "student3", professor0.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31", "phone32"}, professor0.getStudents().get(0).getPhones().toArray());
        assertPersonEquals(4, "student4", professor0.getStudents().get(1));
        assertArrayEquals(new Object[]{"phone41"}, professor0.getStudents().get(1).getPhones().toArray());


        final JoinJdbcMapperTest.Student student2 = (JoinJdbcMapperTest.Student) persons.get(1);
        assertPersonEquals(2, "student2", student2);

        final JoinJdbcMapperTest.Student student3 = (JoinJdbcMapperTest.Student) persons.get(2);
        assertPersonEquals(3, "student3", student3);

        final JoinJdbcMapperTest.Professor<?> professor4 = (JoinJdbcMapperTest.Professor<?>)persons.get(3);

        assertPersonEquals(4, "professor4", professor4);


    }


    private <T extends JoinJdbcMapperTest.Professor<?>> void validateProfessorMap(T professor0) {
        assertPersonEquals(1, "professor1", professor0);
        assertEquals("has 2 students", 1, professor0.getStudents().size());
        assertPersonEquals(3, "student3", professor0.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31"}, professor0.getStudents().get(0).getPhones().toArray());
    }
    private <T extends JoinJdbcMapperTest.Professor<?>> void validateProfessorMapTo(T professor0) {
        assertPersonEquals(1, "professor1", professor0);
        assertEquals("has 2 students", 2, professor0.getStudents().size());
        assertPersonEquals(3, "student3", professor0.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31"}, professor0.getStudents().get(0).getPhones().toArray());
        assertPersonEquals(4, "student4", professor0.getStudents().get(1));
        assertArrayEquals(new Object[]{"phone41"}, professor0.getStudents().get(1).getPhones().toArray());

    }


    private void assertPersonEquals(int id, String name, JoinJdbcMapperTest.Person person) {
        assertEquals(id, person.getId());
        assertEquals(name, person.getName());
    }
    private ResultSet setUpResultSetMock() throws SQLException {
        ResultSet rs = mock(ResultSet.class);

        ResultSetMetaData metaData = mock(ResultSetMetaData.class);


        final String[] columns = new String[] { "person_type", "id", "name", "students_id", "students_name", "students_phones_value"};

        when(metaData.getColumnCount()).thenReturn(columns.length);
        when(metaData.getColumnLabel(anyInt())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocationOnMock) throws Throwable {
                return columns[-1 + (Integer)invocationOnMock.getArguments()[0]];
            }
        });

        when(rs.getMetaData()).thenReturn(metaData);

        final AtomicInteger ai = new AtomicInteger();

        final Object[][] rows = new Object[][]{
                {"professor", 1, "professor1", 3, "student3", "phone31"},
                {"professor", 1, "professor1", 3, "student3", "phone32"},
                {"professor", 1, "professor1", 4, "student4", "phone41"},
                {"student", 2, "student2", null, null,  null},
                {"student", 3, "student3", null, null,  null},
                {"professor", 4, "professor4", null, null,  null},
        };

        when(rs.next()).then(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocationOnMock) throws Throwable {
                return ai.getAndIncrement() < rows.length;
            }
        });
        final Answer<Object> getValue = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] row = rows[ai.get() - 1];
                final Integer col = -1 + (Integer) invocationOnMock.getArguments()[0];
                return (row[col]);
            }
        };

        final Answer<Object> getColumnValue = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                final Object[] row = rows[ai.get() - 1];
                final String col = (String) invocationOnMock.getArguments()[0];
                return (row[Arrays.asList(columns).indexOf(col)]);
            }
        };

        when(rs.getInt(anyInt())).then(getValue);
        when(rs.getString(anyInt())).then(getValue);
        when(rs.getString(any(String.class))).then(getColumnValue);
        when(rs.getObject(anyInt())).then(getValue);

        return rs;
    }
}
