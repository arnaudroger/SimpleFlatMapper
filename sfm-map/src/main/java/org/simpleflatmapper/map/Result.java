package org.simpleflatmapper.map;

import org.simpleflatmapper.reflect.ReflectionService;

import java.util.ArrayList;
import java.util.List;

@ReflectionService.PassThrough
public class Result<T, K> {
    private T value;
    private final List<FieldError<K>> errors = new ArrayList<FieldError<K>>();

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public List<FieldError<K>> getErrors() {
        return errors;
    }
    
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @Override
    public String toString() {
        return "Result{" +
                "value=" + value +
                ", errors=" + errors +
                '}';
    }

    public static class FieldError<K> {
        private final K key;
        private final Throwable error;

        public FieldError(K key, Throwable error) {
            this.key = key;
            this.error = error;
        }

        public K getKey() {
            return key;
        }

        public Throwable getError() {
            return error;
        }

        @Override
        public String toString() {
            return "FieldError{" +
                    "key=" + key +
                    ", error=" + error +
                    '}';
        }
    }
}
