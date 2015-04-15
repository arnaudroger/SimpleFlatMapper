package org.sfm.map.column;


import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldKey;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.Predicate;

import java.util.Arrays;

public abstract class ColumnDefinitionImpl<K extends FieldKey<K>, CD extends  ColumnDefinitionImpl<K, CD>>  extends ColumnDefinition<K, CD> {

    public static final Predicate<PropertyMeta<?, ?>> DEFAULT_APPLIES_TO = new Predicate<PropertyMeta<?, ?>>() {
        @Override
        public boolean test(PropertyMeta<?, ?> propertyMeta) {
            return false;
        }
    };
    private final ColumnProperty[] properties;

    protected ColumnDefinitionImpl(ColumnProperty[] properties) {
        if (properties == null) throw new NullPointerException();
        this.properties = properties;
    }


    public K rename(K key) {
        RenameProperty rp = lookFor(RenameProperty.class);
        if (rp != null) {
            return key.alias(rp.getName());
        }
        return key;
    }

    public boolean ignore() {
        return has(IgnoreProperty.class);
    }

    public boolean has(Class<? extends ColumnProperty> clazz) {
        return lookFor(clazz) != null;
    }

    public boolean isKey() {
        return has(KeyProperty.class);
    }

    public Predicate<PropertyMeta<?, ?>> keyAppliesTo() {
        KeyProperty kp = lookFor(KeyProperty.class);

        if (kp != null) {
            return kp.getAppliesTo();
        }

        return DEFAULT_APPLIES_TO;
    }

    @Override
    public CD compose(CD columnDefinition) {
        if (columnDefinition == null) throw new NullPointerException();
        ColumnDefinitionImpl cdi = columnDefinition;
        ColumnProperty[] properties = new ColumnProperty[this.properties.length + cdi.properties.length];
        System.arraycopy(cdi.properties, 0, properties, 0, cdi.properties.length);
        System.arraycopy(this.properties, 0, properties, cdi.properties.length, this.properties.length);
        return newColumnDefinition(properties);
    }

    public CD add(ColumnProperty property) {
        if (property == null) throw new NullPointerException();
        ColumnProperty[] properties = new ColumnProperty[this.properties.length + 1];
        System.arraycopy(this.properties, 0, properties, 0, this.properties.length);
        properties[this.properties.length] = property;
        return newColumnDefinition(properties);
    }

    @SuppressWarnings("unchecked")
    public <T> T lookFor(Class<T> propClass) {
        for(ColumnProperty cp : properties) {
            if (cp != null && propClass.equals(cp.getClass())) {
                return (T) cp;
            }
        }
        return null;
    }

    protected abstract CD newColumnDefinition(ColumnProperty[] properties);

    @Override
    public CD addRename(String name) {
        return add(new RenameProperty(name));
    }

    @Override
    public CD addIgnore() {
        return add(new IgnoreProperty());
    }

    @Override
    public CD addKey() {
        return add(new KeyProperty());
    }

    @Override
    public CD addKey(Predicate<PropertyMeta<?, ?>> appliesTo) {
        return add(new KeyProperty(appliesTo));
    }

    @Override
    protected void appendToStringBuilder(StringBuilder sb) {
        for (int i = 0; i < properties.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(properties[i].toString());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ColumnDefinitionImpl)) return false;

        ColumnDefinitionImpl<?, ?> that = (ColumnDefinitionImpl<?, ?>) o;

        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(properties, that.properties);

    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(properties);
    }
}
