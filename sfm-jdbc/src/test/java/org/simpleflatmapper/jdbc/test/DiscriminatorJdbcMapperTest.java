package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.reflect.IndexedGetter;
import org.simpleflatmapper.test.beans.Person;
import org.simpleflatmapper.test.beans.Professor;
import org.simpleflatmapper.test.beans.Student;
import org.simpleflatmapper.test.beans.StudentGS;
import org.simpleflatmapper.test.beans.ProfessorGS;
import org.simpleflatmapper.util.CheckedBiFunction;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.Predicate;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
//IFJAVA8_START
import java.util.stream.Collectors;
import java.util.stream.Stream;
//IFJAVA8_END

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DiscriminatorJdbcMapperTest {

    @Test
    public void testNewDiscriminatorFieldAccessorDSL() throws Exception {
        JdbcMapper<Person> mapper =
                JdbcMapperFactoryHelper.asm()
                        .addKeys("id", "students_id")
                        .discriminator(Person.class)
                        .onColumn("person_type", String.class)
                        .with(new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Person>>() {
                            @Override
                            public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Person> builder) {
                                builder
                                        .when("student", StudentGS.class)
                                        .when("professor", ProfessorGS.class);
                            }
                        }
                )
                        .newMapper(Person.class);
        validateMapper(mapper);
    }
    @Test
    public void testNewDiscriminatorFieldAccessorDSLNamedGetter() throws Exception {
        JdbcMapper<Person> mapper =
                JdbcMapperFactoryHelper.asm()
                        .addKeys("id", "students_id")
                        .discriminator(Person.class).onColumnWithNamedGetter("person_type", new CheckedBiFunction<ResultSet, String, String>() {
                                    @Override
                                    public String apply(ResultSet rs, String columnName) throws Exception {
                                        return rs.getString(columnName);
                                    }
                            }).with(new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Person>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Person> builder) {
                                        builder
                                                .when("student", StudentGS.class)
                                                .when("professor", ProfessorGS.class);
                                    }
                                }
                        )
                        .newMapper(Person.class);
        validateMapper(mapper);
    }
    @Test
    public void testNewDiscriminatorFieldAccessorDSLIntGetter() throws Exception {
        JdbcMapper<Person> mapper =
                JdbcMapperFactoryHelper.asm()
                        .addKeys("id", "students_id")
                        .discriminator(Person.class).onColumnWithIndexedGetter("person_type", new IndexedGetter<ResultSet, String>() {
                    @Override
                    public String get(ResultSet rs, int columnIndex) throws Exception {
                        return rs.getString(columnIndex);
                    }
                }).with(new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Person>>() {
                            @Override
                            public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Person> builder) {
                                builder
                                        .when("student", StudentGS.class)
                                        .when("professor", ProfessorGS.class);
                            }
                        }
                )
                        .newMapper(Person.class);
        validateMapper(mapper);
    }

    @Test
    public void testNewDiscriminatorFieldAccessor() throws Exception {
        JdbcMapper<Person> mapper =
                JdbcMapperFactoryHelper.asm()
                        .addKeys("id", "students_id")
                        .discriminator(Person.class, "person_type", new CheckedBiFunction<ResultSet, String, String>() {
                                    @Override
                                    public String apply(ResultSet rs, String columnName) throws Exception {
                                        return rs.getString(columnName);
                                    }
                                }, new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Object>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Object> builder) {
                                        builder
                                                .when("student", StudentGS.class)
                                                .when("professor", ProfessorGS.class);
                                    }
                                }
                        )
                        .newMapper(Person.class);
        validateMapper(mapper);
    }

    @Test
    public void testNewDiscriminator() throws Exception {
        JdbcMapper<Person> mapper =
                JdbcMapperFactoryHelper.asm()
                        .addKeys("id", "students_id")
                        .discriminator(Person.class, "person_type", String.class, new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Object>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Object> builder) {
                                        builder
                                                .when("student", StudentGS.class)
                                                .when("professor", ProfessorGS.class);
                                    }
                                }
                        )
                        .newMapper(Person.class);
        validateMapper(mapper);
    }

    @Test
    public void testNewDiscriminatorNoAsm() throws Exception {
        JdbcMapper<Person> mapper =
                JdbcMapperFactoryHelper.noAsm()
                        .addKeys("id", "students_id")
                        .discriminator(Person.class, "person_type", String.class, new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Object>>() {
                                    @Override
                                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Object> builder) {
                                        builder
                                                .when("student", StudentGS.class)
                                                .when("professor", ProfessorGS.class);
                                    }
                                }
                        )
                        .newMapper(Person.class);


        validateMapper(mapper);

    }

    @Test
    public void testNewJoinTableCNoAsmMultiThread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);


        try {
            final JdbcMapper<Person> mapper =
                    JdbcMapperFactoryHelper.noAsm()
                            .addKeys("id", "students_id")
                            .discriminator(Person.class, "person_type", String.class, new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Object>>() {
                                        @Override
                                        public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<ResultSet, JdbcColumnKey, String, Object> builder) {
                                            builder
                                                    .when("student", StudentGS.class)
                                                    .when("professor", ProfessorGS.class);
                                        }
                                    }
                            )
                            .newMapper(Person.class);

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
    public void testDiscriminator() throws Exception {
        JdbcMapper<Person> mapper =
                JdbcMapperFactoryHelper.asm()
                .addKeys("id", "students_id")
                .<Person>newDiscriminator("person_type")
                .when("student", StudentGS.class)
                        .addMapping("person_type")
                        .addMapping("id")
                        .addMapping("name", 3, FieldMapperColumnDefinition.<JdbcColumnKey>identity())
                .when(new Predicate<String>() {
                    @Override
                    public boolean test(String s) {
                        return "professor".equals(s);
                    }
                }, ProfessorGS.class)
                .mapper();
        validateMapper(mapper);
    }

    @Test
    public void testDiscriminatorNoAsm() throws Exception {
        JdbcMapper<Person> mapper =
                JdbcMapperFactoryHelper.noAsm()
                        .addKeys("id", "students_id")
                        .<Person>newDiscriminator("person_type")
                        .when("student", StudentGS.class)
                        .when("professor", ProfessorGS.class)
                        .mapper();



        validateMapper(mapper);

    }

    @Test
    public void testJoinTableCNoAsmMultiThread() throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(4);


        try {
            final JdbcMapper<Person> mapper =
                    JdbcMapperFactoryHelper.noAsm()
                            .addKeys("id", "students_id")
                            .<Person>newDiscriminator("person_type")
                            .when("student", StudentGS.class)
                            .when("professor", ProfessorGS.class)
                            .mapper();

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

    private <T extends Person> void validateMapper(JdbcMapper<T> mapper) throws Exception {
        List<T> persons = mapper.forEach(setUpResultSetMock(), new ListCollector<T>()).getList();
        validatePersons(persons);

        //IFJAVA8_START
        validatePersons(mapper.stream(setUpResultSetMock()).collect(Collectors.<T>toList()));

        final Stream<T> stream = mapper.stream(setUpResultSetMock());
        validatePersons(stream.limit(10).collect(Collectors.<T>toList()));
        //IFJAVA8_END

        Iterator<T> iterator = mapper.iterator(setUpResultSetMock());
        persons = new ArrayList<T>();
        while(iterator.hasNext()) {
            persons.add(iterator.next());
        }
        validatePersons(persons);

        ResultSet rs = setUpResultSetMock();

        rs.next();
        MappingContext<? super ResultSet> mappingContext = mapper.newMappingContext(rs);
        mappingContext.broke(rs);
        final T professor = mapper.map(rs, mappingContext);
        validateProfessorMap((Professor)professor);
        rs.next();
        mappingContext.broke(rs);
        rs.next();
        mappingContext.broke(rs);
    }


    private void validatePersons(List<? extends Person> persons) {
        assertEquals("we get 4 persons from the resultset", 4, persons.size());
        final Professor<?> professor0 = (Professor<?>)persons.get(0);

        assertPersonEquals(1, "professor1", professor0);
        assertEquals("has 2 students", 2, professor0.getStudents().size());
        assertPersonEquals(3, "student3", professor0.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31", "phone32"}, professor0.getStudents().get(0).getPhones().toArray());
        assertPersonEquals(4, "student4", professor0.getStudents().get(1));
        assertArrayEquals(new Object[]{"phone41"}, professor0.getStudents().get(1).getPhones().toArray());


        final Student student2 = (Student) persons.get(1);
        assertPersonEquals(2, "student2", student2);

        final Student student3 = (Student) persons.get(2);
        assertPersonEquals(3, "student3", student3);

        final Professor<?> professor4 = (Professor<?>)persons.get(3);

        assertPersonEquals(4, "professor4", professor4);


    }


    private <T extends Professor<?>> void validateProfessorMap(T professor0) {
        assertPersonEquals(1, "professor1", professor0);
        assertEquals("has 2 students", 1, professor0.getStudents().size());
        assertPersonEquals(3, "student3", professor0.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31"}, professor0.getStudents().get(0).getPhones().toArray());
    }
    private <T extends Professor<?>> void validateProfessorMapTo(T professor0) {
        assertPersonEquals(1, "professor1", professor0);
        assertEquals("has 2 students", 2, professor0.getStudents().size());
        assertPersonEquals(3, "student3", professor0.getStudents().get(0));
        assertArrayEquals(new Object[]{"phone31"}, professor0.getStudents().get(0).getPhones().toArray());
        assertPersonEquals(4, "student4", professor0.getStudents().get(1));
        assertArrayEquals(new Object[]{"phone41"}, professor0.getStudents().get(1).getPhones().toArray());

    }


    private void assertPersonEquals(int id, String name, Person person) {
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
        when(rs.getObject(anyString(), any(Class.class))).then(getColumnValue);

        return rs;
    }
}
