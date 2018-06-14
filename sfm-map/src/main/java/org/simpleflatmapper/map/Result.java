package org.simpleflatmapper.map;

import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.util.Function;

import java.util.ArrayList;
import java.util.List;

public class Result<T, K> {
    private final T value;
    private final List<FieldError<K>> errors;

    private Result(T value, List<FieldError<K>> errors) {
        this.value = value;
        this.errors = errors;
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
    
    public static <T, K> ResultBuilder<T, K> builder() {
        return new ResultBuilder<T, K>();
    }

    @ReflectionService.PassThrough
    public static class ResultBuilder<T, K> {
        private T value;
        private final List<FieldError<K>> errors = new ArrayList<FieldError<K>>();

        public ResultBuilder() {
        }
        
        public void setValue(T value) {
            this.value = value;
        }
        
        public void addError(FieldError<K> error) {
            errors.add(error);
        }

        public Result<T, K> build() {
            return new Result<T, K>(value, errors);
        }
    } 
    
    public static <T, K> Function<ResultBuilder<T, K>, Result<T, K>> buildingFunction() {
        return new Function<ResultBuilder<T, K>, Result<T, K>>() {
            @Override
            public Result<T, K> apply(ResultBuilder<T, K> tkResultBuilder) {
                return tkResultBuilder.build();
            }
        };
    }
}
