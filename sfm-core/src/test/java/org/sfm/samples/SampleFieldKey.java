package org.sfm.samples;

import org.sfm.map.FieldKey;
import org.sfm.map.mapper.TypeAffinity;

public class SampleFieldKey extends FieldKey<SampleFieldKey> implements TypeAffinity {

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
