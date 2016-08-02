package org.simpleflatmapper.jdbc.impl;

import org.simpleflatmapper.core.utils.Enumarable;
import org.simpleflatmapper.core.utils.ErrorHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultSetEnumarable implements Enumarable<ResultSet> {
    private final ResultSet resultSet;

    public ResultSetEnumarable(ResultSet resultSet) {
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
