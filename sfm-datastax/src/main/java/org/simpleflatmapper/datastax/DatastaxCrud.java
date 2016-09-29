package org.simpleflatmapper.datastax;

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
    private final PreparedStatement deleteQueryWithTimestamp;
    private final PreparedStatement insertQuery;
    private final PreparedStatement insertQueryWithTtlAndTimestamp;
    private final PreparedStatement insertQueryWithTtl;
    private final PreparedStatement insertQueryWithTimestamp;
    private final BoundStatementMapper<K> keySetter;
    private final BoundStatementMapper<K> keySetterWith1Option;
    private final BoundStatementMapper<T> insertSetter;
    private final DatastaxMapper<T> selectMapper;
    private final int numberOfColumns;

    private final Session session;

    public DatastaxCrud(
            PreparedStatement insertQuery,
            PreparedStatement insertQueryWithTtlAndTimestamp,
            PreparedStatement insertQueryWithTtl,
            PreparedStatement insertQueryWithTimestamp,
            PreparedStatement readQuery,
            PreparedStatement deleteQuery,
            PreparedStatement deleteQueryWithTimestamp,
            BoundStatementMapper<T> insertSetter,
            BoundStatementMapper<K> keySetter,
            BoundStatementMapper<K> keySetterWith1Option,
            DatastaxMapper<T> selectMapper, int numberOfColumns,
            Session session) {
        this.readQuery = readQuery;
        this.deleteQuery = deleteQuery;
        this.insertQuery = insertQuery;
        this.insertQueryWithTtlAndTimestamp = insertQueryWithTtlAndTimestamp;
        this.insertQueryWithTtl = insertQueryWithTtl;
        this.insertQueryWithTimestamp = insertQueryWithTimestamp;
        this.deleteQueryWithTimestamp = deleteQueryWithTimestamp;
        this.keySetter = keySetter;
        this.insertSetter = insertSetter;
        this.keySetterWith1Option = keySetterWith1Option;
        this.selectMapper = selectMapper;
        this.numberOfColumns = numberOfColumns;
        this.session = session;
    }

    public void save(T value) {
        saveAsync(value).getUninterruptibly();
    }

    public void save(T value, int ttl, long timestamp) {
        saveAsync(value, ttl, timestamp).getUninterruptibly();
    }

    public void saveWithTtl(T value, int ttl) {
        saveWithTtlAsync(value, ttl).getUninterruptibly();
    }

    public void saveWithTimestamp(T value, long timestamp) {
        saveWithTimestampAsync(value, timestamp).getUninterruptibly();
    }

    public UninterruptibleFuture<Void> saveAsync(T value) {
        BoundStatement boundStatement = saveQuery(value);
        return new NoResultFuture(session.executeAsync(boundStatement));
    }

    public UninterruptibleFuture<Void> saveAsync(T value, int ttl, long timestamp) {
        BoundStatement boundStatement = saveQuery(value, ttl, timestamp);
        return new NoResultFuture(session.executeAsync(boundStatement));
    }

    public UninterruptibleFuture<Void> saveWithTtlAsync(T value, int ttl) {
        BoundStatement boundStatement = saveQueryWithTtl(value, ttl);
        return new NoResultFuture(session.executeAsync(boundStatement));
    }

    public UninterruptibleFuture<Void> saveWithTimestampAsync(T value, long timestamp) {
        BoundStatement boundStatement = saveQueryWithTimestamp(value, timestamp);
        return new NoResultFuture(session.executeAsync(boundStatement));
    }

    public BoundStatement saveQuery(T value) {
        return insertSetter.mapTo(value, insertQuery.bind());
    }

    public BoundStatement saveQuery(T value, int ttl, long timestamp) {
        BoundStatement boundStatement = insertQueryWithTtlAndTimestamp.bind();

        insertSetter.mapTo(value, boundStatement);

        boundStatement.setInt(numberOfColumns, ttl);
        boundStatement.setLong(numberOfColumns + 1, timestamp);

        return boundStatement;
    }

    public BoundStatement saveQueryWithTtl(T value, int ttl) {
        BoundStatement boundStatement = insertQueryWithTtl.bind();
        insertSetter.mapTo(value, boundStatement);

        boundStatement.setInt(numberOfColumns, ttl);

        return boundStatement;
    }

    public BoundStatement saveQueryWithTimestamp(T value, long timestamp) {
        BoundStatement boundStatement = insertQueryWithTimestamp.bind();
        insertSetter.mapTo(value, boundStatement);

        boundStatement.setLong(numberOfColumns, timestamp);

        return boundStatement;
    }

    public T read(K key) {
        return readAsync(key).getUninterruptibly();
    }

    public UninterruptibleFuture<T> readAsync(K key) {
        BoundStatement boundStatement = keySetter.mapTo(key, readQuery.bind());
        return new OneResultFuture<T>(session.executeAsync(boundStatement), selectMapper);
    }

    public void delete(K key) {
        deleteAsync(key).getUninterruptibly();
    }

    public void delete(K key, long timestamp) {
        deleteAsync(key, timestamp).getUninterruptibly();
    }

    public UninterruptibleFuture<Void> deleteAsync(K key, long timestamp) {
        BoundStatement boundStatement = deleteQuery(key, timestamp);
        ResultSetFuture resultSetFuture = session.executeAsync(boundStatement);
        return new NoResultFuture(resultSetFuture);
    }

    public UninterruptibleFuture<Void> deleteAsync(K key) {
        BoundStatement boundStatement = deleteQuery(key);
        ResultSetFuture resultSetFuture = session.executeAsync(boundStatement);

        return new NoResultFuture(resultSetFuture);
    }

    public BoundStatement deleteQuery(K key) {
        return keySetter.mapTo(key, deleteQuery.bind());
    }

    public BoundStatement deleteQuery(K key, long timestamp) {
        BoundStatement boundStatement = deleteQueryWithTimestamp.bind();
        boundStatement.setLong(0, timestamp);
        return keySetterWith1Option.mapTo(key, boundStatement);
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

    @Override
    public String toString() {
        return "DatastaxCrud{\n" +
                "\n\treadQuery=" + readQuery +
                ",\n\tdeleteQuery=" + deleteQuery +
                ",\n\tdeleteQueryWithTimestamp=" + deleteQueryWithTimestamp +
                ",\n\tinsertQuery=" + insertQuery +
                ",\n\tinsertQueryWithTtlAndTimestamp=" + insertQueryWithTtlAndTimestamp +
                ",\n\tinsertQueryWithTtl=" + insertQueryWithTtl +
                ",\n\tinsertQueryWithTimestamp=" + insertQueryWithTimestamp +
                ",\n\tkeySetter=" + keySetter +
                ",\n\tkeySetterWith1Option=" + keySetterWith1Option +
                ",\n\tinsertSetter=" + insertSetter +
                ",\n\tselectMapper=" + selectMapper +
                ",\n\tnumberOfColumns=" + numberOfColumns +
                ",\n\tsession=" + session +
                '}';
    }
}
