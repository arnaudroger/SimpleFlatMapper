package org.sfm.map.context;

import java.sql.SQLException;

public interface KeySourceGetter<K, S> {
    Object getValue(K key, S source) throws SQLException;
}
