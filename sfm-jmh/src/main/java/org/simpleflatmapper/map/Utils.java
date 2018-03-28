package org.simpleflatmapper.map;

import org.simpleflatmapper.jdbc.JdbcColumnKey;
import org.simpleflatmapper.map.mapper.MapperKey;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Utils {

    private static final String[] columnSamples = new String[] {
            "id", "firstname", "lastname", "address", "zipcode", "country",
            "phone"
    };

    public static List<MapperKey<JdbcColumnKey>> generateKeys(int size, int maxColumns) {
        Random random = new Random(13265656556l);
        ArrayList<MapperKey<JdbcColumnKey>> list = new ArrayList<>(size);

        for(int i = 0; i < size; i++) {
            int nbColumn = random.nextInt(maxColumns) + 2;

            JdbcColumnKey[] columnKeys = new JdbcColumnKey[nbColumn];

            for(int j = 0; j < columnKeys.length; j++) {
                JdbcColumnKey key = new JdbcColumnKey(newName(random), j+1);
                columnKeys[j] = key;
            }

            list.add(new MapperKey<>(columnKeys));
        }


        return list;
    }

    private static String newName(Random random) {
        int i = random.nextInt(columnSamples.length  * 2);
        if (i >= columnSamples.length) {
            return "c" + Long.toHexString(i);
        }
        return columnSamples[i];
    }

    public static MapperKey<JdbcColumnKey> duplicateKey(MapperKey<JdbcColumnKey> key) {
        JdbcColumnKey[] newKeys = new JdbcColumnKey[key.getColumns().length];

        for(int i = 0; i < newKeys.length; i++) {
            newKeys[i] = duplicateKey(key.getColumns()[i]);
        }

        return new MapperKey<>(newKeys);
    }

    private static JdbcColumnKey duplicateKey(JdbcColumnKey jdbcColumnKey) {
        return new JdbcColumnKey(jdbcColumnKey.getName(), jdbcColumnKey.getIndex(), jdbcColumnKey.getSqlType(null), jdbcColumnKey.getParent());
    }
}
