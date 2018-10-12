package org.simpleflatmapper.test.map.issue;

import org.junit.Test;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.GetterProperty;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.test.map.mapper.AbstractConstantTargetMapperBuilderTest;
import org.simpleflatmapper.test.map.mapper.AbstractMapperBuilderTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class Issue365Test {


    public static final Setter<Data, String> SETTER = new Setter<Data, String>() {
        @Override
        public void set(Data target, String value) throws Exception {
            int indexOfDot = value.indexOf('.');
            target.algorithm = value.substring(0, indexOfDot);
            target.type = value.substring(indexOfDot + 1, value.length());
        }
    };

    public static final Getter<Data, String> GETTER = new Getter<Data, String>() {

        @Override
        public String get(Data target) throws Exception {
            return target.algorithm + "." + target.type;
        }
    };

    public static class Data {
        public String algorithm;
        public String type;
        public double score;

        @Override
        public String toString() {
            return "Data{" +
                    "algorithm='" + algorithm + '\'' +
                    ", type='" + type + '\'' +
                    ", score=" + score +
                    '}';
        }
    }

    public static class DataHolder {
        public Data data;
    }

    @Test
    public void testMapOnCustomSetter() throws Exception {

        ClassMeta<Data> classMeta = ReflectionService.newInstance().getClassMeta(Data.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Data> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Data>(classMeta, mapperConfig());

        EnumerableMapper<Object[][], Data, ?> mapper =
                builder
                        .addMapping("score")
                        .addMapping("benchmark")
                        .mapper();

        Data data = mapper.iterator(new Object[][]{{3.455, "algo.type"}}).next();

        assertEquals(3.455, data.score, 0.0);
        assertEquals("algo", data.algorithm);
        assertEquals("type", data.type);
    }


    @Test
    public void testMapOnCustomGetter() throws Exception {

        ClassMeta<Data> classMeta = ReflectionService.newInstance().getClassMeta(Data.class);

        AbstractConstantTargetMapperBuilderTest.Writerbuilder<Data> builder =
                new AbstractConstantTargetMapperBuilderTest.Writerbuilder<Data>(classMeta, mapperConfig());

        FieldMapper<Data, List<Object>> mapper =
                builder
                    .addColumn("score")
                    .addColumn("benchmark")
                    .mapper();

        Data data = new Data();
        data.score = 3.455;
        data.algorithm = "algo";
        data.type = "type";

        List<Object> list = new ArrayList<Object>();
        
        mapper.mapTo(data, list, MappingContext.EMPTY_CONTEXT);

        assertEquals(Arrays.asList(new Object[] {3.455, "algo.type"}), list);
    }


    @Test
    public void testMapOnCustomSetterSubProperty() throws Exception {

        ClassMeta<DataHolder> classMeta = ReflectionService.newInstance().getClassMeta(DataHolder.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<DataHolder> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<DataHolder>(classMeta, mapperConfig());

        EnumerableMapper<Object[][], DataHolder, ?> mapper =
                builder
                        .addMapping("data_score")
                        .addMapping("data_benchmark")
                        .mapper();

        DataHolder data = mapper.iterator(new Object[][]{{3.455, "algo.type"}}).next();

        assertEquals(3.455, data.data.score, 0.0);
        assertEquals("algo", data.data.algorithm);
        assertEquals("type", data.data.type);
    }

    private MapperConfig<SampleFieldKey, Object[]> mapperConfig() {
        FieldMapperColumnDefinitionProviderImpl<SampleFieldKey> provider =
                new FieldMapperColumnDefinitionProviderImpl<SampleFieldKey>();

        provider.addColumnDefinition("benchmark",
                FieldMapperColumnDefinition.<SampleFieldKey>identity().add(new SetterProperty(SETTER)).add(new GetterProperty(GETTER)));
        return MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig().columnDefinitions(provider);
    }
}
