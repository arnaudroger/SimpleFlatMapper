package org.sfm.jdbc.impl;

import org.sfm.map.MappingException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

public class ResultSetBreakDetector implements BreakDetector<ResultSet> {


    private final int[] columns;

    private Object[] lastKey;
    private boolean isBroken;

    public ResultSetBreakDetector(int[] columns) {
        this.columns = columns;
    }

    @Override
    public void handle(ResultSet source) throws MappingException {
        Object[] newKeys = new Object[columns.length];

        for(int i = 0; i < columns.length; i++) {
            try {
                newKeys[i] = source.getObject(columns[i]);
            } catch (SQLException e) {
                throw new MappingException(e.getMessage(), e);
            }
        }

        isBroken = lastKey != null && !Arrays.equals(lastKey, newKeys);

        lastKey = newKeys;
    }

    @Override
    public boolean isBroken() {
        return isBroken;
    }
}
