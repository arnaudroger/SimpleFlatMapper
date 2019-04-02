package org.simpleflatmapper.jdbc.test.named;

import org.junit.Test;
import org.simpleflatmapper.jdbc.named.NamedSqlQuery;

import static org.junit.Assert.assertEquals;

public class NamedSqlQueryTest {

    @Test
    public void testInsertQuestionMark() {
        String sql = "INSERT INTO table(col1, col2) values(?, ?)";

        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse(sql);

        assertEquals(2, namedSqlQuery.getParametersSize());
        assertEquals("col1", namedSqlQuery.getParameter(0).getName());
        assertEquals("col2", namedSqlQuery.getParameter(1).getName());

        assertEquals("INSERT INTO table(col1, col2) values(?, ?)",
                namedSqlQuery.toSqlQuery());

    }
    @Test
    public void testParseComplexSql() {
        String sql = "SELECT a, b, c FROM table " +
                "where " +
                "a in (?, :futon) " +
                "and b=lower(?) " +
                "and c = :c " +
                "and upper(lower(d)) = ? " +
                "and upper(lower(e)) = upper(lower(?)) " +
                "and lower(f) = upper(lower(?))";

        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse(sql);

        assertEquals(7, namedSqlQuery.getParametersSize());
        assertEquals("a", namedSqlQuery.getParameter(0).getName());
        assertEquals("futon", namedSqlQuery.getParameter(1).getName());
        assertEquals("b", namedSqlQuery.getParameter(2).getName());
        assertEquals("c", namedSqlQuery.getParameter(3).getName());
        assertEquals("d", namedSqlQuery.getParameter(4).getName());
        assertEquals("e", namedSqlQuery.getParameter(5).getName());
        assertEquals("f", namedSqlQuery.getParameter(6).getName());

        assertEquals("SELECT a, b, c FROM table " +
                        "where a in (?, ?) " +
                        "and b=lower(?) " +
                        "and c = ? " +
                        "and upper(lower(d)) = ? " +
                        "and upper(lower(e)) = upper(lower(?)) " +
                        "and lower(f) = upper(lower(?))",
                namedSqlQuery.toSqlQuery());

    }

    @Test
    public void testParseSqlWithQuestionMarks() {
        String sql = "SELECT * FROM table where field = ? and field2 = ?";

        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse(sql);

        assertEquals(2, namedSqlQuery.getParametersSize());
        assertEquals("field", namedSqlQuery.getParameter(0).getName());
        assertEquals("field2", namedSqlQuery.getParameter(1).getName());

        assertEquals("SELECT * FROM table where field = ? and field2 = ?",
                namedSqlQuery.toSqlQuery());

    }

    @Test
    public void testParseSqlWithNamedParam() {
        String sql = "SELECT * FROM table where field = :field and field = :field2";

        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse(sql);

        assertEquals(2, namedSqlQuery.getParametersSize());
        assertEquals("field", namedSqlQuery.getParameter(0).getName());
        assertEquals("field2", namedSqlQuery.getParameter(1).getName());

        assertEquals("SELECT * FROM table where field = ? and field = ?",
                namedSqlQuery.toSqlQuery());

    }


    @Test
    public void testParseSqlWithOnlyNamedParams() {
        String sql = ":field2";

        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse(sql);

        assertEquals(1, namedSqlQuery.getParametersSize());
        assertEquals("field2", namedSqlQuery.getParameter(0).getName());

        assertEquals("?",
                namedSqlQuery.toSqlQuery());

    }


    @Test
    public void testParseSqlWithNoParams() {
        String sql = "select 1 from dual";

        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse(sql);

        assertEquals(0, namedSqlQuery.getParametersSize());

        assertEquals("select 1 from dual",
                namedSqlQuery.toSqlQuery());

    }

    @Test
    public void testIssue625() {
        String sql = "WITH some_cte AS (\n" +
                "    SELECT :form_email_address\n" +
                "    FROM something s\n" +
                ")\n" +
                "SELECT :form_name\n" +
                "FROM something s";

        NamedSqlQuery namedSqlQuery = NamedSqlQuery.parse(sql);

        assertEquals(2, namedSqlQuery.getParametersSize());

        assertEquals("form_email_address", namedSqlQuery.getParameter(0).getName());
        assertEquals("form_name", namedSqlQuery.getParameter(1).getName());


    }
}