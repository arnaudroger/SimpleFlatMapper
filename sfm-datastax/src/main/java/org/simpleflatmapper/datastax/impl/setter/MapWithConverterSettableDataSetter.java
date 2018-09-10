package org.simpleflatmapper.datastax.impl.setter;

import com.datastax.driver.core.SettableByIndexData;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.converter.Converter;

import java.util.HashMap;
import java.util.Map;

public class MapWithConverterSettableDataSetter<KI, KO, VI, VO> implements Setter<SettableByIndexData<?>, Map<KI, VI>> {
    private final int index;
    private final Converter<KI, KO> keyConverter;
    private final Converter<VI, VO> valueConverter;

    public MapWithConverterSettableDataSetter(int index, Converter<KI, KO> keyConverter, Converter<VI, VO> valueConverter) {
        this.index = index;
        this.keyConverter = keyConverter;
        this.valueConverter = valueConverter;
    }

    @Override
    public void set(SettableByIndexData<?> target, Map<KI, VI> value) throws Exception {
        if (value == null) {
            target.setToNull(index);
        } else {
            Map<KO, VO> map = new HashMap<KO, VO>();

            for(Map.Entry<KI, VI> e : value.entrySet()) {
                map.put(keyConverter.convert(e.getKey(), null),
                        valueConverter.convert(e.getValue(), null));
            }

            target.setMap(index, map);
        }
    }
}
