package org.simpleflatmapper.reflect.test;

import org.junit.Test;
import org.simpleflatmapper.reflect.BiInstantiator;
import org.simpleflatmapper.reflect.BuilderInstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorDefinition;
import org.simpleflatmapper.reflect.InstantiatorFactory;
import org.simpleflatmapper.reflect.Parameter;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.BuilderBiInstantiator;
import org.simpleflatmapper.reflect.impl.BuilderInstantiatorDefinitionFactory;
import org.simpleflatmapper.reflect.instantiator.ExecutableInstantiatorDefinition;
import org.simpleflatmapper.util.BiFunction;
import org.simpleflatmapper.util.ConstantBiFunction;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.simpleflatmapper.reflect.test.Utils.TEST_ASM_FACTORY_PROVIDER;

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

        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();

        params.put(parameters[1], new ConstantBiFunction<Void, Object, String>("myname"));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);
        assertFalse((instantiator instanceof BuilderBiInstantiator));

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

        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[1], new ConstantBiFunction<Void, Object, String>(null));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(null));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertEquals(null, o.getName());
        assertEquals(0, o.getId());
    }


    @Test
    public void testBuilderFromMethodNoAsmNullHandling() throws Exception {
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

        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[1], new ConstantBiFunction<Void, Object, String>(null));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(null));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, false, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertEquals(null, o.getName());
        assertEquals(0, o.getId());
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

        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[1], new ConstantBiFunction<Void, Object, String>("myname"));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, false, true);
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

        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[1], new ConstantBiFunction<Void, Object, String>("myname"));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true, true);
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
        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[2], new ConstantBiFunction<Void, Object, String>("zrux"));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object,ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertFalse((instantiator instanceof BuilderBiInstantiator));
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
        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[2], new ConstantBiFunction<Void, Object, String>(null));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(null));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object,ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertFalse((instantiator instanceof BuilderBiInstantiator));
        assertEquals(0, o.getId());
        assertEquals(null, o.getZrux());
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
        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[2], new ConstantBiFunction<Void, Object, String>("zrux"));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(3));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null, true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertTrue((instantiator instanceof BuilderBiInstantiator));
        assertEquals(3, o.getId());
        assertEquals("zrux", o.getZrux());
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
        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[2], new ConstantBiFunction<Void, Object, String>(null));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(null));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null, true);
        final BiInstantiator<Void, Object, ClassBuilderWithMethod> instantiator = instantiatorFactory
                .<Void, Object, ClassBuilderWithMethod>getBiInstantiator(b, Void.class, Object.class, params, true, true);
        final ClassBuilderWithMethod o = instantiator
                .newInstance(null, null);

        assertTrue((instantiator instanceof BuilderBiInstantiator));
        assertEquals(0, o.getId());
        assertEquals(null, o.getZrux());
    }

    @Test
    public void testBuilderExtendsFromMethod() throws Exception {
        final List<InstantiatorDefinition> instantiatorDefinitions = BuilderInstantiatorDefinitionFactory.extractDefinitions(User.class);

        assertEquals(1, instantiatorDefinitions.size());

        BuilderInstantiatorDefinition b = (BuilderInstantiatorDefinition) instantiatorDefinitions.get(0);

        assertEquals(User.Builder.class.getName(), b.getName());

        // builder instantiator
        final ExecutableInstantiatorDefinition builderInstantiator = (ExecutableInstantiatorDefinition) b.getBuilderInstantiator();
        assertEquals(User.class.getMethod("builder"), builderInstantiator.getExecutable());
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

        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();

        params.put(parameters[1], new ConstantBiFunction<Void, Object, String>("myname"));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(1));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(TEST_ASM_FACTORY_PROVIDER, true);
        final BiInstantiator<Void, Object, User> instantiator = instantiatorFactory
                .<Void, Object, User>getBiInstantiator(b, Void.class, Object.class, params, true, true);
        final User o = instantiator
                .newInstance(null, null);
        assertFalse((instantiator instanceof BuilderBiInstantiator));

        assertEquals("myname", o.getName());
        assertEquals(1, o.getId());
    }


    @Test
    public void testBuilderExtendsFromMethodNoAsm() throws Exception {
        final List<InstantiatorDefinition> instantiatorDefinitions = BuilderInstantiatorDefinitionFactory.extractDefinitions(User.class);

        assertEquals(1, instantiatorDefinitions.size());

        BuilderInstantiatorDefinition b = (BuilderInstantiatorDefinition) instantiatorDefinitions.get(0);

        final Parameter[] parameters = b.getParameters();

        Arrays.sort(parameters, new Comparator<Parameter>() {
            @Override
            public int compare(Parameter o1, Parameter o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        Map<Parameter, BiFunction<? super Void, ? super Object, ?>> params = new HashMap<Parameter, BiFunction<? super Void, ? super Object, ?>>();
        params.put(parameters[1], new ConstantBiFunction<Void, Object, String>("myname"));
        params.put(parameters[0], new ConstantBiFunction<Void, Object, Integer>(1));

        final InstantiatorFactory instantiatorFactory = new InstantiatorFactory(null);
        final BiInstantiator<Void, Object, User> instantiator = instantiatorFactory
                .<Void, Object, User>getBiInstantiator(b, Void.class, Object.class, params, false, true);
        final User o = instantiator
                .newInstance(null, null);

        assertEquals("myname", o.getName());
        assertEquals(1, o.getId());
    }


    public static abstract class AbstractAccount {
        private final int id;

        AbstractAccount(Builder builder) {
            id = builder.id;
        }

        public int getId() {
            return id;
        }

        @SuppressWarnings("unchecked")
        public static abstract class Builder<T extends Builder> {
            private int id;

            public T id(int val) {
                id = val;
                return (T) this;
            }

        }
    }

    public static class User extends AbstractAccount {
        private final String name;

        User(Builder builder) {
            super(builder);
            this.name = builder.name;
        }

        public String getName() {
            return name;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder extends AbstractAccount.Builder<Builder> {
            private String name;

            public Builder name(String val) {
                name = val;
                return this;
            }

            public User build() {
                return new User(this);
            }
        }
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
                if (name == null) throw new NullPointerException();
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