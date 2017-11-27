package org.simpleflatmapper.test.map;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.reflect.TypeAffinity;

import java.lang.reflect.Type;
import java.util.Arrays;

public class SampleFieldKey extends FieldKey<SampleFieldKey> implements TypeAffinity {

    public static final KeyFactory<SampleFieldKey> KEY_FACTORY = new KeyFactory<SampleFieldKey>() {
        @Override
        public SampleFieldKey newKey(String name, int i) {
            return new SampleFieldKey(name, i);
        }
    } ;
    private final Class<?>[] affinities;
    private final Type type;

    public SampleFieldKey(String name, int index) {
        this(name, index, new Class[0]);
    }

    public SampleFieldKey(String name, int index, Class<?>... affinities) {
        this(name, index, affinities, Object.class, null);
    }

    public SampleFieldKey(String name, int index, Class<?>[] affinities, Type type) {
        this(name, index, affinities, type, null);
    }

    public SampleFieldKey(String name, int index, Class<?>[] affinities, Type type, SampleFieldKey parent) {
        super(name, index, parent);
        this.affinities = affinities;
        this.type = type;

    }

    public SampleFieldKey(String name, int index, SampleFieldKey parent) {
        this(name, index, new Class[0], Object.class, parent);
    }

    @Override
    public Type getType(Type targetType) {
        return type;
    }

    @Override
    public SampleFieldKey alias(String alias) {
        return new SampleFieldKey(alias, index, this);
    }


    @Override
    public Class<?>[] getAffinities() {
        return affinities;
    }

    @Override
    public String toString() {
        return "SampleFieldKey{" +
                "name=" + getName() +
                ", affinities=" + Arrays.toString(affinities) +
                ", type=" + type +
                '}';
    }
}
