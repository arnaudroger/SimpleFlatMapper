package org.simpleflatmapper.reflect.meta;

public class CountNumberOfMatchedProperties<T> implements PropertyFinder.FoundProperty<T> {


    public int nbFound =  0;
    public final PropertyFinder.FoundProperty<T> delegate;

    public CountNumberOfMatchedProperties(PropertyFinder.FoundProperty<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public <P extends PropertyMeta<T, ?>> void found(P propertyMeta, Runnable selectionCallback, PropertyMatchingScore score, PropertyFinder.TypeAffinityScorer typeAffinityScorer) {
        if (!propertyMeta.isNonMapped()) {
            nbFound++;
        }
        delegate.found(propertyMeta, selectionCallback, score, typeAffinityScorer);
    }
}
