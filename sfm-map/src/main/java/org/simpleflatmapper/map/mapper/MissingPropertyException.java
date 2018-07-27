package org.simpleflatmapper.map.mapper;

import java.util.List;

public class MissingPropertyException extends RuntimeException {
    private final List<String> missingProperties;

    public MissingPropertyException(List<String> missingProperties) {
        super("The following mandatory properties were not present: " + missingProperties);
        this.missingProperties = missingProperties;
    }

    public List<String> getMissingProperties() {
        return missingProperties;
    }
}
