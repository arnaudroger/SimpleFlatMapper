package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Instantiator;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.getter.ConstantIntGetter;
import org.simpleflatmapper.reflect.impl.BuilderBiInstantiator;
import org.simpleflatmapper.reflect.impl.BuilderInstantiator;
import org.simpleflatmapper.reflect.impl.BuilderInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.util.BiFactory;
import org.simpleflatmapper.util.ConstantBiFactory;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BuilderBiInstantiatorDefinitionFactoryTest {

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

        Map<Parameter, BiFactory<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFactory<? super Void, ? super Object, ?>>();

        params.put(parameters[1], new ConstantBiFactory<Void, Object, String>("myname"));
        params.put(parameters[0], new ConstantBiFactory<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(new AsmFactory(getClass().getClassLoader()), true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);
        assertFalse((instantiator instanceof BuilderBiInstantiator));

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

        Map<Parameter, BiFactory<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFactory<? super Void, ? super Object, ?>>();
        params.put(parameters[1], new ConstantBiFactory<Void, Object, String>("myname"));
        params.put(parameters[0], new ConstantBiFactory<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

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

        Map<Parameter, BiFactory<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFactory<? super Void, ? super Object, ?>>();
        params.put(parameters[1], new ConstantBiFactory<Void, Object, String>("myname"));
        params.put(parameters[0], new ConstantBiFactory<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(new AsmFactory(getClass().getClassLoader()), true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertFalse((instantiator instanceof BuilderBiInstantiator));
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
        Map<Parameter, BiFactory<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFactory<? super Void, ? super Object, ?>>();
        params.put(parameters[2], new ConstantBiFactory<Void, Object, String>("zrux"));
        params.put(parameters[0], new ConstantBiFactory<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(new AsmFactory(getClass().getClassLoader()), true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object,ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertFalse((instantiator instanceof BuilderBiInstantiator));
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
        Map<Parameter, BiFactory<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFactory<? super Void, ? super Object, ?>>();
        params.put(parameters[2], new ConstantBiFactory<Void, Object, String>("zrux"));
        params.put(parameters[0], new ConstantBiFactory<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null, true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertTrue((instantiator instanceof BuilderBiInstantiator));
        assertEquals(3, o.getId());
        assertEquals("zrux", o.getZrux());
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