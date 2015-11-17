package org.sfm.jdbc.spring;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.reflect.ReflectionService;
import org.springframework.jdbc.core.namedparam.NamedParameterUtils;
import org.springframework.jdbc.core.namedparam.ParsedSql;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import static org.junit.Assert.assertEquals;

public class SqlParameterSourceTest {

    @Test
    public void testParseSql() {
        String sql = "INSERT INTO table VALUES(:id, :name, :email)";

        SqlParameterSourceBuilder<DbObject> parameterSourceBuilder = new SqlParameterSourceBuilder<DbObject>(DbObject.class);

        ParsedSql parsedSql = NamedParameterUtils.parseSqlStatement(sql);

        StaticSqlParameters<DbObject> sqlParameters = parameterSourceBuilder.build(parsedSql);

        testMapping(sqlParameters);
    }

    protected void testMapping(SqlParameters<DbObject> sqlParameters) {
        DbObject dbObject = getDbObject();

        SqlParameterSource parameterSource = sqlParameters.value(dbObject);

        assertEquals(12345l, parameterSource.getValue("id"));
        assertEquals("name", parameterSource.getValue("name"));
        assertEquals("email", parameterSource.getValue("email"));
    }

    protected DbObject getDbObject() {
        DbObject dbObject = new DbObject();
        dbObject.setId(12345);
        dbObject.setName("name");
        dbObject.setEmail("email");
        return dbObject;
    }


    @Test
    public void testDynamicParams(){
        DynamicSqlParameters<DbObject> parameters = new DynamicSqlParameters<DbObject>(ReflectionService.newInstance().getClassMeta(DbObject.class));

        testMapping(parameters);


    }


}
