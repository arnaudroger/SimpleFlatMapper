package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.map.mapper.AbstractMapperFactory;
import org.simpleflatmapper.map.property.IgnoreProperty;
import org.simpleflatmapper.map.property.KeyProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.util.Consumer;


import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

public class AbstractMapperBuilderDiscriminatorTest {
    
    
    @Test
    public void test571() throws Exception {
        SetRowMapper<Object[], Object[][], A571, Exception> mapper = AbstractMapperBuilderTest.SampleMapperFactory.newInstance()
                .discriminator(A571.class,
                        new Getter<Object[], String>() {
                            @Override
                            public String get(Object[] target) throws Exception {
                                return (String) target[2];
                            }
                        },
                        new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common>>() {
                            @Override
                            public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common> builder) {
                                builder
                                        .when("1", A571_1.class)
                                        .when("2", A571_2.class)
                                ;
                            }
                        })
                .discriminator(B571.class,
                        new Getter<Object[], String>() {
                            @Override
                            public String get(Object[] target) throws Exception {
                                return (String) target[3];
                            }
                        },
                        new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common>>() {
                            @Override
                            public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common> builder) {
                                builder
                                        .when("1", B571_1.class)
                                        .when("2", B571_2.class)
                                ;
                            }
                        })
                .newBuilder(ReflectionService.disableAsm().getClassMeta(A571.class))
                .addMapping("id")
                .addMapping("b_name")
                .mapper();

        A571_1 next = (A571_1) mapper.iterator(new Object[][]{
                {"id", "name", "1", "1"}
        }).next();

        B571_1 b = (B571_1) next.b;
        
        assertEquals("name", b.name);

    }
    
    public static abstract class A571 {
        public final String id;
        public final B571 b;

        protected A571(String id, B571 b) {
            this.id = id;
            this.b = b;
        }
    }
    
    public static class A571_1 extends A571 {
        public A571_1(String id, B571 b) {
            super(id, b);
        }
    }

    public static class A571_2 extends A571 {
        public A571_2(String id, B571 b) {
            super(id, b);
        }
    }
    
    public static abstract class B571 {
        
    }
    
    public static class B571_1 extends B571 {
        public final String name;

        public B571_1(String name) {
            this.name = name;
        }
    }
    
    public static class B571_2 extends B571 {
        public final String name;

        public B571_2(String name) {
            this.name = name;
        }
    }
    @Test
    public void test561KeyAndWithKeyOnB() throws Exception {
        //fail();

        //
        SetRowMapper<Object[], Object[][], RootC561, Exception> mapper = AbstractMapperBuilderTest.SampleMapperFactory.newInstance()
                .discriminator(C561.class,
                        new Getter<Object[], String>() {
                            @Override
                            public String get(Object[] target) throws Exception {
                                return (String) target[3];
                            }
                        },
                        new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common>>() {
                            @Override
                            public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common> builder) {
                                builder
                                        .when("a", C561_A.class)
                                        .when("b", C561_B.class)
                                ;
                            }
                        })
                .newBuilder(ReflectionService.disableAsm().getClassMeta(RootC561.class))
                .addMapping("id", KeyProperty.DEFAULT)
                .addMapping("c561s_namesId", KeyProperty.DEFAULT)
                .addMapping("c561s_bid", KeyProperty.DEFAULT)
                .mapper();


        Iterator<RootC561> iterator = mapper.iterator(new Object[][]{
//                {"1", "a1", "", "a"},
//                {"1", "a2", "", "a"},
                {"1", "b1", "2", "b"},
                {"1", "b2", "2", "b"},
                {"1", "b3", "2", "b"},

        });

        RootC561 r = iterator.next();

        assertEquals("1", r.id);
        assertEquals(1, r.c561s.size());
//        C561_A a1 = (C561_A)r.c561s.get(0);
//        assertEquals("a1", a1.namesId);
//        C561_A a2 = (C561_A)r.c561s.get(1);
//        assertEquals("a2", a2.namesId);


        C561_B b = (C561_B)r.c561s.get(0);

        assertEquals(3, b.names.size());
        assertEquals("b1", b.names.get(0).id);
        assertEquals("b2", b.names.get(1).id);
        assertEquals("b3", b.names.get(2).id);

    }
    @Test
    public void test561KeyAndNoKeyOnB() throws Exception {
        //fail();

        //
        SetRowMapper<Object[], Object[][], RootC561, Exception> mapper = AbstractMapperBuilderTest.SampleMapperFactory.newInstance()
                .discriminator(C561.class,
                        new Getter<Object[], String>() {
                            @Override
                            public String get(Object[] target) throws Exception {
                                return (String) target[3];
                            }
                        },
                        new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common>>() {
                            @Override
                            public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common> builder) {
                                builder
                                        .when("a", C561_A.class)
                                        .when("b", C561_B.class)
                                ;
                            }
                        })
                .newBuilder(ReflectionService.disableAsm().getClassMeta(RootC561.class))
                .addMapping("id", KeyProperty.DEFAULT)
                .addMapping("c561s_namesId", KeyProperty.DEFAULT)
                .mapper();


        Iterator<RootC561> iterator = mapper.iterator(new Object[][]{
                {"1", "a1", "", "a"},
                {"1", "a2", "", "a"},
                {"1", "b1", "2", "b"},
                {"1", "b2", "2", "b"},
                {"1", "b3", "2", "b"},

        });

        RootC561 r = iterator.next();
        
        assertEquals("1", r.id);
        assertEquals(5, r.c561s.size());
        C561_A a1 = (C561_A)r.c561s.get(0);
        assertEquals("a1", a1.namesId);
        C561_A a2 = (C561_A)r.c561s.get(1);
        assertEquals("a2", a2.namesId);


        C561_B b = (C561_B)r.c561s.get(2);
        assertEquals(1, b.names.size());
        assertEquals("b1", b.names.get(0).id);

        b = (C561_B)r.c561s.get(3);
        assertEquals(1, b.names.size());
        assertEquals("b2", b.names.get(0).id);


        b = (C561_B)r.c561s.get(4);
        assertEquals(1, b.names.size());
        assertEquals("b3", b.names.get(0).id);
        
    }
    
    public static class RootC561 {
        public final String id;
        public final List<C561> c561s;

        public RootC561(String id, List<C561> c561s) {
            this.id = id;
            this.c561s = c561s;
        }
    }
    public static class C561 {
        
    } 
    
    public static class C561_A extends C561 {
        public final String namesId;

        public C561_A(String namesId) {
            this.namesId = namesId;
        }
    }
    public static class C561_B extends C561 {
        public final List<C561_B_ID> names;
        public final String bid;

        public C561_B(List<C561_B_ID> names, String bid) {
            this.names = names;
            this.bid = bid;
        }
    }
    public static class C561_B_ID {
        public final String id;

        public C561_B_ID(String id) {
            this.id = id;
        }
    }
    
    @Test
    public void testDiscriminator() {

        AbstractMapperBuilderTest.SampleMapperBuilder<Common> builder = newCommonBuilder();

        builder.addMapping("id");
        builder.addMapping("valueStr");
        builder.addMapping("valueInt");

        SetRowMapper<Object[], Object[][], Common, Exception> mapper = builder.mapper();

        StringValue stringValue = (StringValue) mapper.map(new Object[] {1l, "strValue", 2, "str"});
        assertEquals(1, stringValue.id);
        assertEquals("strValue", stringValue.valueStr);

        IntegerValue integerValue = (IntegerValue) mapper.map(new Object[] {2l, "str", 3, "int"});
        assertEquals(2, integerValue.id);
        assertEquals(3, integerValue.valueInt);

    }

    @Test
    public void testDiscriminatorFailOnNonMatchColumn() {
        try {
            AbstractMapperBuilderTest.SampleMapperBuilder<Common> builder = newCommonBuilder();

            builder.addMapping("id");
            builder.addMapping("valueNotThere");

            fail();
        } catch (MapperBuildingException e){
            // expected
        }
    }
    
    @Test
    public void testDiscriminatorOnJoin() throws Exception {
        AbstractMapperBuilderTest.SampleMapperBuilder<ListOfCommon> builder = newListOfCommonBuilder();

        builder.addMapping("commons_id", KeyProperty.DEFAULT);
        builder.addMapping("commons_valueStr");
        builder.addMapping("commons_valueInt");
        builder.addMapping("type", new IgnoreProperty());
        builder.addMapping("id", KeyProperty.DEFAULT);


        SetRowMapper<Object[], Object[][], ListOfCommon, Exception> mapper = builder.mapper();


        Iterator<ListOfCommon> iterator = mapper.iterator(
                new Object[][]{
                        {1l, "strValue", 2, "str", 1l},
                        {2l, "strValue", 3, "int", 1l},
                        
                        {3l, "strValue2", 2, "str", 2l},
                        {4l, "strValue", 4, "int", 2l},
                        {5l, "strValue", 5, "int", 2l},
                });
        
        ListOfCommon loc = iterator.next();
        
        
        assertEquals(1l, loc.id);
        assertEquals(2, loc.commons.size());
        assertEquals(1l, loc.commons.get(0).id);
        assertEquals(2l, loc.commons.get(1).id);
        assertEquals("strValue", ((StringValue)loc.commons.get(0)).valueStr);
        assertEquals(3, ((IntegerValue)loc.commons.get(1)).valueInt);
        
        loc = iterator.next();

        assertEquals(2l, loc.id);
        assertEquals(3, loc.commons.size());
        assertEquals(3l, loc.commons.get(0).id);
        assertEquals(4l, loc.commons.get(1).id);
        assertEquals(5l, loc.commons.get(2).id);
        assertEquals("strValue2", ((StringValue)loc.commons.get(0)).valueStr);
        assertEquals(4, ((IntegerValue)loc.commons.get(1)).valueInt);
        assertEquals(5, ((IntegerValue)loc.commons.get(2)).valueInt);
        
        assertFalse(iterator.hasNext());
    }

    private AbstractMapperBuilderTest.SampleMapperBuilder<Common> newCommonBuilder() {
        return newBuilder(reflectionService.getClassMeta(Common.class));
    }
    private AbstractMapperBuilderTest.SampleMapperBuilder<ListOfCommon> newListOfCommonBuilder() {
        return newBuilder(reflectionService.getClassMeta(ListOfCommon.class));
    }

    ReflectionService reflectionService = ReflectionService.newInstance();

    private <T> AbstractMapperBuilderTest.SampleMapperBuilder<T> newBuilder(ClassMeta<T> classMeta) {
        return AbstractMapperBuilderTest.SampleMapperFactory.newInstance()
                .discriminator(Common.class,
                        new Getter<Object[], String>() {
                            @Override
                            public String get(Object[] target) throws Exception {
                                return (String) target[3];
                            }
                        },
                        new Consumer<AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common>>() {
                    @Override
                    public void accept(AbstractMapperFactory.DiscriminatorConditionBuilder<Object[], SampleFieldKey, String, Common> builder) {
                        builder
                            .when("str", StringValue.class).
                            when("int", IntegerValue.class);
                    }
                })
                .newBuilder(classMeta);
    }

    public static class ListOfCommon {
        public final long id;
        public final List<Common> commons;

        public ListOfCommon(long id, List<Common> commons) {
            this.id = id;
            this.commons = commons;
        }
    }

    public static abstract class Common {
        public final long id;

       Common(long id) {
            this.id = id;
        }
    }
    
    public static class StringValue extends Common {
        public final String valueStr;

        public StringValue(long id, String valueStr) {
            super(id);
            this.valueStr = valueStr;
        }
    }

    public static class IntegerValue extends Common {
        public final int valueInt;

        public IntegerValue(long id, int valueInt) {
            super(id);
            this.valueInt = valueInt;
        }
    }
}
