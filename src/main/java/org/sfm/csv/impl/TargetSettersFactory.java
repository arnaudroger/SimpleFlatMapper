package org.sfm.csv.impl;

import org.sfm.csv.CsvColumnKey;
import org.sfm.reflect.Instantiator;

public class TargetSettersFactory<T> {

    private final Instantiator<AbstractTargetSetters<T>, T> instantiator;
    private final CsvColumnKey[] keys;


    public TargetSettersFactory(Instantiator<AbstractTargetSetters<T>, T> instantiator, CsvColumnKey[] keys) {
        this.instantiator = instantiator;
        this.keys = keys;
    }

    public AbstractTargetSetters<T> newInstace(DelayedCellSetter<T, ?>[] delayedSetters, CellSetter<T>[] setters) {
        return new CsvMapperObjectSetters(instantiator, delayedSetters, setters, keys);
    }

    @Override
    public String toString() {
        return "TargetSettersFactory{" +
                "instantiator=" + instantiator +
                '}';
    }
}
