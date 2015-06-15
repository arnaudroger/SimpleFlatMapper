package org.sfm.map.impl;


import org.sfm.map.MappingContext;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.impl.keys.*;
import org.sfm.reflect.Getter;
import org.sfm.reflect.meta.ListElementPropertyMeta;
import org.sfm.reflect.meta.MapElementPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.BooleanProvider;
import org.sfm.utils.Predicate;

import java.util.ArrayList;
import java.util.List;

public class MappingContextFactoryBuilder<S, K> {

    private final Counter counter;
    private final int currentIndex;
    private final MappingContextFactoryBuilder<S, K> parent;
    private final List<K> keys;
    private final KeySourceGetter<K, S> keySourceGetter;
    private final List<MappingContextFactoryBuilder<S, K>> children = new ArrayList<MappingContextFactoryBuilder<S, K>>();
    private final PropertyMeta<?, ?> owner;

    public MappingContextFactoryBuilder(KeySourceGetter<K, S> keySourceGetter) {
        this(new Counter(), new ArrayList<K>(), keySourceGetter, null, null);
    }

    protected MappingContextFactoryBuilder(Counter counter, List<K> keys, KeySourceGetter<K, S> keySourceGetter, MappingContextFactoryBuilder<S, K> parent, PropertyMeta<?, ?> owner) {
        this.counter = counter;
        this.currentIndex = counter.value;
        this.keys = keys;
        this.keySourceGetter = keySourceGetter;
        this.parent = parent;
        this.counter.value++;
        this.owner = owner;
    }

    public void addKey(K key) {
        if (!keys.contains(key)) {
            keys.add(key);
        }
    }

    public Predicate<S> nullChecker() {
        return new NullChecker<S, K>(keys, keySourceGetter);
    }

    public Getter<MappingContext<S>, BooleanProvider> breakDetectorGetter() {
        if (isEmpty()) {
            if (parent != null) {
                return parent.breakDetectorGetter();
            } else {
                return new RootBreakGetterProvider<S>();
            }
        } else {
            return new BreakGetter<S>(currentIndex);
        }
    }

    public MappingContextFactoryBuilder<S, K> newBuilder(List<K> subKeys, PropertyMeta<?, ?> owner) {
        MappingContextFactoryBuilder<S, K> subBuilder = new MappingContextFactoryBuilder<S, K>(counter, subKeys, keySourceGetter, this, owner);
        children.add(subBuilder);
        return subBuilder;
    }

    @SuppressWarnings("unchecked")
    public MappingContextFactory<S> newFactory() {
        if (parent != null)  {
            throw new IllegalStateException();
        }

        List<MappingContextFactoryBuilder<S, K>> builders = getAllBuilders();

        if (builders.isEmpty()) {
            return new MappingContextFactoryImpl<S, K>(null, -1);
        }

        @SuppressWarnings("unchecked")
        KeysDefinition<S, K>[] keyDefinions = new KeysDefinition[builders.get(builders.size() -1).currentIndex + 1];

        int rootDetector = -1;
        for(int i = 0; i < builders.size(); i++) {
            final MappingContextFactoryBuilder<S, K> builder = builders.get(i);

            final KeysDefinition<S, K> keyDefinition = builder.newKeysDefinition(builder.getParentNonEmptyIndex());
            keyDefinions[builder.currentIndex] = keyDefinition;
            if (builder.currentIndex == 0 || (rootDetector == -1 && builder.isEligibleAsRootKey())) {
                rootDetector = builder.currentIndex;
            }
        }

        return new MappingContextFactoryImpl<S, K>(keyDefinions, rootDetector);
    }

    private boolean isEligibleAsRootKey() {
        return !(owner instanceof ListElementPropertyMeta)
                && !(owner instanceof MapElementPropertyMeta)
                && (parent == null || parent.isEligibleAsRootKey());
    }

    private int getParentNonEmptyIndex() {
        if (parent == null) {
            return -1;
        } else {
            if (parent.isEmpty()) {
                return parent.getParentNonEmptyIndex();
            } else {
                return parent.currentIndex;
            }
        }
    }

    private KeysDefinition<S, K> newKeysDefinition(int parent) {
        return new KeysDefinition<S, K>(keys, keySourceGetter, parent);
    }


    private List<MappingContextFactoryBuilder<S, K>> getAllBuilders() {
        List<MappingContextFactoryBuilder<S, K>> list = new ArrayList<MappingContextFactoryBuilder<S, K>>();

        if (!isEmpty()) {
            list.add(this);
        }

        for(MappingContextFactoryBuilder<S, K> child : children) {
            list.addAll(child.getAllBuilders());
        }

        return list;
    }

    public boolean isEmpty() {
        return keys.isEmpty();
    }

    public boolean hasNoDependentKeys() {
        if (!isEmpty()) {
            return false;
        }

        for(MappingContextFactoryBuilder<S, K> builder : children) {
            if (!builder.hasNoDependentKeys()) {
                return false;
            }
        }
        return true;
    }

    public boolean isRoot() {
        return parent == null;
    }

    private static class Counter {
        int value;
    }

    @Override
    public String toString() {
        return "MappingContextFactoryBuilder{" +
                "currentIndex=" + currentIndex +
                ", keys=" + keys +
                ", children=" + children +
                '}';
    }
}
