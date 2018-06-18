package org.simpleflatmapper.csv.impl.asm;

import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.mapper.CellSetter;
import org.simpleflatmapper.csv.mapper.CsvMapperCellHandler;
import org.simpleflatmapper.csv.mapper.DelayedCellSetterFactory;
import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.reflect.Instantiator;

import java.lang.reflect.Type;
import java.util.Arrays;

public class CsvMapperKey {

    private final CsvColumnKey[] keys;
    private final Class<?>[] setters;
    private final Class<?>[] delayedSetters;
    private final Class<?> instantiator;
    private final Type target;
    private final Class<?> fieldErrorHandler;
    private final int maxMethodSize;



    public <T> CsvMapperKey(CsvColumnKey[] keys, CellSetter<T>[] setters, DelayedCellSetterFactory<T, ?>[] delayedCellSetterFactories, Instantiator<CsvMapperCellHandler<T>, T> instantiator, Type target, FieldMapperErrorHandler<? super CsvColumnKey> fieldErrorHandler, int maxMethodSize) {
        this.keys = keys;
        this.setters = getClassArray(setters);
        this.delayedSetters = getClassArray(delayedCellSetterFactories);
        this.instantiator = getClass(instantiator);
        this.target = target;
        this.fieldErrorHandler = getClass(fieldErrorHandler);
        this.maxMethodSize = maxMethodSize;

    }

    private Class<?>[] getClassArray(Object[] objects) {
        Class<?>[] classes = new Class[objects != null ? objects.length : 0];

        if(objects != null) {
            int i = 0;
            for(Object o : objects) {
                classes[i] = getClass(o);
                i++;
            }
        }

        return classes;
    }

    private Class<?> getClass(Object o) {
        if (o != null) return o.getClass();
        return null;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CsvMapperKey that = (CsvMapperKey) o;

        if (maxMethodSize != that.maxMethodSize) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(keys, that.keys)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(setters, that.setters)) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(delayedSetters, that.delayedSetters)) return false;
        if (instantiator != null ? !instantiator.equals(that.instantiator) : that.instantiator != null) return false;
        if (target != null ? !target.equals(that.target) : that.target != null) return false;
        return !(fieldErrorHandler != null ? !fieldErrorHandler.equals(that.fieldErrorHandler) : that.fieldErrorHandler != null);

    }

    @Override
    public int hashCode() {
        int result = keys != null ? Arrays.hashCode(keys) : 0;
        result = 31 * result + (setters != null ? Arrays.hashCode(setters) : 0);
        result = 31 * result + (delayedSetters != null ? Arrays.hashCode(delayedSetters) : 0);
        result = 31 * result + (instantiator != null ? instantiator.hashCode() : 0);
        result = 31 * result + (target != null ? target.hashCode() : 0);
        result = 31 * result + (fieldErrorHandler != null ? fieldErrorHandler.hashCode() : 0);
        result = 31 * result + maxMethodSize;
        return result;
    }
}
