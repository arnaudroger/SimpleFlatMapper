package org.simpleflatmapper.lightningcsv;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Row implements Map<String, String> {

    public static final int SORTED_HEADERS_THRESHOLD = 10;
    private final Headers headers;
    private final String[] values;
    private Set<Entry<String, String>> entrySetCache;
    private Collection<String> valuesCollectionCache;

    public Row(Headers headers, String[] values) {
        this.headers = headers;
        this.values = values;
    }

    @Override
    public int size() {
        return headers.size();
    }

    @Override
    public boolean isEmpty() {
        return headers.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return headers.containsKey((String) key);
        } 
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for(int i = 0; i < values.length; i++) {
            if (value == null ? values[i] == null : value.equals(values[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof String)) {
            return null;
        }

        int i = headers.indexOf((String)key);
        
        if (i != -1) {
            return values[i];
        } 
        return null;
    }

    @Override
    public String put(String key, String value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return headers.keySet();
    }

    @Override
    public Collection<String> values() {
        if (valuesCollectionCache == null) {
            valuesCollectionCache = Collections.unmodifiableList(Arrays.asList(values));
        }
        return valuesCollectionCache;
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        if (entrySetCache == null) {
            HashSet<Entry<String, String>> set = new HashSet<Entry<String, String>>();
            for(int i = 0; i < headers.headers.length; i++) {
                set.add(new AbstractMap.SimpleImmutableEntry<String, String>(headers.headers[i], values[i]));
            }
            entrySetCache = Collections.unmodifiableSet(set);
        }
        return entrySetCache;
    }
    
    
    public static Headers headers(String[] headers) {
        if (headers.length > SORTED_HEADERS_THRESHOLD) {
            return new SortedHeaders(headers);
        }
        return new DefaultHeaders(headers);
    }
    
    static abstract class Headers {
        protected final String[] headers;
        private Set<String> keySet;

        protected Headers(String[] headers) {
            this.headers = headers;
        }

        public final boolean containsKey(String key) {
            return indexOf(key) != -1;
        }

        public final int size() {
            return headers.length;
        }

        public final boolean isEmpty() {
            return headers.length == 0;
        }
        
        public final Set<String> keySet() {
            if (keySet == null) {
                keySet = new HashSet<String>();
                Collections.addAll(keySet, headers);
            }
            return keySet;
        }
        
        public abstract int indexOf(String key);

    }
    
    public static class DefaultHeaders extends Headers {

        protected DefaultHeaders(String[] headers) {
            super(headers);
        }

        @Override
        public final int indexOf(String key) {
            for(int i = 0; i < headers.length; i++) {
                if (Row.equals(key, headers[i])) {
                    return i;
                }
            }
            return -1;
        }
    }
    
    public static class SortedHeaders extends Headers {
        private final String[] sortedHeader;
        private final int[] sortedHeaderIndex;
        
        protected SortedHeaders(String[] headers) {
            super(headers);
            sortedHeader = new String[headers.length];
            sortedHeaderIndex = new int[headers.length];

            IndexedHeader[] indexedHeaders = new IndexedHeader[headers.length];
            for(int i = 0; i < indexedHeaders.length; i++) {
                indexedHeaders[i] = new IndexedHeader(headers[i], i);
            }
            Arrays.sort(indexedHeaders, IndexedHeader.NAME_COMPARATOR);
            
            for(int i = 0; i < indexedHeaders.length; i++) {
                IndexedHeader ih = indexedHeaders[i];
                sortedHeader[i] = ih.name;
                sortedHeaderIndex[i] = ih.index;
            }
        }

        @Override
        public final int indexOf(String key) {
            int i = Arrays.binarySearch(sortedHeader, key);
            if (i < 0) return -1;
            return sortedHeaderIndex[i];
        }
        
        private static class IndexedHeader {
            public static final Comparator<IndexedHeader> NAME_COMPARATOR = new Comparator<IndexedHeader>() {
                @Override
                public int compare(IndexedHeader o1, IndexedHeader o2) {
                    return o1.name.compareTo(o2.name);
                }
            };
            public final String name;
            public final int index;

            IndexedHeader(String name, int index) {
                this.name = name == null ? "" : name;
                this.index = index;
            }
        }
    }

    private static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }
}
