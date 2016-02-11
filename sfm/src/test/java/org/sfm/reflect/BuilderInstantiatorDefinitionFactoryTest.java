package org.sfm.reflect;

import org.junit.Test;
import org.sfm.reflect.asm.AsmFactory;
import org.sfm.reflect.impl.BuilderInstantiator;
import org.sfm.reflect.impl.ConstantGetter;
import org.sfm.reflect.impl.ConstantIntGetter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BuilderInstantiatorDefinitionFactoryTest {

    @Test
    public void testBuilderFromMethod() throws Exception {
        final List<InstantiatorDefinition> instantiatorDefinitions = BuilderInstantiatorDefinitionFactory.extractDefinitions(ClassBuilderWithMethod.class);

        assertEquals(1, instantiatorDefinitions.size());

        BuilderInstantiatorDefinition b = (BuilderInstantiatorDefinition) instantiatorDefinitions.get(0);

        assertEquals(ClassBuilderWithMethod.Builder.class.getName(), b.getName());

        // builder instantiator
        final ExecutableInstantiatorDefinition builderInstantiator = (ExecutableInstantiatorDefinition) b.getBuilderInstantiator();
        assertEquals(ClassBuilderWithMethod.class.getMethod("builder"), builderInstantiator.getExecutable());
        assertEquals(0, builderInstantiator.getParameters().length);

        final Parameter[] parameters = b.getParameters();

        assertEquals(2, parameters.length);

        Arrays.sort(parameters, new Comparator<Parameter>() {
            @Override
            public int compare(Parameter o1, Parameter o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        assertEquals("name", parameters[1].getName());
        assertEquals("id", parameters[0].getName());

        Map<Parameter, Getter<? super Void, ?>> params = new HashMap<Parameter, Getter<? super Void, ?>>();

        params.put(parameters[1], new ConstantGetter<Void, Object>("myname"));
        params.put(parameters[0], new ConstantIntGetter<Void>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(new AsmFactory(getClass().getClassLoader()), true);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);
        assertFalse((instantiator instanceof BuilderInstantiator));

        assertEquals("myname", o.getName());
        assertEquals(3, o.getId());
    }

    @Test
    public void testBuilderFromMethodNoAsm() throws Exception {
        final List<InstantiatorDefinition> instantiatorDefinitions = BuilderInstantiatorDefinitionFactory.extractDefinitions(ClassBuilderWithMethod.class);

        assertEquals(1, instantiatorDefinitions.size());

        BuilderInstantiatorDefinition b = (BuilderInstantiatorDefinition) instantiatorDefinitions.get(0);

        final Parameter[] parameters = b.getParameters();

        Arrays.sort(parameters, new Comparator<Parameter>() {
            @Override
            public int compare(Parameter o1, Parameter o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Map<Parameter, Getter<? super Void, ?>> params = new HashMap<Parameter, Getter<? super Void, ?>>();

        params.put(parameters[1], new ConstantGetter<Void, Object>("myname"));
        params.put(parameters[0], new ConstantIntGetter<Void>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertEquals("myname", o.getName());
        assertEquals(3, o.getId());
    }


    @Test
    public void testBuilderFromMethodAsmBoxing() throws Exception {
        final List<InstantiatorDefinition> instantiatorDefinitions = BuilderInstantiatorDefinitionFactory.extractDefinitions(ClassBuilderWithMethod.class);

        assertEquals(1, instantiatorDefinitions.size());

        BuilderInstantiatorDefinition b = (BuilderInstantiatorDefinition) instantiatorDefinitions.get(0);

        final Parameter[] parameters = b.getParameters();

        Arrays.sort(parameters, new Comparator<Parameter>() {
            @Override
            public int compare(Parameter o1, Parameter o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        Map<Parameter, Getter<? super Void, ?>> params = new HashMap<Parameter, Getter<? super Void, ?>>();

        params.put(parameters[1], new ConstantGetter<Void, Object>("myname"));
        params.put(parameters[0], new ConstantGetter<Void, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(new AsmFactory(getClass().getClassLoader()), true);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertFalse((instantiator instanceof BuilderInstantiator));
        assertEquals("myname", o.getName());
        assertEquals(3, o.getId());
    }


    public static abstract class ClassBuilderWithMethod {

        public abstract String getName();
        public abstract int getId();

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private String name;
            private int id;


            public Builder id(int id) {
                this.id = id;
                return this;
            }

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public ClassBuilderWithMethod build() {
                return new ClassBuilderWithMethodImpl(name, id);
            }
        }

        private static class ClassBuilderWithMethodImpl extends ClassBuilderWithMethod {
            private final String name;
            private final int id;

            private ClassBuilderWithMethodImpl(String name, int id) {
                this.name = name;
                this.id = id;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getId() {
                return id;
            }
        }

    }
}