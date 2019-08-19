package org.simpleflatmapper.jdbc.spring.test;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.SqlParameterSourceFactory;
import org.simpleflatmapper.map.property.ConverterProperty;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import static org.junit.Assert.assertEquals;

public class Issue675Test {

    @Test
    public void testSourceFactory() {
        String query = "SELECT * FROM table WHERE type = :myEnum and id = :id";
        SqlParameterSourceFactory<Issue675> sourceFactory = JdbcTemplateMapperFactory.newInstance()
                .addColumnProperty("myEnum", new ConverterProperty<MyEnum, Long>(new ContextualConverter<MyEnum, Long>() {
                    @Override
                    public Long convert(MyEnum in, Context context) throws Exception {
                        switch (in) {
                            case ONE: return 1l;
                            case TWO: return 2l;
                            default: throw new IllegalArgumentException();
                        }
                    }
                }, MyEnum.class))
                .newSqlParameterSourceFactory(Issue675.class, query);



        SqlParameterSource sqlParameterSource = sourceFactory.newSqlParameterSource(new Issue675(1, MyEnum.TWO));

        assertEquals(1l, sqlParameterSource.getValue("id"));
        assertEquals(2l, sqlParameterSource.getValue("myEnum"));
    }

    public static class Issue675 {
        public final long id;
        public final MyEnum myEnum;

        public Issue675(long id, MyEnum myEnum) {
            this.id = id;
            this.myEnum = myEnum;
        }
    }

    public static enum MyEnum {
        ONE, TWO
    }
}
