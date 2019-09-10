package org.simpleflatmapper.test.map.issue;

import org.junit.Test;
import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.property.MapTypeProperty;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.test.map.mapper.AbstractMapperBuilderTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class Issue682Test {

    public static class Data {
        public final String username;
        public final List<UUID> foodUuid;
        public final List<String> foodName;

        public Data(String username, List<UUID> foodUuid, List<String> foodName) {
            this.username = username;
            this.foodUuid = foodUuid;
            this.foodName = foodName;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Data data = (Data) o;

            if (username != null ? !username.equals(data.username) : data.username != null) return false;
            if (foodUuid != null ? !foodUuid.equals(data.foodUuid) : data.foodUuid != null) return false;
            return foodName != null ? foodName.equals(data.foodName) : data.foodName == null;
        }

        @Override
        public int hashCode() {
            int result = username != null ? username.hashCode() : 0;
            result = 31 * result + (foodUuid != null ? foodUuid.hashCode() : 0);
            result = 31 * result + (foodName != null ? foodName.hashCode() : 0);
            return result;
        }
    }


    @Test
    public void testMap() throws Exception {

        ClassMeta<Data> classMeta = ReflectionService.newInstance(false).getClassMeta(Data.class);

        AbstractMapperBuilderTest.SampleMapperBuilder<Data> builder =
                new AbstractMapperBuilderTest.SampleMapperBuilder<Data>(classMeta, MapperConfig.<SampleFieldKey, Object[]>fieldMapperConfig());

        EnumerableMapper<Object[][], Data, ?> mapper =
                builder
                        .addMapping("username")
                        .addMapping("food_uuid")
                        .addMapping("food_name")
                        .mapper();

        UUID uuid = UUID.randomUUID();
        Data data = mapper.iterator(new Object[][]{{"username", uuid, "name"}}).next();

        assertEquals(new Data("username", Arrays.asList(uuid), Arrays.asList("name")), data);
    }
}
