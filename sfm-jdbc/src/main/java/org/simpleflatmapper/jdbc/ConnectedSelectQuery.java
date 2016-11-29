package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectedSelectQuery<T, P> {

    private final SelectQuery<T, P> delegate;
    private final TransactionTemplate txFactory;

    public ConnectedSelectQuery(SelectQuery<T, P> delegate, TransactionTemplate txFactory) {
        this.delegate = delegate;
        this.txFactory = txFactory;
    }

    public T readFirst(final P p) throws SQLException {
        return txFactory.doInTransaction(
                new SQLFunction<Connection, T>() {
                    @Override
                    public T apply(Connection connection) throws SQLException {
                        return delegate.readFirst(connection, p);
                    }
                }
        );
    }

    public <C extends CheckedConsumer<? super T>> C read(final P p, final C consumer) throws SQLException {
        txFactory.doInTransaction(new SQLFunction<Connection, Object>() {
            @Override
            public Object apply(Connection connection) throws SQLException {
                delegate.read(connection, p, consumer);
                return null;
            }
        });
        return consumer;
    }

}
