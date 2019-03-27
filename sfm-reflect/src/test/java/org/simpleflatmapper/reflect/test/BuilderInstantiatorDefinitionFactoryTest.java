package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.*;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.impl.BuilderInstantiator;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.ConstantIntGetter;
import org.simpleflatmapper.reflect.impl.BuilderInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.simpleflatmapper.reflect.test.Utils.TEST_ASM_FACTORY_PROVIDER;

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

        assertEquals(3, parameters.length);

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

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true, true);
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
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertEquals("myname", o.getName());
        assertEquals(3, o.getId());
    }


    @Test
    public void testBuilderFromMethodNullHandling() throws Exception {
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

        params.put(parameters[1], new ConstantGetter<Void, Object>(null));
        params.put(parameters[0], new ConstantGetter<Void, Integer>(null));


        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertEquals(null, o.getName());
        assertEquals(0, o.getId());
    }
    @Test
    public void testBuilderFromMethodNullHandlingNoAsm() throws Exception {
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

        params.put(parameters[1], new ConstantGetter<Void, Object>(null));
        params.put(parameters[0], new ConstantGetter<Void, Integer>(null));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, false, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertEquals(null, o.getName());
        assertEquals(0, o.getId());
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

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertFalse((instantiator instanceof BuilderInstantiator));
        assertEquals("myname", o.getName());
        assertEquals(3, o.getId());
    }


    @Test
    public void testBuilderFromMethodVoid() throws Exception {
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

        params.put(parameters[2], new ConstantGetter<Void, Object>("zrux"));
        params.put(parameters[0], new ConstantGetter<Void, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertFalse((instantiator instanceof BuilderInstantiator));
        assertEquals(3, o.getId());
        assertEquals("zrux", o.getZrux());
    }

    @Test
    public void testBuilderFromMethodVoidNoAsm() throws Exception {
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

        params.put(parameters[2], new ConstantGetter<Void, Object>("zrux"));
        params.put(parameters[0], new ConstantGetter<Void, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null, true);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, false, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertTrue((instantiator instanceof BuilderInstantiator));
        assertEquals(3, o.getId());
        assertEquals("zrux", o.getZrux());
    }

    @Test
    public void testBuilderFromMethodVoidNullHandling() throws Exception {
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

        params.put(parameters[2], new ConstantGetter<Void, Object>(null));
        params.put(parameters[0], new ConstantGetter<Void, Integer>(null));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertFalse((instantiator instanceof BuilderInstantiator));
        assertEquals(0, o.getId());
        assertEquals(null, o.getZrux());
    }

    @Test
    public void testBuilderFromMethodVoidNoAsmNullHandling() throws Exception {
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

        params.put(parameters[2], new ConstantGetter<Void, Object>(null));
        params.put(parameters[0], new ConstantGetter<Void, Integer>(null));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null, true);
        final Instantiator<Void, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, ClassBuilderWithMethod>getInstantiator(b, Void.class, params, false, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null);

        assertTrue((instantiator instanceof BuilderInstantiator));
        assertEquals(0, o.getId());
        assertEquals(null, o.getZrux());
    }
    
    
    public static abstract class ClassBuilderWithMethod {

        public abstract String getName();
        public abstract int getId();
        public abstract String getZrux();

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private final String name;
            private final int id;
            private String zrux;

            private Builder() {
                name = null;
                id = 0;
                zrux = null;
            }

            public Builder(String name, int id, String zrux) {
                this.name = name;
                this.id = id;
                this.zrux = zrux;
            }


            public Builder id(int id) {
                return new Builder(name, id, zrux);
            }

            public Builder name(String name) {
                return new Builder(name, id, zrux);
            }

            public void zrux(String zrux) {
                this.zrux = zrux;
            }

            public ClassBuilderWithMethod build() {
                return new ClassBuilderWithMethodImpl(name, id, zrux);
            }
        }

        private static class ClassBuilderWithMethodImpl extends ClassBuilderWithMethod {
            private final String name;
            private final String zrux;
            private final int id;

            private ClassBuilderWithMethodImpl(String name, int id, String zrux) {
                this.name = name;
                this.id = id;
                this.zrux = zrux;
            }

            @Override
            public String getName() {
                return name;
            }

            @Override
            public int getId() {
                return id;
            }

            @Override
            public String getZrux() {
                return zrux;
            }
        }

    }
}