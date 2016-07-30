package org.sfm.samples;

import org.sfm.map.FieldKey;

public class SampleFieldKey extends FieldKey<SampleFieldKey> {

    public SampleFieldKey(String name, int index) {
        super(name, index);
    }

    public SampleFieldKey(String name, int index, SampleFieldKey parent) {
        super(name, index, parent);
    }

    @Override
    public SampleFieldKey alias(String alias) {
        return new SampleFieldKey(alias, index, this);
    }
}
