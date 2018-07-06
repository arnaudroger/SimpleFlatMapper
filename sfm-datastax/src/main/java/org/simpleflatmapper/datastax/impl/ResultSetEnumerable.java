package org.simpleflatmapper.datastax.impl;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import org.simpleflatmapper.util.Enumerable;


public class ResultSetEnumerable implements Enumerable<Row> {
    private final ResultSet resultSet;
    private Row currentRow;

    public ResultSetEnumerable(ResultSet resultSet) {
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
