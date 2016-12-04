package org.simpleflatmapper.test.issue;

import org.junit.Test;
import org.simpleflatmapper.map.CaseInsensitiveFieldKeyNamePredicate;
import org.simpleflatmapper.map.mapper.FieldMapperColumnDefinitionProviderImpl;
import org.simpleflatmapper.map.Mapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.SetterProperty;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.test.map.mapper.AbstractMapperBuilderTest;

import java.io.IOException;

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
    public void testMapOnCustomSetter() throws IOException {

        ClassMeta<Data> classMeta = ReflectionService.newInstance().getClassMeta(Data.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Data> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Data>(classMeta, mapperConfig());

        Mapper<Object[], Data> mapper =
                builder
                        .addMapping("score")
                        .addMapping("benchmark")
                        .mapper();

        Data data = mapper.map(new Object[]{3.455, "algo.type"});

        assertEquals(3.455, data.score, 0.0);
        assertEquals("algo", data.algorithm);
        assertEquals("type", data.type);
    }

    @Test
    public void testMapOnCustomSetterSubProperty() throws IOException {

        ClassMeta<DataHolder> classMeta = ReflectionService.newInstance().getClassMeta(DataHolder.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<DataHolder> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<DataHolder>(classMeta, mapperConfig());

        Mapper<Object[], DataHolder> mapper =
                builder
                        .addMapping("data_score")
                        .addMapping("data_benchmark")
                        .mapper();

        DataHolder data = mapper.map(new Object[]{3.455, "algo.type"});

        assertEquals(3.455, data.data.score, 0.0);
        assertEquals("algo", data.data.algorithm);
        assertEquals("type", data.data.type);
    }

    private MapperConfig<SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> mapperConfig() {
        FieldMapperColumnDefinitionProviderImpl<SampleFieldKey> provider =
                new FieldMapperColumnDefinitionProviderImpl<SampleFieldKey>();

        provider.addColumnDefinition(new CaseInsensitiveFieldKeyNamePredicate("benchmark"),
                FieldMapperColumnDefinition.<SampleFieldKey>identity().add(new SetterProperty(SETTER)));
        return MapperConfig.<SampleFieldKey>fieldMapperConfig().columnDefinitions(provider);
    }
}
