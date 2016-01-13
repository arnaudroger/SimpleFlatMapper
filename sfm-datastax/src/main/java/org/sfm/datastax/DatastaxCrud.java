package org.sfm.datastax;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.ResultSetFuture;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DatastaxCrud<T, K> {

    /**
     * can keep ref to prepared statement accross time
     * https://datastax.github.io/java-driver/features/statements/prepared/
     */
    private final PreparedStatement readQuery;
    private final PreparedStatement deleteQuery;
    private final PreparedStatement insertQuery;
    private final BoundStatementMapper<K> keySetter;
    private final BoundStatementMapper<T> insertSetter;
    private final DatastaxMapper<T> selectMapper;

    public DatastaxCrud(
            PreparedStatement insertQuery,
            PreparedStatement readQuery,
            PreparedStatement deleteQuery,
            BoundStatementMapper<T> insertSetter,
            BoundStatementMapper<K> keySetter,
            DatastaxMapper<T> selectMapper) {
        this.readQuery = readQuery;
        this.deleteQuery = deleteQuery;
        this.insertQuery = insertQuery;
        this.keySetter = keySetter;
        this.insertSetter = insertSetter;
        this.selectMapper = selectMapper;
    }

    public void save(Session session, T value) {
        saveAsync(session, value).getUninterruptibly();
    }

    public UninterruptibleFuture<Void> saveAsync(Session session, T value) {
        BoundStatement boundStatement = saveQuery(session, value);
        return new NoResultFuture(session.executeAsync(boundStatement));
    }

    public BoundStatement saveQuery(Session session, T value) {
        return insertSetter.mapTo(value, insertQuery.bind());
    }

    public T read(Session session, K key) {
        return readAsync(session, key).getUninterruptibly();
    }

    public UninterruptibleFuture<T> readAsync(Session session, K key) {
        BoundStatement boundStatement = keySetter.mapTo(key, readQuery.bind());
        return new OneResultFuture<T>(session.executeAsync(boundStatement), selectMapper);
    }

    public void delete(Session session, K key) {
        deleteAsync(session, key).getUninterruptibly();
    }

    public UninterruptibleFuture<Void> deleteAsync(Session session, K key) {
        BoundStatement boundStatement = deleteQuery(session, key);
        ResultSetFuture resultSetFuture = session.executeAsync(boundStatement);

        return new NoResultFuture(resultSetFuture);
    }

    public BoundStatement deleteQuery(Session session, K key) {
        return keySetter.mapTo(key, deleteQuery.bind());
    }

    private class OneResultFuture<T> implements UninterruptibleFuture<T> {
        private final ResultSetFuture resultSetFuture;
        private final DatastaxMapper<T> mapper;

        public OneResultFuture(ResultSetFuture resultSetFuture, DatastaxMapper<T> mapper) {
            this.resultSetFuture = resultSetFuture;
            this.mapper = mapper;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return resultSetFuture.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return resultSetFuture.isCancelled();
        }

        @Override
        public boolean isDone() {
            return resultSetFuture.isDone();
        }

        @Override
        public T get() throws InterruptedException, ExecutionException {
            ResultSet rs = resultSetFuture.get();
            return mapOneSelect(rs);
        }

        @Override
        public T getUninterruptibly() {
            ResultSet rs = resultSetFuture.getUninterruptibly();
            return mapOneSelect(rs);
        }

        @Override
        public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            ResultSet rs = resultSetFuture.get(timeout, unit);
            return mapOneSelect(rs);
        }

        private T mapOneSelect(ResultSet rs) {
            Row row = rs.one();
            if (row != null) {
                return mapper.map(row);
            }
            return null;
        }

        @Override
        public void addListener(Runnable listener, Executor executor) {
            resultSetFuture.addListener(listener, executor);
        }
    }

    private class NoResultFuture implements UninterruptibleFuture<Void> {
        private final ResultSetFuture resultSetFuture;

        public NoResultFuture(ResultSetFuture resultSetFuture) {
            this.resultSetFuture = resultSetFuture;
        }

        @Override
        public boolean cancel(boolean mayInterruptIfRunning) {
            return resultSetFuture.cancel(mayInterruptIfRunning);
        }

        @Override
        public boolean isCancelled() {
            return resultSetFuture.isCancelled();
        }

        @Override
        public boolean isDone() {
            return resultSetFuture.isDone();
        }

        @Override
        public Void get() throws InterruptedException, ExecutionException {
            resultSetFuture.get();
            return null;
        }

        @Override
        public Void getUninterruptibly() {
            resultSetFuture.getUninterruptibly();
            return null;
        }

        @Override
        public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
            resultSetFuture.get(timeout, unit);
            return null;
        }

        @Override
        public void addListener(Runnable listener, Executor executor) {
            resultSetFuture.addListener(listener, executor);
        }
    }
}
