package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperBuilder;
import org.simpleflatmapper.map.property.ConverterProperty;
import org.simpleflatmapper.test.beans.DbEnumObject;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.tuple.Tuple2;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JdbcMapperEnumTest {

	@Test
	public void testIndexedEnumUndefined() throws Exception {
		JdbcMapperBuilder<DbEnumObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbEnumObject.class);
		builder.addMapping("val", 1);
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getObject(1)).thenReturn(Integer.valueOf(2));
		
		assertEquals(DbObject.Type.type3, mapper.map(rs).getVal());

		when(rs.getObject(1)).thenReturn("type2");
		
		assertEquals(DbObject.Type.type2, mapper.map(rs).getVal());
	}
	
	
	
	@Test
	public void testIndexedEnumString() throws Exception {
		JdbcMapperBuilder<DbEnumObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbEnumObject.class);
		builder.addMapping("val",1, Types.VARCHAR);
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getString(1)).thenReturn("type2");
		
		assertEquals(DbObject.Type.type2, mapper.map(rs).getVal());
		
	}


    @Test
    public void testIndexedEnumFactoryMethod() throws Exception {
        JdbcMapperBuilder<DbEnumObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbEnumObject.class);
        builder.addMapping("val",1, Types.VARCHAR, new Object[] {
                ConverterProperty.of(new ContextualConverter<String, DbObject.Type>() {
            @Override
            public DbObject.Type convert(String s, Context context) throws Exception {
                return DbObject.Type.shortForm(s);
            }
        })});

        JdbcMapper<DbEnumObject> mapper = builder.mapper();

        ResultSet rs = mock(ResultSet.class);

        when(rs.getString(1)).thenReturn("t2");

        assertEquals(DbObject.Type.type2, mapper.map(rs).getVal());

    }
	
	@Test
	public void testIndexedEnumOrdinal() throws Exception {
		JdbcMapperBuilder<DbEnumObject> builder = JdbcMapperFactoryHelper.asm().newBuilder(DbEnumObject.class);
		builder.addMapping("val",1, Types.INTEGER);
		
		JdbcMapper<DbEnumObject> mapper = builder.mapper();
		
		ResultSet rs = mock(ResultSet.class);
		
		when(rs.getInt(1)).thenReturn(2);
		
		assertEquals(DbObject.Type.type3, mapper.map(rs).getVal());
		
	}

    public enum TypeRoot {
        type1   ("1"), type2   ("2"), type3   ("3"), type4   ("4");

        private String value;
        TypeRoot(String ... values) { this.value = values[0]; }
        public String getValue() { return value;  }
    }


    @Test
    public void testEnumRoot() throws SQLException {
        JdbcMapperBuilder<TypeRoot> builder = JdbcMapperFactoryHelper.asm().newBuilder(TypeRoot.class);
        builder.addMapping("c1");

        JdbcMapper<TypeRoot> mapper = builder.mapper();

        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject(1)).thenReturn(0);
        when(rs.next()).thenReturn(true, false);

        assertEquals(TypeRoot.type1, mapper.iterator(rs).next());
    }


    @Test
    public void testEnumTuple() throws SQLException {
        JdbcMapperBuilder<Tuple2<TypeRoot, TypeRoot>> builder = JdbcMapperFactoryHelper.asm().newBuilder(new TypeReference<Tuple2<TypeRoot, TypeRoot>>() {});
        builder.addMapping("0");
        builder.addMapping("1");

        JdbcMapper<Tuple2<TypeRoot, TypeRoot>> mapper = builder.mapper();

        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject(1)).thenReturn(0);
        when(rs.getObject(2)).thenReturn(1);
        when(rs.next()).thenReturn(true, false);

        Tuple2<TypeRoot, TypeRoot> tuple = mapper.iterator(rs).next();
        assertEquals(TypeRoot.type1, tuple.first());
        assertEquals(TypeRoot.type2, tuple.second());
    }
}
