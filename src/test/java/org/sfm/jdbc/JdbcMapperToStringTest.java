package org.sfm.jdbc;


import org.junit.Test;
import org.sfm.beans.DbObject;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class JdbcMapperToStringTest {

    @Test
    public void testStaticJdbcMapperNoAsm() {
        JdbcMapper<DbObject> mapper = JdbcMapperFactory
                .newInstance()
                .useAsm(false)
                .newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper();

        assertEquals("JdbcMapperImpl{" +
                "instantiator=StaticConstructorInstantiator{constructor=public org.sfm.beans.DbObject(), args=[]}, " +
                "fieldMappers=[LongFieldMapper{getter=LongResultSetGetter{column=1}, setter=LongMethodSetter{method=public void org.sfm.beans.DbObject.setId(long)}}, " +
                "FieldMapperImpl{getter=StringResultSetGetter{column=2}, setter=MethodSetter{method=public void org.sfm.beans.DbObject.setName(java.lang.String)}}]}", mapper.toString());
    }

    @Test
    public void testStaticJdbcMapperAsm() {
        JdbcMapper<DbObject> mapper = JdbcMapperFactory
                .newInstance()
                .newBuilder(DbObject.class).addMapping("id").addMapping("name").mapper();

        System.out.println(mapper.toString());
        assertTrue(Pattern.matches("AsmMapperResultSet2DbObject2_[0-9]+\\{" +
                "instantiator=AsmInstantiatorDbObjectResultSet[0-9]+\\{\\}, " +
                "mapper0=LongFieldMapper\\{getter=LongResultSetGetter\\{column=1\\}, setter=AsmSettersetIdDbObjectlong\\{\\}\\}, " +
                "mapper1=FieldMapperImpl\\{getter=StringResultSetGetter\\{column=2\\}, setter=AsmSettersetNameDbObjectString\\{\\}\\}\\}", mapper.toString()));
    }
}
