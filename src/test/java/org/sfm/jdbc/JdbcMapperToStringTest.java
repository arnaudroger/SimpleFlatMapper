package org.sfm.jdbc;


import org.junit.Test;
import org.sfm.beans.DbObject;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JdbcMapperToStringTest {

    @Test
    public void testStaticJdbcMapperNoAsm() {
        JdbcMapper<DbObject> mapper = JdbcMapperFactoryHelper.noAsm()
                .newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper();

        assertEquals("JdbcMapperImpl{" +
                "instantiator=StaticConstructorInstantiator{constructor=public org.sfm.beans.DbObject(), args=[]}, " +
                "fieldMappers=[LongFieldMapper{getter=LongResultSetGetter{column=1}, setter=LongMethodSetter{method=public void org.sfm.beans.DbObject.setId(long)}}, " +
                "FieldMapperImpl{getter=StringResultSetGetter{column=2}, setter=MethodSetter{method=public void org.sfm.beans.DbObject.setName(java.lang.String)}}]}", mapper.toString());
    }

    @Test
    public void testStaticJdbcMapperAsm() {
        JdbcMapper<DbObject> mapper = JdbcMapperFactoryHelper.asm()
                .newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper();

        String input = mapper.toString();

        assertTrue(Pattern.matches("AsmMapperResultSet2DbObject2_0_[a-z0-9]+\\{" +
                "instantiator=AsmInstantiatorDbObjectResultSet[a-z0-9]+\\{\\}, " +
                "mapper0=LongFieldMapper\\{getter=LongResultSetGetter\\{column=1\\}, setter=AsmSettersetIdDbObjectlong\\{\\}\\}, " +
                "mapper1=FieldMapperImpl\\{getter=StringResultSetGetter\\{column=2\\}, setter=AsmSettersetNameDbObjectString\\{\\}\\}\\}", input));
    }


    @Test
    public void testDynamicJdbcMapperNoAsm() throws SQLException {
        JdbcMapper<DbObject> mapper = JdbcMapperFactoryHelper.noAsm()
                .newMapper(DbObject.class);

        ResultSet rs = mock(ResultSet.class);
        ResultSetMetaData metaData = mock(ResultSetMetaData.class);
        when(rs.getMetaData()).thenReturn(metaData);
        when(metaData.getColumnCount()).thenReturn(1);
        when(metaData.getColumnLabel(1)).thenReturn("id");

        mapper.iterator(rs);


        assertEquals("DynamicJdbcMapper{target=class org.sfm.beans.DbObject, " +
                "MapperCache{[{ColumnsMapperKey{[id]}," +
                "JdbcMapperImpl{instantiator=StaticConstructorInstantiator{constructor=public org.sfm.beans.DbObject(), args=[]}, " +
                "fieldMappers=[LongFieldMapper{getter=LongResultSetGetter{column=1}, setter=LongMethodSetter{method=public void org.sfm.beans.DbObject.setId(long)}}]}}]}}", mapper.toString());
    }
}
