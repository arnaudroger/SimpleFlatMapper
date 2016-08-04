package  org.simpleflatmapper.core.map;


import org.junit.Test;
import  org.simpleflatmapper.core.map.column.FieldMapperColumnDefinition;
import  org.simpleflatmapper.core.map.mapper.DefaultPropertyNameMatcherFactory;
import  org.simpleflatmapper.core.map.error.RethrowMapperBuilderErrorHandler;
import  org.simpleflatmapper.core.map.mapper.PropertyMapping;
import  org.simpleflatmapper.core.map.mapper.PropertyMappingsBuilder;
import org.simpleflatmapper.core.reflect.ReflectionService;
import org.simpleflatmapper.core.reflect.meta.ClassMeta;
import org.simpleflatmapper.core.reflect.meta.ListElementPropertyMeta;
import org.simpleflatmapper.core.reflect.meta.PropertyMeta;
import org.simpleflatmapper.core.reflect.meta.SubPropertyMeta;
import org.simpleflatmapper.util.ForEachCallBack;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeHelper;


import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PropertyMappingsBuilderTest {


    public static class AnonymousElement {
        public List<String> phones;
    }

    @Test
    public void testStringInArray() {

        final ClassMeta<AnonymousElement> classMeta = ReflectionService.newInstance().getClassMeta(AnonymousElement.class);
        PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder =
                new PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        new RethrowMapperBuilderErrorHandler(),
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });

        builder.addProperty(new SampleFieldKey("phones_value", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());


        assertIsPhonesElement(builder);

    }


    //@Test causes issue with jdbc array
    public void testAnonymousInArray() {

        final ClassMeta<AnonymousElement> classMeta = ReflectionService.newInstance().getClassMeta(AnonymousElement.class);
        PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder2 =
                new PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>(
                        classMeta,
                        DefaultPropertyNameMatcherFactory.DEFAULT,
                        new RethrowMapperBuilderErrorHandler(),
                        new Predicate<PropertyMeta<?, ?>>() {
                            @Override
                            public boolean test(PropertyMeta<?, ?> propertyMeta) {
                                return true;
                            }
                        });

        builder2.addProperty(new SampleFieldKey("phones", 0), FieldMapperColumnDefinition.<SampleFieldKey>identity());

        assertIsPhonesElement(builder2);

    }

    @SuppressWarnings("unchecked")
    private void assertIsPhonesElement(PropertyMappingsBuilder<AnonymousElement, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> builder) {
        final List<PropertyMapping<AnonymousElement, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>> props = new ArrayList<PropertyMapping<AnonymousElement, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>>();
        builder.forEachProperties(new ForEachCallBack<PropertyMapping<AnonymousElement, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>>>() {
            @Override
            public void handle(PropertyMapping<AnonymousElement, ?, SampleFieldKey, FieldMapperColumnDefinition<SampleFieldKey>> pm) {
                props.add(pm);
            }
        });

        assertEquals(1, props.size());

        final PropertyMeta<AnonymousElement, ?> propertyMeta = props.get(0).getPropertyMeta();

        assertTrue("Is sub property", propertyMeta.isSubProperty());

        SubPropertyMeta<AnonymousElement, ?, ?> subPropertyMeta = (SubPropertyMeta<AnonymousElement, ?, ?>) propertyMeta;

        assertTrue(TypeHelper.isAssignable(List.class, subPropertyMeta.getOwnerProperty().getPropertyType()));
        assertTrue(subPropertyMeta.getSubProperty() instanceof ListElementPropertyMeta);
    }
}
