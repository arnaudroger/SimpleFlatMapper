package org.simpleflatmapper.map.context;


import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.ConstantGetter;
import org.simpleflatmapper.reflect.meta.ArrayElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.MapElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.map.context.impl.BreakDetectorMappingContextFactory;
import org.simpleflatmapper.map.context.impl.BreakGetter;
import org.simpleflatmapper.map.context.impl.NullChecker;
import org.simpleflatmapper.map.context.impl.RootBreakGetterProvider;
import org.simpleflatmapper.map.context.impl.ValuedMappingContextFactory;
import org.simpleflatmapper.reflect.setter.AppendCollectionSetter;
import org.simpleflatmapper.util.BooleanProvider;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.Supplier;
import org.simpleflatmapper.util.TrueBooleanProvider;

import java.util.ArrayList;
import java.util.List;

public class MappingContextFactoryBuilder<S, K> {

    private final Counter counter;
    private final int currentIndex;
    private final MappingContextFactoryBuilder<S, K> parent;
    private final List<K> keys;
    private final KeySourceGetter<K, S> keySourceGetter;
    private final List<MappingContextFactoryBuilder<S, K>> children = new ArrayList<MappingContextFactoryBuilder<S, K>>();
    private final List<Supplier<?>> suppliers = new ArrayList<Supplier<?>>();
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

    public void addSupplier(int index, Supplier<?> supplier) {
        while(suppliers.size() <= index) {
            suppliers.add(null);
        }
        suppliers.set(index, supplier);
    }

    public Predicate<S> nullChecker() {
        return new NullChecker<S, K>(keys, keySourceGetter);
    }

    public Getter<MappingContext<? super S>, BooleanProvider> breakDetectorGetter() {
        if (hasNoKeys()) {
            if (owner instanceof ArrayElementPropertyMeta) {
                ArrayElementPropertyMeta elementPropertyMeta = (ArrayElementPropertyMeta) owner;
                if (elementPropertyMeta.getSetter() instanceof AppendCollectionSetter) {
                    return new ConstantGetter<MappingContext<? super S>, BooleanProvider>(TrueBooleanProvider.INSTANCE);
                }
            }

            if (parent != null) {
                // Object in collection with no key and append setter break all the time
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


        MappingContextFactory<S> context;

        if (suppliers.isEmpty()) {
            context = MappingContext.EMPTY_FACTORY;
        } else {
            context = new ValuedMappingContextFactory<S>(suppliers);
        }
        if (!builders.isEmpty()) {
            KeysDefinition<S, K>[] keyDefinions = new KeysDefinition[builders.get(builders.size() - 1).currentIndex + 1];

            int rootDetector = getRootDetector(builders);


            for (int i = 0; i < builders.size(); i++) {
                final MappingContextFactoryBuilder<S, K> builder = builders.get(i);
                int parentIndex = builder.getParentNonEmptyIndex();
                if (parentIndex == -1 && rootDetector != builder.currentIndex()) {
                    parentIndex = rootDetector;
                }
                final KeysDefinition<S, K> keyDefinition = builder.newKeysDefinition(builder.currentIndex, parentIndex);
                keyDefinions[builder.currentIndex] = keyDefinition;
            }

            context = new BreakDetectorMappingContextFactory<S, K>(keyDefinions, rootDetector, context, counter.value);
        }

        return context;

    }

    private int getRootDetector(List<MappingContextFactoryBuilder<S, K>> builders) {
        int rootDetector = -1;
        // calculate rootDetector
        for (int i = 0; i < builders.size(); i++) {
            final MappingContextFactoryBuilder<S, K> builder = builders.get(i);
            if (builder.currentIndex == 0 || (rootDetector == -1 && builder.isEligibleAsRootKey())) {
                rootDetector = builder.currentIndex;
            }
        }
        return rootDetector;
    }

    private boolean isEligibleAsRootKey() {
        return !(owner instanceof ArrayElementPropertyMeta)
                && !(owner instanceof MapElementPropertyMeta)
                && (parent == null || parent.isEligibleAsRootKey());
    }

    private int getParentNonEmptyIndex() {
        if (parent == null) {
            return -1;
        } else {
            if (parent.hasNoKeys()) {
                return parent.getParentNonEmptyIndex();
            } else {
                return parent.currentIndex;
            }
        }
    }

    private KeysDefinition<S, K> newKeysDefinition(int currentIndex, int parent) {
        return new KeysDefinition<S, K>(keys, keySourceGetter, currentIndex, parent);
    }


    private List<MappingContextFactoryBuilder<S, K>> getAllBuilders() {
        List<MappingContextFactoryBuilder<S, K>> list = new ArrayList<MappingContextFactoryBuilder<S, K>>();

        if (!hasNoKeys()) {
            list.add(this);
        }

        for(MappingContextFactoryBuilder<S, K> child : children) {
            list.addAll(child.getAllBuilders());
        }

        return list;
    }

    public boolean hasNoKeys() {
        return keys.isEmpty();
    }

    public boolean hasNoDependentKeys() {
        if (!hasNoKeys()) {
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

    public int currentIndex() {
        return currentIndex;
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
