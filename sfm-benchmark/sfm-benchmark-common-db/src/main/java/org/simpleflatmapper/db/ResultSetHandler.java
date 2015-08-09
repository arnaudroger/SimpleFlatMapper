package org.simpleflatmapper.db;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface ResultSetHandler {
    void handle(ResultSet rs) throws Exception;
}
