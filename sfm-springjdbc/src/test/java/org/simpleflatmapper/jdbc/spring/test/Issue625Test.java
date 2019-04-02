package org.simpleflatmapper.jdbc.spring.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.spring.JdbcTemplateMapperFactory;
import org.simpleflatmapper.jdbc.spring.SqlParameterSourceFactory;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import static org.junit.Assert.assertEquals;

public class Issue625Test {

    @Test
    public void testSourceFactory() {
        String query = "WITH some_cte AS (\n" +
                "    SELECT :form_email_address\n" +
                "    FROM something s\n" +
                ")\n" +
                "SELECT :form_name\n" +
                "FROM something s";
        SqlParameterSourceFactory<Issue625> sourceFactory = JdbcTemplateMapperFactory.newInstance().newSqlParameterSourceFactory(Issue625.class, query);

        SqlParameterSource sqlParameterSource = sourceFactory.newSqlParameterSource(new Issue625("email", "value"));

        assertEquals("email", sqlParameterSource.getValue("form_email_address"));
        assertEquals("value", sqlParameterSource.getValue("form_name"));
    }

    public static class Issue625 {
        public final String formEmailAddress;
        public final String formName;

        public Issue625(String formEmailAddress, String formName) {
            this.formEmailAddress = formEmailAddress;
            this.formName = formName;
        }
    }
}
