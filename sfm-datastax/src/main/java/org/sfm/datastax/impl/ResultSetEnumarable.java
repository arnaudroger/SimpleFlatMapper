package org.sfm.datastax.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.sfm.utils.Enumarable;
import org.sfm.utils.ErrorHelper;


public class ResultSetEnumarable implements Enumarable<Row> {
    private final ResultSet resultSet;
    private Row currentRow;

    public ResultSetEnumarable(ResultSet resultSet) {
        this.resultSet = resultSet;
    }

    @Override
    public boolean next() {
        if (resultSet.isExhausted()) return false;
        currentRow = resultSet.one();
        return true;
    }

    @Override
    public Row currentValue() {
        return currentRow;
    }
}
