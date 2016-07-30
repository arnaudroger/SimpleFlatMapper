package org.sfm.map;


import org.junit.Test;
import org.sfm.map.column.FieldMapperColumnDefinition;
import org.sfm.map.mapper.DefaultPropertyNameMatcherFactory;
import org.sfm.map.error.RethrowMapperBuilderErrorHandler;
import org.sfm.map.mapper.PropertyMapping;
import org.sfm.map.mapper.PropertyMappingsBuilder;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ListElementPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.SubPropertyMeta;
import org.sfm.samples.SampleFieldKey;
import org.sfm.utils.ForEachCallBack;
import org.sfm.utils.Predicate;


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

        builder.addProperty(new SampleFieldKey("phones_value", 0), FieldMapperColumnDefinition.identity());


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

        builder2.addProperty(new SampleFieldKey("phones", 0), FieldMapperColumnDefinition.identity());

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
