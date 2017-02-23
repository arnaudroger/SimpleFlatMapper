package org.simpleflatmapper.jdbc.test;

import org.junit.Test;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbObject.Type;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.test.jdbc.TestRowHandler;
import org.simpleflatmapper.util.ListCollector;
import org.simpleflatmapper.util.CheckedConsumer;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
//IFJAVA8_START
import java.util.function.Consumer;
import java.util.stream.Stream;
//IFJAVA8_END

import static org.junit.Assert.*;



public class DynamicJdbcMapperTest {

	final JdbcMapper<DbObject> mapper;

	public DynamicJdbcMapperTest() throws NoSuchMethodException, SecurityException, SQLException {
		mapper = JdbcMapperFactoryHelper.noAsm().newMapper(DbObject.class);
	}

	@Test
	public void testResultSetMapperForEachRS()
			throws Exception {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				List<DbObject> objects = mapper.forEach(ps.executeQuery(), new ListCollector<DbObject>()).getList();
				assertEquals(1, objects.size());
				DbHelper.assertDbObjectMapping(objects.get(0));
			}
		});
	}

	@Test
	public void testResultSetMapperIterator()
			throws Exception {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				Iterator<DbObject> objectIterator = mapper.iterator(ps.executeQuery());
				assertTrue(objectIterator.hasNext());
				try {
					objectIterator.remove();
					fail("Expect UnsupportedOperationException");
				} catch(UnsupportedOperationException e) {
				}
				DbHelper.assertDbObjectMapping(objectIterator.next());
				assertFalse(objectIterator.hasNext());
				try {
					objectIterator.next();
					fail("Expect UnsupportedOperationException");
				} catch(NoSuchElementException e) {}
			}
		});
	}


	//IFJAVA8_START
	@Test
	public void testResultSetMapperStream()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				Stream<DbObject> stream = mapper.stream(ps.executeQuery());
				stream.forEach(new Consumer<DbObject>() {
					int i = 0;
					@Override
					public void accept(DbObject dbObject) {
						assertTrue(i < 1);
						try {
							DbHelper.assertDbObjectMapping(dbObject);
						} catch (ParseException e) {
							throw new RuntimeException(e);
						}
						i++;
					}
				});
			}
		});
	}
	//IFJAVA8_END

	@Test
	public void testResultSetMapperMap()
			throws SQLException, Exception, ParseException {
		DbHelper.testDbObjectFromDb(new TestRowHandler<PreparedStatement>() {
			@Override
			public void handle(PreparedStatement ps) throws Exception {
				ResultSet rs = ps.executeQuery();
				rs.next();
				DbObject object = mapper.map(rs);
				DbHelper.assertDbObjectMapping(object);
			}
		});
	}


	private static final int NBROW = 2;
	private static final int NBFUTURE = 10000;
	@Test
	public void testMultipleThread() throws InterruptedException, ExecutionException {
		final JdbcMapper<DbObject> mapper = JdbcMapperFactoryHelper.asm().newMapper(DbObject.class);

		ExecutorService service = Executors.newFixedThreadPool(4);
		final AtomicLong sumOfAllIds = new AtomicLong();
		final AtomicLong nbRow = new AtomicLong();

		final CheckedConsumer<DbObject> handler = new CheckedConsumer<DbObject>() {
			@Override
			public void accept(DbObject t) throws Exception {
				long id = t.getId();

				assertEquals("name" + Long.toHexString(id), t.getName());
				assertEquals("email" + Long.toHexString(id), t.getEmail());
				assertEquals(Type.values()[(int)(id) % 4], t.getTypeName());
				assertEquals(Type.values()[(int)(id) % 4], t.getTypeOrdinal());
				assertEquals(id, t.getCreationTime().getTime() / 1000);

				sumOfAllIds.addAndGet(id);
				nbRow.incrementAndGet();
			}
		};


		List<Future<Object>> futures = new ArrayList<Future<Object>>();
		for(int i = 0; i < NBFUTURE; i++) {
			futures.add(service.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					mapper.forEach(new MockDbObjectResultSet(NBROW), handler);
					return null;
				}
			}));
		}


		int i = 0;
		for(Future<Object> future : futures) {
			try {
				future.get();
			}  catch(Exception e) {
				System.out.println("Future " + i + " fail " + e);
			}
			i++;
		}
		assertEquals(NBFUTURE, i);
		assertEquals(NBFUTURE * NBROW, nbRow.get());

		int sum = 0;
		for(i = 1 ; i <= NBROW ; i++) {
			sum += i;
		}

		assertEquals(NBFUTURE * sum, sumOfAllIds.get());
	}

}
