package  org.simpleflatmapper.core.map;

import org.junit.Test;
import  org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import  org.simpleflatmapper.core.map.mapper.ColumnDefinition;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.core.samples.SampleFieldKey;
import org.simpleflatmapper.core.utils.Predicate;

import java.io.InputStream;
import java.lang.reflect.Type;

import static org.junit.Assert.*;

public class FieldMapperColumnDefinitionTest {


    @Test
    public void testCompose() throws Exception {
        GetterFactory<InputStream, SampleFieldKey> getterFactory = new GetterFactory<InputStream, SampleFieldKey>() {
            @Override
            public <P> Getter<InputStream, P> newGetter(Type target, SampleFieldKey key, ColumnDefinition<?, ?> columnDefinition) {
                return null;
            }

            @Override
            public String toString() {
                return "GetterFactory";
            }

        };
        Getter<InputStream, Integer> getter = new Getter<InputStream, Integer>() {
            @Override
            public Integer get(InputStream target) throws Exception {
                return 3;
            }
            @Override
            public String toString() {
                return "Getter";
            }
        };
        FieldMapper<InputStream, Object> fieldMapper = new FieldMapper<InputStream, Object>() {
            @Override
            public void mapTo(InputStream source, Object target, MappingContext<? super InputStream> mappingContext) throws Exception {
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
