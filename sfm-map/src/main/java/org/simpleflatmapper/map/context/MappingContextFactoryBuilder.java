package org.simpleflatmapper.map.context;


import org.simpleflatmapper.converter.ContextFactoryBuilder;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.impl.KeyDefinitionBuilder;
import org.simpleflatmapper.map.impl.JoinUtils;
import org.simpleflatmapper.reflect.meta.ArrayElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.MapKeyValueElementPropertyMeta;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.map.context.impl.BreakDetectorMappingContextFactory;
import org.simpleflatmapper.map.context.impl.NullChecker;
import org.simpleflatmapper.map.context.impl.ValuedMappingContextFactory;
import org.simpleflatmapper.reflect.setter.AppendCollectionSetter;
import org.simpleflatmapper.util.Predicate;
import org.simpleflatmapper.util.Supplier;

import java.util.ArrayList;
import java.util.List;

public class MappingContextFactoryBuilder<S, K> implements ContextFactoryBuilder {

    private final Counter counter;
    private final int currentIndex;
    private final MappingContextFactoryBuilder<S, K> parent;
    private final List<KeyAndPredicate<S, K>> keys;
    private final List<KeyAndPredicate<S, K>> inferNullColumns;
    private final KeySourceGetter<K, ? super S> keySourceGetter;
    private final List<MappingContextFactoryBuilder<S, K>> children = new ArrayList<MappingContextFactoryBuilder<S, K>>();
    private final List<Supplier<?>> suppliers = new ArrayList<Supplier<?>>();
    private final PropertyMeta<?, ?> owner;
    private final boolean ignoreRootKey;

    public MappingContextFactoryBuilder(KeySourceGetter<K, ? super S> keySourceGetter, boolean ignoreRootKey) {
        this(new Counter(), new ArrayList<KeyAndPredicate<S, K>>(), new ArrayList<KeyAndPredicate<S, K>>(), keySourceGetter, null, null, ignoreRootKey);
    }

    protected MappingContextFactoryBuilder(Counter counter, List<KeyAndPredicate<S, K>> keys, List<KeyAndPredicate<S, K>> inferNullColumns, KeySourceGetter<K, ? super S> keySourceGetter, MappingContextFactoryBuilder<S, K> parent, PropertyMeta<?, ?> owner, boolean ignoreRootKey) {
        this.counter = counter;
        this.currentIndex = counter.value;
        this.keys = keys;
        this.inferNullColumns = inferNullColumns;
        this.keySourceGetter = keySourceGetter;
        this.parent = parent;
        this.ignoreRootKey = ignoreRootKey;
        this.counter.value++;
        this.owner = owner;
    }


    public void addKey(KeyAndPredicate<S, K> keyAndPredicate) {
        addKeyTo(keyAndPredicate, keys);
        addKeyTo(keyAndPredicate, inferNullColumns);
    }

    public void addInferNull(KeyAndPredicate<S, K> keyAndPredicate) {
        addKeyTo(keyAndPredicate, inferNullColumns);
    }


    private void addKeyTo(KeyAndPredicate<S, K> keyAndPredicate, List<KeyAndPredicate<S, K>> keyAndPredicates) {
        for(int i = 0; i < keyAndPredicates.size(); i++) {
            KeyAndPredicate<S, K> kp = keyAndPredicates.get(i);
            if (kp.key.equals(keyAndPredicate.key)) {
                keyAndPredicates.set(i, kp.mergeWith(keyAndPredicate));
                return;
            }
        }
        keyAndPredicates.add(keyAndPredicate);
    }

    @Override
    public int addSupplier(Supplier<?> supplier) {
        if (parent == null) {
            int index = suppliers.size();
            suppliers.add(index, supplier);
            return index;
        } else {
            return parent.addSupplier(supplier);
        }
    }

    public Predicate<S> nullChecker() {
        return new NullChecker<S, K>(inferNullColumns, keySourceGetter);
    }

    public MappingContextFactoryBuilder<S, K> newBuilder(List<KeyAndPredicate<S, K>> subKeys, List<KeyAndPredicate<S, K>> inferNullColumns, PropertyMeta<?, ?> owner) {
        // look for duplicate 
        for(MappingContextFactoryBuilder<S, K> child : children) {
            if ((child.owner.getPath().equals(owner.getPath())
                        && child.owner.getPropertyClassMeta().equals(owner.getPropertyClassMeta()))
            ) {
                return child;
            }
        }
        
        MappingContextFactoryBuilder<S, K> subBuilder = new MappingContextFactoryBuilder<S, K>(counter, subKeys, inferNullColumns, keySourceGetter, this, owner, ignoreRootKey);
        children.add(subBuilder);
        return subBuilder;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MappingContextFactory<S> build() {
        if (parent != null)  {
            throw new IllegalStateException();
        }


        MappingContextFactory<S> context;

        ArrayList<MappingContextFactoryBuilder<S, K>> builders = new ArrayList<MappingContextFactoryBuilder<S, K>>();
        addAllBuilders(builders);
        
        if (suppliers.isEmpty()) {
            context = MappingContext.EMPTY_FACTORY;
        } else {
            context = new ValuedMappingContextFactory<S>(suppliers);
        }

        if (hasKeys(builders)) {
            KeyDefinitionBuilder<S, K>[] keyDefinitionsBuilder = new KeyDefinitionBuilder[builders.get(builders.size() - 1).currentIndex + 1];

            for (int i = 0; i < builders.size(); i++) {
                MappingContextFactoryBuilder<S, K> builder = builders.get(i);

                populateKey(keyDefinitionsBuilder, builders, builder);
            }

            KeyDefinition<S, K>[] keyDefinitions = KeyDefinitionBuilder.<S, K>toKeyDefinitions(keyDefinitionsBuilder);
            KeyDefinition<S, K> rootKeyDefinition = keyDefinitions[0];

            context = new BreakDetectorMappingContextFactory<S>(rootKeyDefinition, keyDefinitions, context);
        }

        return context;
    }



    private KeyDefinitionBuilder<S, K> populateKey(KeyDefinitionBuilder<S, K>[] keyDefinitions, ArrayList<MappingContextFactoryBuilder<S, K>> builders, MappingContextFactoryBuilder<S, K> builder) {

        if (keyDefinitions[builder.currentIndex] != null) {
            return keyDefinitions[builder.currentIndex];
        }

        int parentIndex = builder.getNonEmptyParentIndex();

        KeyDefinitionBuilder<S, K> parent = null;
        if (parentIndex != -1) {
            parent = keyDefinitions[parentIndex];
            if (parent == null) {
                // not yet define look for parent and create key
                for(int i = 0; i < builders.size(); i++) {
                    MappingContextFactoryBuilder<S, K> potentialParent = builders.get(i);
                    if (potentialParent.currentIndex == parentIndex) {
                        parent = populateKey(keyDefinitions, builders, potentialParent);
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
        if (parent != null && builder.inheritKeys(parentIndex)) {
             keyDefinition = parent.asChild(builder.currentIndex);
        } else {
            List<KeyAndPredicate<S, K>> keys = new ArrayList<KeyAndPredicate<S, K>>(builder.effectiveKeys());

            // ignore root parent
            if (parent != null && (parentIndex >0 || !ignoreRootKey)) {
                appendParentKeys(parent, keys);
            }
            
            keyDefinition = new KeyDefinitionBuilder<S, K>(keys, builder.keySourceGetter, builder.currentIndex);
        }

        keyDefinitions[builder.currentIndex] = keyDefinition;
        return keyDefinition;
    }

    private boolean inheritKeys(int parentIndex) {
        return (effectiveKeys().isEmpty() && ! newObjectOnEachRow(parentIndex));
    }

    private void appendParentKeys(KeyDefinitionBuilder<S, K> parent, List<KeyAndPredicate<S, K>> keys) {
        // if keys is empty we generate a new row every time so leave empty
        if (!keys.isEmpty()) {
            for(KeyAndPredicate<S, K> k : parent.getKeyAndPredicates()) {
                addKeyTo(k, keys);
            }
        }
    }

    private List<KeyAndPredicate<S, K>> effectiveKeys() {

        if (!keys.isEmpty()) {
            return keys;
        }

        List<KeyAndPredicate<S, K>> keys = new ArrayList<KeyAndPredicate<S, K>>();
        for(MappingContextFactoryBuilder<S, K> child : children) {
            if (child.isEligibleAsSubstituteKey()) {
                keys.addAll(child.effectiveKeys());
            }

        }
        return keys;
    }


    private boolean newObjectOnEachRow(int parentIndex) {
        if (owner instanceof ArrayElementPropertyMeta) {
            ArrayElementPropertyMeta elementPropertyMeta = (ArrayElementPropertyMeta) owner;
            if (elementPropertyMeta.getSetter() instanceof AppendCollectionSetter) {
                return true;
            }
        } else if (owner instanceof MapKeyValueElementPropertyMeta) {
            return true;
        }
        
        if (parent != null && parent.currentIndex != parentIndex ) {
            return parent.newObjectOnEachRow(parentIndex);
        }
        
        return false;
    }

    private static <S, K> boolean hasKeys(ArrayList<MappingContextFactoryBuilder<S, K>> builders) {
        for(int i = 0; i < builders.size(); i++) {
            if (!builders.get(i).hasNoKeys()) return true;
        }
        return false;
    }

    private boolean isEligibleAsSubstituteKey() {
        return !JoinUtils.isArrayElement(owner);
    }

    // ignore empty parent useful to skip root keys
    private int getNonEmptyParentIndex() {
        return parent == null
                ? -1
                : parent.effectiveKeys().isEmpty() ? parent.getNonEmptyParentIndex() : parent.currentIndex;
    }


    private void addAllBuilders(ArrayList<MappingContextFactoryBuilder<S, K>> builders) {
        builders.add(this);
        for(MappingContextFactoryBuilder<S, K> child : children) {
            child.addAllBuilders(builders);
        }
    }

    public boolean hasNoKeys() {
        return effectiveKeys().isEmpty();
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

    public boolean hasChildren() {
        return children.isEmpty();
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
