package org.simpleflatmapper.test.map.issue;

import org.junit.Test;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.property.MapTypeProperty;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.test.map.mapper.AbstractConstantTargetMapperBuilderTest;
import org.simpleflatmapper.test.map.mapper.AbstractMapperBuilderTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Issue662Test {




    public static class Data {
        public final Map<String, byte[]> map;

        public Data(Map<String, byte[]> map) {
            this.map = map;
        }
    }


    @Test
    public void testMap() throws Exception {

        ClassMeta<Data> classMeta = ReflectionService.newInstance().getClassMeta(Data.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Data> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Data>(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig());

        EnumerableMapper<Object[][], Data, ?> mapper =
                builder
                        .addMapping("map_key", MapTypeProperty.KEY_VALUE)
                        .addMapping("map_value", MapTypeProperty.KEY_VALUE)
                        .mapper();

        Data data = mapper.iterator(new Object[][]{{"key", new byte[] {1}}}).next();

        assertArrayEquals(new byte[] {1}, data.map.get("key"));
    }


}
