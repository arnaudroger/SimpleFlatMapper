package org.simpleflatmapper.datastax.impl.getter;

import com.datastax.driver.core.GettableByIndexData;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.HashMap;
import java.util.Map;

public class DatastaxMapWithConverterGetter<K, V, KO, VO> implements Getter<GettableByIndexData, Map<KO, VO>> {

    private final int index;
    private final Class<K> keyType;
    private final Class<V> valueType;
    private final ContextualConverter<K, KO> keyConverter;
    private final ContextualConverter<V, VO> valueConverter;

    public DatastaxMapWithConverterGetter(int index, Class<K> keyType, Class<V> valueType, ContextualConverter<K, KO> keyConverter, ContextualConverter<V, VO> valueConverter) {
        this.index = index;
        this.keyType = keyType;
        this.valueType = valueType;
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public Map<KO, VO> get(GettableByIndexData target) throws Exception {
        Map<K, V> map = target.getMap(index, keyType, valueType);

        Map<KO, VO> outMap = new HashMap<KO, VO>();
        for(Map.Entry<K,V> e : map.entrySet()) {
            outMap.put(keyConverter.convert(e.getKey(), null), valueConverter.convert(e.getValue(), null));
        }
        return outMap;
    }
}
