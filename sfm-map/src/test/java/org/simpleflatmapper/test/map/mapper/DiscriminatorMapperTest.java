package org.simpleflatmapper.test.map.mapper;



import static org.junit.Assert.*;

public class DiscriminatorMapperTest {
//
//    DiscriminatorMapper<Object[], Object[][], Person, RuntimeException> mapper;
//
//
//    @Before
//    public void setUp() {
//        SourceMapper<Object[], Person> studentMapper = new SourceMapper<Object[], Person>() {
//            @Override
//            public Person map(Object[] source) throws MappingException {
//                return map(source, null);
//            }
//
//            @Override
//            public Person map(Object[] source, MappingContext<? super Object[]> context) throws MappingException {
//                StudentGS studentGS = new StudentGS();
//                try {
//                    mapTo(source, studentGS, context);
//                } catch (Exception e) {
//                    ErrorHelper.rethrow(e);
//                }
//                return studentGS;
//            }
//
//            @Override
//            public void mapTo(Object[] source, Person target, MappingContext<? super Object[]> context) throws Exception {
//                StudentGS studentGS = (StudentGS) target;
//                studentGS.setName((String) source[1]);
//                studentGS.setId((Integer) source[2]);
//            }
//        };
//        SourceMapper<Object[], Person> professorMapper = new SourceMapper<Object[], Person>() {
//            @Override
//            public Person map(Object[] source) throws MappingException {
//                return map(source, null);
//            }
//
//            @Override
//            public Person map(Object[] source, MappingContext<? super Object[]> context) throws MappingException {
//                ProfessorGS professorGS = new ProfessorGS();
//                try {
//                    mapTo(source, professorGS, context);
//                } catch (Exception e) {
//                    ErrorHelper.rethrow(e);
//                }
//                return professorGS;
//            }
//
//            @Override
//            public void mapTo(Object[] source, Person target, MappingContext<? super Object[]> context) throws Exception {
//                ProfessorGS professorGS = (ProfessorGS) target;
//                professorGS.setName((String) source[1]);
//                professorGS.setId((Integer) source[2]);
//            }
//        };
//
//        List<DiscriminatorMapper.PredicatedMapper<Object[], Object[][], Person, RuntimeException>> mappers =
//                new ArrayList<DiscriminatorMapper.PredicatedMapper<Object[], Object[][], Person, RuntimeException>>();
//
//
//
//        mappers.add(new DiscriminatorMapper.PredicatedMapper<Object[], Object[][], Person, RuntimeException>(
//                new Predicate<Object[]>() {
//                    @Override
//                    public boolean test(Object[] objects) {
//                        return "student".equals(objects[0]);
//                    }
//                },
//                studentMapper,
//                new MappingContextFactoryFromRows<Object[], Object[][], RuntimeException>() {
//                    @Override
//                    public MappingContext<? super Object[]> newMappingContext(Object[][] objects) throws RuntimeException {
//                        return MappingContext.EMPTY_CONTEXT;
//                    }
//                }
//        ));
//
//        mappers.add(new DiscriminatorMapper.PredicatedMapper<Object[], Object[][], Person, RuntimeException>(
//                new Predicate<Object[]>() {
//                    @Override
//                    public boolean test(Object[] objects) {
//                        return "professor".equals(objects[0]);
//                    }
//                },
//                professorMapper,
//                new MappingContextFactoryFromRows<Object[], Object[][], RuntimeException>() {
//                    @Override
//                    public MappingContext<? super Object[]> newMappingContext(Object[][] objects) throws RuntimeException {
//                        return MappingContext.EMPTY_CONTEXT;
//                    }
//                }
//        ));
//
//        mapper =
//                new DiscriminatorMapper<Object[], Object[][], Person, RuntimeException>(mappers,
//                        new UnaryFactory<Object[][], Enumarable<Object[]>>() {
//                            @Override
//                            public Enumarable<Object[]> newInstance(Object[][] objects) {
//                                return new ArrayEnumarable<Object[]>(objects);
//                            }
//                        }, new UncheckedConverter<Object[], String>() {
//                    @Override
//                    public String convert(Object[] in) {
//                        return Arrays.toString(in);
//                    }
//                }, RethrowConsumerErrorHandler.INSTANCE);
//
//    }
//    @Test
//    public void testMapStudentProfessor() {
//
//
//        List<Person> list = mapper.forEach(
//                new Object[][]{
//                        {"student", "sname", 1},
//                        {"professor", "pname", 2}
//                },
//                new ListCollector<Person>()).getList();
//
//        assertEquals(2, list.size());
//
//        StudentGS studentGS = (StudentGS) list.get(0);
//        ProfessorGS professorGS = (ProfessorGS) list.get(1);
//
//        assertEquals("sname", studentGS.getName());
//        assertEquals("pname", professorGS.getName());
//
//
//
//
//    }
//
//    @Test
//    public void testMapCat() {
//        try {
//            mapper.forEach(
//                    new Object[][]{
//                            {"cat", "sname", 1},
//                            {"professor", "pname", 2}
//                    },
//                    new ListCollector<Person>()).getList();
//            fail();
//        } catch (MappingException e) {
//            //expected
//        }
//    }

}