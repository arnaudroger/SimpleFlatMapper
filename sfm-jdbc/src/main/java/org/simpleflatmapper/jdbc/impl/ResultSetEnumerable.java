package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.ErrorHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetEnumerable implements Enumerable<ResultSet> {
    private final ResultSet resultSet;

    public ResultSetEnumerable(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public boolean next() {
        try {
            return resultSet.next();
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
