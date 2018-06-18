package org.simpleflatmapper.csv.property;

/**
 * Indicate that the column should be present for the row to consider having data.
 * The column needs to be present in the mapping - headers or manual mapper through builder.
 * The behavior is undefined when using joins.
 */
public final class MandatoryColumnProperty {
    public static final MandatoryColumnProperty INSTANCE = new MandatoryColumnProperty();
    
    private MandatoryColumnProperty() {
    }
}
