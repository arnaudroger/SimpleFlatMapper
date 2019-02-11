package org.simpleflatmapper.reflect.test.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public final class ImmutableFoobarValue implements FoobarValue {
    private final int foo;
    private final String bar;
    private final UUID crux;

    private ImmutableFoobarValue(int foo, String bar, UUID crux) {
        this.foo = foo;
        this.bar = bar;
        this.crux = crux;
    }

    public int foo() {
        return this.foo;
    }

    public String bar() {
        return this.bar;
    }

    public UUID crux() {
        return this.crux;
    }

    public final ImmutableFoobarValue withFoo(int value) {
        return this.foo == value ? this : new ImmutableFoobarValue(value, this.bar, this.crux);
    }

    public final ImmutableFoobarValue withBar(String value) {
        if (this.bar.equals(value)) {
            return this;
        } else {
            String newValue = value;
            return new ImmutableFoobarValue(this.foo, newValue, this.crux);
        }
    }

    public final ImmutableFoobarValue withCrux(UUID value) {
        if (this.crux == value) {
            return this;
        } else {
            UUID newValue = value;
            return new ImmutableFoobarValue(this.foo, this.bar, newValue);
        }
    }

    public boolean equals(Object another) {
        if (this == another) {
            return true;
        } else {
            return another instanceof ImmutableFoobarValue && this.equalTo((ImmutableFoobarValue)another);
        }
    }

    private boolean equalTo(ImmutableFoobarValue another) {
        return this.foo == another.foo && this.bar.equals(another.bar) && this.crux.equals(another.crux);
    }

    public int hashCode() {
        int h = 5381 + (5381 << 5) + this.foo;
        h += (h << 5) + this.bar.hashCode();
        h += (h << 5) + this.crux.hashCode();
        return h;
    }

    public String toString() {
        return "FoobarValue{foo=" + this.foo + ", bar=" + this.bar + ", crux=" + this.crux + "}";
    }

    public static ImmutableFoobarValue copyOf(FoobarValue instance) {
        return instance instanceof ImmutableFoobarValue ? (ImmutableFoobarValue)instance : builder().from(instance).build();
    }

    public static ImmutableFoobarValue.Builder builder() {
        return new ImmutableFoobarValue.Builder();
    }

    public static final class Builder {
        private static final long INIT_BIT_FOO = 1L;
        private static final long INIT_BIT_BAR = 2L;
        private static final long INIT_BIT_CRUX = 4L;
        private long initBits;
        private int foo;
        private String bar;
        private UUID crux;

        private Builder() {
            this.initBits = 7L;
        }

        public final ImmutableFoobarValue.Builder from(FoobarValue instance) {
            this.foo(instance.foo());
            this.bar(instance.bar());
            this.crux(instance.crux());
            return this;
        }

        public final ImmutableFoobarValue.Builder foo(int foo) {
            this.foo = foo;
            this.initBits &= -2L;
            return this;
        }

        public final ImmutableFoobarValue.Builder bar(String bar) {
            this.bar = bar;
            this.initBits &= -3L;
            return this;
        }

        public final ImmutableFoobarValue.Builder crux(UUID crux) {
            this.crux = crux;
            this.initBits &= -5L;
            return this;
        }

        public ImmutableFoobarValue build() {
            if (this.initBits != 0L) {
                throw new IllegalStateException(this.formatRequiredAttributesMessage());
            } else {
                return new ImmutableFoobarValue(this.foo, this.bar, this.crux);
            }
        }

        private String formatRequiredAttributesMessage() {
            List<String> attributes = new ArrayList();
            if ((this.initBits & 1L) != 0L) {
                attributes.add("foo");
            }

            if ((this.initBits & 2L) != 0L) {
                attributes.add("bar");
            }

            if ((this.initBits & 4L) != 0L) {
                attributes.add("crux");
            }

            return "Cannot build FoobarValue, some of required attributes are not set " + attributes;
        }
    }
}