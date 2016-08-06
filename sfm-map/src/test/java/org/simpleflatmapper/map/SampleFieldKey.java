package org.simpleflatmapper.map;

import org.simpleflatmapper.map.mapper.KeyFactory;
import org.simpleflatmapper.reflect.TypeAffinity;

public class SampleFieldKey extends FieldKey<SampleFieldKey> implements TypeAffinity {

    public static final KeyFactory<SampleFieldKey> KEY_FACTORY = new KeyFactory<SampleFieldKey>() {
        @Override
        public SampleFieldKey newKey(String name, int i) {
            return new SampleFieldKey(name, i);
        }
    } ;
    private final Class<?>[] affinities;

    public SampleFieldKey(String name, int index) {
        super(name, index);
        this.affinities = new Class[0];
    }

    public SampleFieldKey(String name, int index, Class<?>... affinities) {
        super(name, index);
        this.affinities = affinities;
    }

    public SampleFieldKey(String name, int index, SampleFieldKey parent) {
        super(name, index, parent);
        this.affinities = new Class[0];
    }

    @Override
    public SampleFieldKey alias(String alias) {
        return new SampleFieldKey(alias, index, this);
    }


    @Override
    public Class<?>[] getAffinities() {
        return affinities;
    }
}
