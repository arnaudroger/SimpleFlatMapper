package org.simpleflatmapper.reflect.test.meta;

import org.junit.Test;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.setter.NullSetter;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.ConstantPredicate;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.TypeReference;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class OptionalClassMetaTest {

    private final Type dbObjectType = new TypeReference<Optional<DbObject>>() {
    }.getType();

    ClassMeta<Optional<DbObject>> objectClassMeta = ReflectionService.newInstance().getClassMeta(dbObjectType);

    ClassMeta<Optional<String>> stringClassMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<Optional<String>>() {
    }.getType());

    private PropertyFinder.PropertyFilter isValidPropertyMeta = PropertyFinder.PropertyFilter.trueFilter();
    @Test
    public void testFindProperty() throws Exception {
        PropertyMeta<Optional<DbObject>, String> email = objectClassMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("email"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);

        DbObject dbObject = new DbObject();
        dbObject.setEmail("houlala 2 la mission!");

        Optional<DbObject> optional = Optional.of(dbObject);

        assertEquals("houlala 2 la mission!",   email.getGetter().get(optional));

        email.getSetter().set(optional, "OuiOui");

        assertEquals("OuiOui",   email.getGetter().get(optional));

        assertEquals(null,  email.getGetter().get(Optional.<DbObject>empty()));



        PropertyMeta<Optional<String>, String> strValue = stringClassMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of("value"), new Object[0], (TypeAffinity)null, isValidPropertyMeta);

        assertEquals("str", strValue.getGetter().get(Optional.of("str")));
        assertTrue(NullSetter.isNull(strValue.getSetter()));



    }

    @Test
    public void testInstantiatorDefinition() throws Exception {
        List<InstantiatorDefinition> instantiatorDefinitions = objectClassMeta.getInstantiatorDefinitions();

        assertEquals(1, instantiatorDefinitions.size());

        InstantiatorDefinition id = instantiatorDefinitions.get(0);

        assertEquals(1, id.getParameters().length);
        assertEquals("value", id.getParameters()[0].getName());
    }

    @Test
    public void testForEach() {
        List<String> names = new ArrayList<String>();
        ClassMeta<Object> classMeta = ReflectionService.newInstance().getClassMeta(new TypeReference<Optional<DbObject>>() {
        }.getType());
        classMeta.forEachProperties(new Consumer<PropertyMeta<?, ?>>() {
            @Override
            public void accept(PropertyMeta<?, ?> dbObjectPropertyMeta) {
                names.add(dbObjectPropertyMeta.getName());
            }
        });

        assertEquals(Arrays.asList("value"), names);
    }


}
