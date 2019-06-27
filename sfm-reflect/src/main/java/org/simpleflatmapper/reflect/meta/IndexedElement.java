package org.simpleflatmapper.reflect.meta;


import java.util.ArrayList;
import java.util.List;

public class IndexedElement<T, E> {

    private final PropertyMeta<T, E> propertyMeta;
    private final ClassMeta<E> elementClassMeta;
    private final PropertyFinder<E> propertyFinder;
    private final List<String> assignedPath = new ArrayList<String>();

    public IndexedElement(PropertyMeta<T, E> propertyMeta, ClassMeta<E> elementClassMeta) {
        this.propertyMeta = propertyMeta;
        this.elementClassMeta = elementClassMeta;
        if (elementClassMeta != null) {
            propertyFinder = elementClassMeta.newPropertyFinder();
        } else {
            propertyFinder = null;
        }
    }

    public PropertyMeta<T, E> getPropertyMeta() {
        return propertyMeta;
    }

    public ClassMeta<E> getElementClassMeta() {
        return elementClassMeta;
    }

    public PropertyFinder<E> getPropertyFinder() {
        return propertyFinder;
    }

    public void addProperty(PropertyMeta<?, ?> s) {
        addProperty(s.getPath());
    }

    public void addProperty(String path) {
        assignedPath.add(path);
    }

    public boolean hasProperty(PropertyMeta<?, ?> property) {
        return hasProperty(property.getPath());
    }

    public boolean hasProperty(String path) {
        return assignedPath.contains(path);
    }

    public boolean hasAnyProperty() {
        return !assignedPath.isEmpty();
    }
}
