package org.simpleflatmapper.jdbc;

import java.sql.SQLException;

public interface SQLFunction<P, R> {
    R apply(P p) throws SQLException;
}
