package org.simpleflatmapper.reflect.test.meta;


import org.junit.Test;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ArrayClassMeta;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.util.Consumer;
import org.simpleflatmapper.util.TypeReference;

import java.util.*;

import static org.junit.Assert.*;

public class ListClassMetaTest {


    @Test
    public void testForEach() {
        try {
            ReflectionService.newInstance().getClassMeta(new TypeReference<List<String>>() {}.getType()).forEachProperties(new Consumer<PropertyMeta<?, ?>>() {
                @Override
                public void accept(PropertyMeta<?, ?> dbObjectPropertyMeta) {
                }
            });
            fail();
        } catch (UnsupportedOperationException e) {
            // expected
        }

    }

    @Test
    public void testUseArrayListOnList() throws NoSuchMethodException {
        typeMetaHasTheSpecifiedClassEmptyConstructor(
                new TypeReference<List<String>>() {},
                ArrayList.class);

    }

    @Test
    public void testUseSpecifiedImplType() throws NoSuchMethodException {
        typeMetaHasTheSpecifiedClassEmptyConstructor(
                new TypeReference<MyList>() {},
                MyList.class);
    }

    static class MyList extends LinkedList<String> {

    }



    private void typeMetaHasTheSpecifiedClassEmptyConstructor(TypeReference<?> typeReference, Class<?> impl) throws NoSuchMethodException {
        final ClassMeta<List<String>> classMeta =
                ReflectionService.newInstance().getClassMeta(typeReference.getType());

        hasOneInstantiatorDefinitionWithEmptyConstructorOnImpl(classMeta, impl);
    }



    private void hasOneInstantiatorDefinitionWithEmptyConstructorOnImpl(ClassMeta<?> classMeta, Class<?> impl) throws NoSuchMethodException {
        assertTrue(classMeta instanceof ArrayClassMeta);
        final List<InstantiatorDefinition> instantiatorDefinitions = classMeta.getInstantiatorDefinitions();

        assertEquals(1, instantiatorDefinitions.size());
        final ExecutableInstantiatorDefinition instantiatorDefinition = (ExecutableInstantiatorDefinition) instantiatorDefinitions.get(0);

        assertEquals(0, instantiatorDefinition.getParameters().length);
        assertEquals(impl.getDeclaredConstructor(), instantiatorDefinition.getExecutable());
    }
}
