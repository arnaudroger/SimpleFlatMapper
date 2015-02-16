package org.sfm.reflect.meta;

import org.sfm.utils.Predicate;

import java.util.ArrayList;
import java.util.List;

public class IndexedElement<T, E> {

    private final PropertyMeta<T, E> propertyMeta;
    private final ClassMeta<E> elementClassMeta;
    private final PropertyFinder<E> propertyFinder;
    private final List<String> assignedPath = new ArrayList<String>();


    public IndexedElement(PropertyMeta<T, E> propertyMeta, ClassMeta<E> elementClassMeta, Predicate<PropertyMeta<?, ?>> isJoinProperty) {
        this.propertyMeta = propertyMeta;
        this.elementClassMeta = elementClassMeta;
        if (elementClassMeta != null) {
            propertyFinder = elementClassMeta.newPropertyFinder(propertyMeta, isJoinProperty);
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

    public List<String> getAssignedPath() {
        return assignedPath;
    }

    public void addProperty(PropertyMeta<?, ?> s) {
        assignedPath.add(s.getPath());
    }

    public boolean hasProperty(PropertyMeta<?, ?> property) {
        return assignedPath.contains(property.getPath());
    }
}
