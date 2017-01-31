package org.simpleflatmapper.map.context.impl;

import org.simpleflatmapper.map.BreakDetector;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.KeysDefinition;
import org.simpleflatmapper.map.context.MappingContextFactory;

import java.util.ArrayList;
import java.util.Arrays;

public class BreakDetectorMappingContextFactory<S, K> implements MappingContextFactory<S> {
    private final KeysDefinition<S, K>[] keyDefinitions;
    private final MappingContextFactory<S> delegateFactory;
    private final int rootDetector;
    private final int nbValues;
    private final int originalKeySize;

    public BreakDetectorMappingContextFactory(KeysDefinition<S, K>[] keyDefinitions,
                                              int rootDetector,
                                              MappingContextFactory<S> delegateFactory, int nbValues) {
        this.originalKeySize = keyDefinitions.length;
        this.keyDefinitions = reorder(keyDefinitions, rootDetector);
        this.rootDetector = rootDetector;
        this.delegateFactory = delegateFactory;
        this.nbValues = nbValues;
    }

    @SuppressWarnings("unchecked")
    private KeysDefinition<S, K>[] reorder(KeysDefinition<S, K>[] keyDefinitions, int rootDetector) {

        KeysDefinition<S, K>[] wKeyDefinitions = Arrays.copyOf(keyDefinitions, keyDefinitions.length);

        ArrayList<KeysDefinition<S, K>> newDefinitions = new ArrayList<KeysDefinition<S, K>>();

        if (rootDetector != -1) {
            newDefinitions.add(wKeyDefinitions[rootDetector]);
            wKeyDefinitions[rootDetector] = null;
            appendChildren(rootDetector, wKeyDefinitions, newDefinitions);
        }
        appendChildren(-1, wKeyDefinitions, newDefinitions);

        for(int i = 0; i < wKeyDefinitions.length; i++) {
            if (wKeyDefinitions[i] != null) {
                throw new IllegalStateException("Invalid State unconsumed keydefinition " + wKeyDefinitions[i]);
            }
        }

        return newDefinitions.toArray(new KeysDefinition[0]);

    }

    private void appendChildren(int parent, KeysDefinition<S, K>[] wKeyDefinitions, ArrayList<KeysDefinition<S, K>> newDefinitions) {
        for(int i = 0; i < wKeyDefinitions.length; i++) {
            KeysDefinition<S, K> definition = wKeyDefinitions[i];
            if (definition != null  && definition.getParentIndex() == parent) {
                newDefinitions.add(definition);
                wKeyDefinitions[i] = null;
                appendChildren(definition.getIndex(), wKeyDefinitions, newDefinitions);
            }
        }
    }

    @Override
    public MappingContext<S> newContext() {
        BreakDetector<S>[][] breakDetectors = newBreakDetectors(keyDefinitions);

        return new BreakDetectorMappingContext<S>(breakDetectors[1], breakDetectors[0], rootDetector, delegateFactory.newContext(), new Object[nbValues]);
    }

    @SuppressWarnings("unchecked")
    private BreakDetector<S>[][] newBreakDetectors(KeysDefinition<S, K>[] definitions) {
        if (definitions == null) return null;

        BreakDetector<S>[] processingOrderBreakDetectors = new BreakDetector[definitions.length];
        BreakDetector<S>[] originalOrderedBreakDetectors = new BreakDetector[originalKeySize];

        for (int i = 0; i < definitions.length; i++) {
            KeysDefinition<S, K> definition = definitions[i];

            BreakDetectorImpl<S, K> breakDetector = new BreakDetectorImpl<>(definition, originalOrderedBreakDetectors);

            processingOrderBreakDetectors[i] = breakDetector;
            originalOrderedBreakDetectors[definition.getIndex()] = breakDetector;
        }


        return new BreakDetector[][] { processingOrderBreakDetectors, originalOrderedBreakDetectors};
    }

}
