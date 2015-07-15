package org.sfm.map.impl.context;

import org.sfm.jdbc.impl.BreakDetector;
import org.sfm.map.MappingContext;
import org.sfm.map.MappingContextFactory;

public class BreakDetectorMappingContextFactory<S, K> implements MappingContextFactory<S> {
    private final KeysDefinition<S, K>[] keyDefinitions;
    private final int rootDetector;

    public BreakDetectorMappingContextFactory(KeysDefinition<S, K>[] keyDefinitions, int rootDetector) {
        this.keyDefinitions = keyDefinitions;
        this.rootDetector = rootDetector;
    }

    @Override
    public MappingContext<S> newContext() {
        return new BreakDetectorMappingContext<S>(newBreakDetectors(keyDefinitions), rootDetector);
    }

    private BreakDetector<S>[] newBreakDetectors(KeysDefinition<S, K>[] definitions) {
        if (definitions == null) return null;

        BreakDetector<S>[] breakDetectors = new BreakDetector[definitions.length];

        for (int i = 0; i < definitions.length; i++) {
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
