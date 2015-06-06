package org.sfm.map;


import org.sfm.jdbc.impl.BreakDetector;
import org.sfm.reflect.Getter;
import org.sfm.reflect.meta.ListElementPropertyMeta;
import org.sfm.reflect.meta.MapElementPropertyMeta;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.BooleanProvider;
import org.sfm.utils.ErrorHelper;
import org.sfm.utils.Predicate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
                return new RootGetterProvider<S>();
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
        KeysDefinition<S, K>[] breakDetectors = new KeysDefinition[builders.get(builders.size() -1).currentIndex + 1];

        int rootDetector = -1;
        for(int i = 0; i < builders.size(); i++) {
            final MappingContextFactoryBuilder<S, K> builder = builders.get(i);

            final KeysDefinition<S, K> detector = builder.newKeysDefinition(builder.getParentNonEmptyIndex());
            breakDetectors[builder.currentIndex] = detector;
            if (builder.currentIndex == 0 || (rootDetector == -1 && builder.isRootEligible())) {
                rootDetector = builder.currentIndex;
            }
        }

        return new MappingContextFactoryImpl<S, K>(breakDetectors, rootDetector);
    }

    private boolean isRootEligible() {
        return !(owner instanceof ListElementPropertyMeta)
                && !(owner instanceof MapElementPropertyMeta)
                && (parent == null || parent.isRootEligible());
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

    private static class BreakGetter<S> implements Getter<MappingContext<S>, BooleanProvider> {
        private final int index;
        private BreakGetter(int index) {
            this.index = index;
        }

        @Override
        public BooleanProvider get(MappingContext<S> target) throws Exception {
            return new MappingContextBooleanProvider<S>(target, index);
        }
    }

    private static class RootGetterProvider<S> implements Getter<MappingContext<S>, BooleanProvider> {

        @Override
        public BooleanProvider get(MappingContext<S> target) throws Exception {
            return new RootBooleanProvider<S>(target);
        }
    }

    private static class RootBooleanProvider<S> implements BooleanProvider {
        private final MappingContext<S> target;

        public RootBooleanProvider(MappingContext<S> target) {
            this.target = target;
        }

        @Override
        public boolean getBoolean() {
            return target == null || target.rootBroke();
        }
    }
    private static class MappingContextBooleanProvider<S> implements BooleanProvider {
        private final MappingContext<S> target;
        private final int index;

        public MappingContextBooleanProvider(MappingContext<S> target, int index) {
            this.target = target;
            this.index = index;
        }

        @Override
        public boolean getBoolean() {
            return target == null || target.broke(index);
        }
    }

    private static class NullChecker<S, K> implements Predicate<S> {

        private final List<K> keys;
        private final KeySourceGetter<K, S> keySourceGetter;

        private NullChecker(List<K> keys, KeySourceGetter<K, S> keySourceGetter) {
            this.keys = keys;
            this.keySourceGetter = keySourceGetter;
        }

        @Override
        public boolean test(S s) {
            try {
                if (keys.isEmpty()) return false;
                for (int i = 0; i < keys.size(); i++) {
                    if (keySourceGetter.getValue(keys.get(i), s) != null) {
                        return false;
                    }
                }
                return true;
            } catch(Exception e) {
                ErrorHelper.rethrow(e);
                throw new IllegalStateException();
            }
        }
    }

    public interface KeySourceGetter<K, S> {
        Object getValue(K key, S source) throws SQLException;
    }

    private static class MappingContextFactoryImpl<S, K> implements MappingContextFactory<S> {
        private final KeysDefinition<S, K>[] breakDetectors;
        private final int rootDetector;
        public MappingContextFactoryImpl(KeysDefinition<S, K>[] breakDetectors, int rootDetector) {
            this.breakDetectors = breakDetectors;
            this.rootDetector = rootDetector;
        }

        @Override
        public MappingContext<S> newContext() {
            return new MappingContext<S>(newBreakDetectors(breakDetectors), rootDetector);
        }

        private BreakDetector<S>[] newBreakDetectors(KeysDefinition<S, K>[] definitions) {
            if (definitions == null) return null;

            BreakDetector<S>[] breakDetectors = new BreakDetector[definitions.length];

            for(int i = 0; i < definitions.length; i++) {
                KeysDefinition<S, K> definition = definitions[i];
                if (definition != null) {
                    breakDetectors[i] = newBreakDetector(definition, definition.getParentIndex() != -1 ? breakDetectors[definition.getParentIndex()] : null);
                }
            }


            return breakDetectors;
        }

        private BreakDetector<S> newBreakDetector(KeysDefinition<S, K> definition, BreakDetector<S> parent) {
            return new BreakDetectorImpl<S, K>(definition, parent);
        }
    }

    private static class KeysDefinition<S, K> {
        private final KeySourceGetter<K, S> keySourceGetter;
        private final List<K> keys;
        private final int parentIndex;

        public KeysDefinition(List<K> keys, KeySourceGetter<K, S> keySourceGetter, int parentIndex) {
            this.keys = keys;
            this.keySourceGetter = keySourceGetter;
            this.parentIndex = parentIndex;
        }

        public boolean isEmpty() {
            return keys.isEmpty();
        }

        public Object[] getValues(S source) {
            try {
                Object[] values = new Object[keys.size()];
                for (int i = 0; i < values.length; i++) {
                    values[i] = keySourceGetter.getValue(keys.get(i), source);
                }
                return values;
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }

        public int getParentIndex() {
            return parentIndex;
        }
    }

    private static class BreakDetectorImpl<S, K> implements BreakDetector<S> {

        private final KeysDefinition<S, K> definition;
        private final BreakDetector<S> parent;
        private Object[] lastValues;

        private boolean isBroken = true;


        public BreakDetectorImpl(KeysDefinition<S, K> definition, BreakDetector<S> parent) {
            this.definition = definition;
            this.parent = parent;
        }

        @Override
        public void handle(S source) throws MappingException {
            if (definition.isEmpty()) {
                return;
            }

            Object[] newValues = definition.getValues(source);

            isBroken = (parent != null && parent.isBroken())
                    || lastValues == null
                    || !Arrays.equals(lastValues, newValues);

            lastValues = newValues;
        }

        @Override
        public boolean isBroken() {
            return isBroken;
        }

        @Override
        public void markAsBroken() {
            isBroken = true;
            lastValues = null;
        }
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
