package org.sfm.map;

import org.junit.Test;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.ColumnDefinition;
import org.sfm.reflect.Getter;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.samples.SampleFieldKey;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.sql.ResultSet;

import static org.junit.Assert.*;

public class FieldMapperColumnDefinitionTest {


    @Test
    public void testCompose() throws Exception {
        GetterFactory<ResultSet, SampleFieldKey> getterFactory = new GetterFactory<ResultSet, SampleFieldKey>() {
            @Override
            public <P> Getter<ResultSet, P> newGetter(Type target, SampleFieldKey key, ColumnDefinition<?, ?> columnDefinition) {
                return null;
            }

            @Override
            public String toString() {
                return "GetterFactory";
            }

        };
        Getter<ResultSet, Integer> getter = new Getter<ResultSet, Integer>() {
            @Override
            public Integer get(ResultSet target) throws Exception {
                return 3;
            }
            @Override
            public String toString() {
                return "Getter";
            }
        };
        FieldMapper<ResultSet, Object> fieldMapper = new FieldMapper<ResultSet, Object>() {
            @Override
            public void mapTo(ResultSet source, Object target, MappingContext<? super ResultSet> mappingContext) throws Exception {
            }

            @Override
            public String toString() {
                return "FieldMapper";
            }
        };
        final Predicate<PropertyMeta<?, ?>> appliesTo = new Predicate<PropertyMeta<?, ?>>() {
            @Override
            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                return false;
            }
        };
        FieldMapperColumnDefinition<SampleFieldKey> compose =
                FieldMapperColumnDefinition.<SampleFieldKey>identity().addRename("blop").addGetter(getter).addFieldMapper(fieldMapper).addGetterFactory(getterFactory).addKey(appliesTo);

        assertEquals("blop", compose.rename(new SampleFieldKey("bar", -1)).getName());
        assertEquals(fieldMapper, compose.getCustomFieldMapper());
        assertEquals(getterFactory, compose.getCustomGetterFactory());
        assertEquals(new Integer(3), compose.getCustomGetter().get(null));

        assertTrue(compose.hasCustomSource());
        assertTrue(compose.hasCustomFactory());
        assertFalse(compose.ignore());
        assertEquals(Integer.class, compose.getCustomSourceReturnType());

        assertTrue(FieldMapperColumnDefinition.<SampleFieldKey>identity().addIgnore().ignore());

        assertEquals("ColumnDefinition{Rename{'blop'}, Getter{Getter}, FieldMapper{FieldMapper}, GetterFactory{GetterFactory}, Key{}, Ignore{}}", compose.addIgnore().toString());

        assertTrue(compose.isKey());
        assertFalse(FieldMapperColumnDefinition.<SampleFieldKey>identity().isKey());
        assertSame(appliesTo, compose.keyAppliesTo());
    }
}
