package org.simpleflatmapper.test.map.mapper;

import org.junit.Test;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.SetRowMapper;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.util.Predicate;


import static org.junit.Assert.assertEquals;

public class AbstractMapperBuilderDiscriminatorTest {
    
    
    @Test
    public void testDiscriminator() {

        ReflectionService reflectionService = ReflectionService.newInstance();
        ClassMeta<Common> classMeta = reflectionService.getClassMeta(Common.class);


        MapperConfig<SampleFieldKey> mapperConfig = MapperConfig.fieldMapperConfig();
        
        mapperConfig = mapperConfig.discriminator(
                Common.class, 
                new MapperConfig.DiscrimnatorCase<Object[], Common>(new Predicate<Object[]>() {
                    @Override
                    public boolean test(Object[] objects) {
                        return "str".equals(objects[3]);
                    }
                }, reflectionService.getClassMeta(StringValue.class)),
                new MapperConfig.DiscrimnatorCase<Object[], Common>(new Predicate<Object[]>() {
                    @Override
                    public boolean test(Object[] objects) {
                        return "int".equals(objects[3]);
                    }
                }, reflectionService.getClassMeta(IntegerValue.class))
        );

        AbstractMapperBuilderTest.SampleMapperBuilder<Common> builder = new AbstractMapperBuilderTest.SampleMapperBuilder<Common>(classMeta, mapperConfig);
        
        builder.addMapping("id");
        builder.addMapping("valueStr");
        builder.addMapping("valueInt");

        SetRowMapper<Object[], Object[][], Common, Exception> mapper = builder.mapper();

        StringValue stringValue = (StringValue) mapper.map(new Object[] {1l, "strValue", 2, "str"});
        assertEquals(1, stringValue.id);
        assertEquals("strValue", stringValue.valueStr);

        IntegerValue integerValue = (IntegerValue) mapper.map(new Object[] {2l, "str", 3, "int"});
        assertEquals(2, integerValue.id);
        assertEquals(3, integerValue.valueInt);

    }
    
    
    public static abstract class Common {
        public final long id;

        Common(long id) {
            this.id = id;
        }
    }
    
    public static class StringValue extends Common {
        public final String valueStr;

        public StringValue(long id, String valueStr) {
            super(id);
            this.valueStr = valueStr;
        }
    }

    public static class IntegerValue extends Common {
        public final int valueInt;

        public IntegerValue(long id, int valueInt) {
            super(id);
            this.valueInt = valueInt;
        }
    }
}
