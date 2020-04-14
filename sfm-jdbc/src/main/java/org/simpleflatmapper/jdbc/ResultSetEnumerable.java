package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetEnumerable implements Enumerable<ResultSet> {
    private final ResultSet resultSet;
    private boolean exhausted = false;
    public ResultSetEnumerable(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public boolean next() {
        if (exhausted) {
            return false;
        }
        try {
            if (resultSet.next()) return true;
            else {
                exhausted = true;
                return false;
            }
        } catch(SQLException e) {
            ErrorHelper.rethrow(e);
            return false;
        }
    }

    @Override
    public ResultSet currentValue() {
        return resultSet;
    }
}
