package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by aroger on 08/11/2016.
 */
public interface SelectQuery<T, P> {
    T readFirst(Connection connection, P p) throws SQLException;

    <C extends CheckedConsumer<? super T>> C read(Connection connection, P p, C consumer) throws SQLException;
}
