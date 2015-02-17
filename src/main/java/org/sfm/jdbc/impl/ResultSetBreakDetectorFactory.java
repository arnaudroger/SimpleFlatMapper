package org.sfm.jdbc.impl;

import org.sfm.jdbc.BreakDetectorFactory;

import java.sql.ResultSet;


public class ResultSetBreakDetectorFactory implements BreakDetectorFactory<ResultSet> {
    private final int[] columns;

    public ResultSetBreakDetectorFactory(int[] columns) {
        this.columns = columns;
    }

    @Override
    public BreakDetector<ResultSet> newInstance() {
        return new ResultSetBreakDetector(columns);
    }
}
