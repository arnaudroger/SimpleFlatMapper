package org.simpleflatmapper.jdbc.impl;

import javax.sql.DataSource;
import java.sql.SQLException;

public class TransactionFactory {

    private final DataSource dataSource;

    public TransactionFactory(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    public Transaction newTransaction() throws SQLException {
        return new Transaction(dataSource.getConnection());
    }

}
