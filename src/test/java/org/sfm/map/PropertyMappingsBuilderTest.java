package org.sfm.map;


import org.junit.Test;
import org.sfm.csv.CsvColumnDefinition;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.impl.DefaultPropertyNameMatcherFactory;
import org.sfm.map.impl.PropertyMapping;
import org.sfm.map.impl.PropertyMappingsBuilder;
import org.sfm.map.impl.RethrowMapperBuilderErrorHandler;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.TypeHelper;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.ListElementPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.reflect.meta.SubPropertyMeta;
import org.sfm.utils.ForEachCallBack;


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

        final ClassMeta<AnonymousElement> classMeta = ReflectionService.newInstance().getClassMeta(AnonymousElement.class, true);
        PropertyMappingsBuilder<AnonymousElement, CsvColumnKey, CsvColumnDefinition> builder =
                new PropertyMappingsBuilder<AnonymousElement, CsvColumnKey, CsvColumnDefinition>(
                        classMeta,
                        new DefaultPropertyNameMatcherFactory(),
                        new RethrowMapperBuilderErrorHandler()
                );

        builder.addProperty(new CsvColumnKey("phones_value", 0), CsvColumnDefinition.IDENTITY);


        assertIsPhonesElement(builder);

    }


    //@Test causes issue with jdbc array
    public void testAnonymousInArray() {

        final ClassMeta<AnonymousElement> classMeta = ReflectionService.newInstance().getClassMeta(AnonymousElement.class, true);
        PropertyMappingsBuilder<AnonymousElement, CsvColumnKey, CsvColumnDefinition> builder2 =
                new PropertyMappingsBuilder<AnonymousElement, CsvColumnKey, CsvColumnDefinition>(
                        classMeta,
                        new DefaultPropertyNameMatcherFactory(),
                        new RethrowMapperBuilderErrorHandler()
                );

        builder2.addProperty(new CsvColumnKey("phones", 0), CsvColumnDefinition.IDENTITY);

        assertIsPhonesElement(builder2);

    }


    private void assertIsPhonesElement(PropertyMappingsBuilder<AnonymousElement, CsvColumnKey, CsvColumnDefinition> builder) {
        final List<PropertyMapping<AnonymousElement, ?, CsvColumnKey, CsvColumnDefinition>> props = new ArrayList<PropertyMapping<AnonymousElement, ?, CsvColumnKey, CsvColumnDefinition>>();
        builder.forEachProperties(new ForEachCallBack<PropertyMapping<AnonymousElement, ?, CsvColumnKey, CsvColumnDefinition>>() {
            @Override
            public void handle(PropertyMapping<AnonymousElement, ?, CsvColumnKey, CsvColumnDefinition> pm) {
                props.add(pm);
            }
        });

        assertEquals(1, props.size());

        final PropertyMeta<AnonymousElement, ?> propertyMeta = props.get(0).getPropertyMeta();

        assertTrue("Is sub property", propertyMeta.isSubProperty());

        SubPropertyMeta<AnonymousElement, ?> subPropertyMeta = (SubPropertyMeta<AnonymousElement, ?>) propertyMeta;

        assertTrue(TypeHelper.isAssignable(List.class, subPropertyMeta.getOwnerProperty().getType()));
        assertTrue(subPropertyMeta.getSubProperty() instanceof ListElementPropertyMeta);
    }
}
