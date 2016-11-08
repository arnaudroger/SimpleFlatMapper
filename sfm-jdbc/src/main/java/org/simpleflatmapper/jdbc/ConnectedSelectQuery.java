package org.simpleflatmapper.jdbc;

import org.simpleflatmapper.jdbc.impl.Transaction;
import org.simpleflatmapper.jdbc.impl.TransactionFactory;
import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.SQLException;

public class ConnectedSelectQuery<T, P> {

    private final SelectQuery<T, P> delegate;
    private final TransactionFactory txFactory;

    public ConnectedSelectQuery(SelectQuery<T, P> delegate, TransactionFactory txFactory) {
        this.delegate = delegate;
        this.txFactory = txFactory;
    }

    public T readFirst(P p) throws SQLException {
        T t = null;
        Transaction tx = txFactory.newTransaction();
        try {
            t = delegate.readFirst(tx.connection(), p);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
        return t;
    }

    public <C extends CheckedConsumer<? super T>> C read(P p, C consumer) throws SQLException {
        Transaction tx = txFactory.newTransaction();
        try {
            delegate.read(tx.connection(), p, consumer);
            tx.commit();
        } catch (Throwable e) {
            tx.handleError(e);
        } finally {
            tx.close();
        }
        return consumer;
    }

}
