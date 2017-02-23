package org.simpleflatmapper.map.context;


import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.impl.KeyDefinitionBuilder;
import org.simpleflatmapper.reflect.meta.ArrayElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.MapElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.map.context.impl.BreakDetectorMappingContextFactory;
import org.simpleflatmapper.map.context.impl.NullChecker;
import org.simpleflatmapper.map.context.impl.ValuedMappingContextFactory;
import org.simpleflatmapper.reflect.setter.AppendCollectionSetter;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.Supplier;

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


        MappingContextFactory<S> context;

        if (suppliers.isEmpty()) {
            context = MappingContext.EMPTY_FACTORY;
        } else {
            context = new ValuedMappingContextFactory<S>(suppliers);
        }

        ArrayList<MappingContextFactoryBuilder<S, K>> builders = new ArrayList<MappingContextFactoryBuilder<S, K>>();
        addAllBuilders(builders);

        if (hasKeys(builders)) {
            KeyDefinitionBuilder<S, K>[] keyDefinitionsBuilder = new KeyDefinitionBuilder[builders.get(builders.size() - 1).currentIndex + 1];

            int rootDetector = getRootDetector(builders);

            for (int i = 0; i < builders.size(); i++) {
                MappingContextFactoryBuilder<S, K> builder = builders.get(i);

                populateKey(keyDefinitionsBuilder, builders, builder, rootDetector);
            }

            KeyDefinition<S, K>[] keyDefinitions = KeyDefinitionBuilder.<S, K>toKeyDefinitions(keyDefinitionsBuilder);

            context = new BreakDetectorMappingContextFactory<S>(keyDefinitions[rootDetector], keyDefinitions, context);
        }

        return context;
    }

    private KeyDefinitionBuilder<S, K> populateKey(KeyDefinitionBuilder<S, K>[] keyDefinitions, ArrayList<MappingContextFactoryBuilder<S, K>> builders, MappingContextFactoryBuilder<S, K> builder, int rootDetector) {

        if (keyDefinitions[builder.currentIndex] != null) {
            return keyDefinitions[builder.currentIndex];
        }

        int parentIndex = builder.getParentNonEmptyIndex();
        if (parentIndex == -1 && rootDetector != builder.currentIndex()) {
            parentIndex = rootDetector;
        }
        KeyDefinitionBuilder<S, K> parent = null;
        if (parentIndex != -1) {
            parent = keyDefinitions[parentIndex];
            if (parent == null) {
                // not yet define look for parent and create key
                for(int i = 0; i < builders.size(); i++) {
                    MappingContextFactoryBuilder<S, K> potentialParent = builders.get(i);
                    if (potentialParent.currentIndex == parentIndex) {
                        parent = populateKey(keyDefinitions, builders, potentialParent, rootDetector);
                        break;
                    }
                }
                if (parent == null) {
                    throw new IllegalArgumentException("Could not find parent for builder " + builder);
                }
            }
        }



        KeyDefinitionBuilder<S, K> keyDefinition;

        // empty key use parent key except for child of appendsetter
        if (builder.keys.isEmpty() && parent != null && ! builder.newObjectOnEachRow()) {
             keyDefinition = parent.asChild(builder.currentIndex);
        } else {
            List<K> keys = new ArrayList<K>(builder.keys);

            // append parent keys except for root, as the root will clear all the cache
            // also if keys is empty we generate a new row every time so leave empty
            if (parent != null && !builder.keys.isEmpty() && ! parent.isRoot()) {
                keys.addAll(parent.getKeys());
            }

            keyDefinition = new KeyDefinitionBuilder<S, K>(keys, builder.keySourceGetter, parent, builder.currentIndex, builder.currentIndex == rootDetector);
        }

        keyDefinitions[builder.currentIndex] = keyDefinition;
        return keyDefinition;
    }

    private boolean newObjectOnEachRow() {
        if (owner instanceof ArrayElementPropertyMeta) {
            ArrayElementPropertyMeta elementPropertyMeta = (ArrayElementPropertyMeta) owner;
            if (elementPropertyMeta.getSetter() instanceof AppendCollectionSetter) {
                return true;
            }
        }
        return false;
    }

    private boolean hasKeys(ArrayList<MappingContextFactoryBuilder<S, K>> builders) {
        for(int i = 0; i < builders.size(); i++) {
            if (!builders.get(i).hasNoKeys()) return true;
        }
        return false;
    }

    private int getRootDetector(List<MappingContextFactoryBuilder<S, K>> builders) {
        int rootDetector = -1;
        // calculate rootDetector
        for (int i = 0; i < builders.size(); i++) {
            final MappingContextFactoryBuilder<S, K> builder = builders.get(i);
            if (!builder.keys.isEmpty()) {
                if (builder.currentIndex == 0 || (rootDetector == -1 && builder.isEligibleAsRootKey())) {
                    rootDetector = builder.currentIndex;
                }
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


    private void addAllBuilders(ArrayList<MappingContextFactoryBuilder<S, K>> builders) {
        builders.add(this);
        for(MappingContextFactoryBuilder<S, K> child : children) {
            child.addAllBuilders(builders);
        }
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
