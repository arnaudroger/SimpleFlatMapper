package org.sfm.map.impl;

import org.junit.Test;
import org.sfm.jdbc.JdbcColumnKey;
import org.sfm.jdbc.impl.getter.ResultSetGetterFactory;
import org.sfm.map.FieldMapper;
import org.sfm.map.impl.fieldmapper.FieldMapperFactory;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyMeta;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class FieldMapperMapperBuilderTest {

    @Test
    public void testAnonymousParameterWithDifferentType() throws Exception {

        ClassMeta<MyObjectWithInner> classMeta = ReflectionService.disableAsm().getClassMeta(MyObjectWithInner.class);
        FieldMapperFactory<ResultSet, JdbcColumnKey> factory = new FieldMapperFactory<>(new ResultSetGetterFactory());

        PropertyMeta<MyObjectWithInner, MultiConstructorObject> propertyMeta = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("prop"));
        FieldMapperColumnDefinition<JdbcColumnKey, ResultSet> identity = FieldMapperColumnDefinition.identity();
        PropertyMapping<MyObjectWithInner, MultiConstructorObject, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>> propertyMapping =
                new PropertyMapping<MyObjectWithInner, MultiConstructorObject, JdbcColumnKey, FieldMapperColumnDefinition<JdbcColumnKey, ResultSet>>(
                        propertyMeta, new JdbcColumnKey("prop", 1, Types.TIMESTAMP), identity);
        FieldMapper<ResultSet, MyObjectWithInner> fieldMapper = factory.newFieldMapper(propertyMapping, new RethrowMapperBuilderErrorHandler());

        MyObjectWithInner o = new MyObjectWithInner();
        ResultSet rs = mock(ResultSet.class);
        when(rs.getTimestamp(1)).thenReturn(new Timestamp(new Date().getTime()));
        fieldMapper.mapTo(rs, o, null);
        assertNotNull(o.prop.date);
    }


    public static class MyObjectWithInner {
        public MultiConstructorObject prop;
    }
    public static class MultiConstructorObject {
        private String str;
        private Date date;

        public MultiConstructorObject(String bob) {
            this.str = bob;
        }
        public MultiConstructorObject(Date bap) {
            this.date = bap;
        }
    }

}