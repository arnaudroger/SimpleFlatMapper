package org.simpleflatmapper.csv.mapper;

import org.simpleflatmapper.csv.CsvRow;
import org.simpleflatmapper.csv.CsvRowSet;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.UnaryFactory;


public class CsvRowEnumerableFactory implements UnaryFactory<CsvRowSet, Enumerable<CsvRow>> {
    @Override
    public Enumerable<CsvRow> newInstance(CsvRowSet rowSet) {
        return rowSet;
    }
}
