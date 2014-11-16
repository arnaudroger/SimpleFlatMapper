package org.sfm.csv;

import static org.junit.Assert.*;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
//IFJAVA8_START
import java.util.function.Consumer;
import java.util.stream.Stream;
//IFJAVA8_END

import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbListObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbObject.Type;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.utils.ListHandler;
import org.sfm.utils.RowHandler;

public class DynamicCsvMapperImplTest {

	public static Reader dbObjectCsvReader() throws UnsupportedEncodingException {
		Reader sr = new StringReader("id,name,email,creationTime,typeOrdinal,typeName\n"
				+ "1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4");
		return sr;
	}
	
	public static Reader dbObjectCsvReader3Lines() throws UnsupportedEncodingException {
		Reader sr = new StringReader("\nid,name,email,creationTime,typeOrdinal,typeName\n"
				+ "1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4\n"
				+ "2,name 2,name2@mail.com,2014-03-04 11:10:03,2,type4"
				
				);
		return sr;
	}
	
	@Test
	public void testDbObject() throws Exception {
		CsvMapper<DbObject> mapper = CsvMapperFactory.newInstance().newMapper(DbObject.class);
		
		List<DbObject> list = mapper.forEach(dbObjectCsvReader3Lines(), new ListHandler<DbObject>(),1,1).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}

	@Test
	public void testFinalDbObject() throws Exception {
		CsvMapper<DbFinalObject> mapper = CsvMapperFactory.newInstance().newMapper(DbFinalObject.class);

		List<DbFinalObject> list = mapper.forEach(dbObjectCsvReader(), new ListHandler<DbFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
		
	}
	
	@Test
	public void testPartialFinalDbObject() throws Exception {
		CsvMapper<DbPartialFinalObject> mapper = CsvMapperFactory.newInstance().newMapper(DbPartialFinalObject.class);
		
		List<DbPartialFinalObject> list = mapper.forEach(dbObjectCsvReader(), new ListHandler<DbPartialFinalObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
		
	}
	
	
	@Test
	public void testDbObjectIterator() throws Exception {
		CsvMapper<DbObject> mapper = CsvMapperFactory.newInstance().newMapper(DbObject.class);
		
		Iterator<DbObject> it = mapper.iterate(dbObjectCsvReader3Lines(), 1);
		
		assertTrue(it.hasNext());
		DbHelper.assertDbObjectMapping(1, it.next());
		assertTrue(it.hasNext());
		DbHelper.assertDbObjectMapping(2, it.next());
		assertFalse(it.hasNext());
		
	}


	//IFJAVA8_START
	@Test
	public void testDbObjectStream() throws Exception {
		CsvMapper<DbObject> mapper = CsvMapperFactory.newInstance().newMapper(DbObject.class);

		Stream<DbObject> it = mapper.stream(dbObjectCsvReader3Lines(), 1);

		it.forEach(new Consumer<DbObject>() {
			int i = 1;

			@Override
			public void accept(DbObject dbObject) {
				try {
					DbHelper.assertDbObjectMapping(i, dbObject);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
				i++;
			}
		});
	}
	//IFJAVA8_END


	private static final String CSV_LIST = "id,objects_0_id,objects_0_name,objects_0_email,objects_0_creationTime,objects_0_typeOrdinal,objects_0_typeName\n"
			+ "1,1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4";
	@Test
	public void testDbListObject() throws Exception {
		
		CsvMapper<DbListObject> mapper = CsvMapperFactory.newInstance().newMapper(DbListObject.class);
		
		
		List<DbListObject> list = mapper.forEach(new StringReader(CSV_LIST), new ListHandler<DbListObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0).getObjects().get(0));

	}
	
	

	private static final int NBROW = 2;
	private static final int NBFUTURE = 10000;
	
	@Test
	public void testMultipleThread() throws InterruptedException, ExecutionException {
		final CsvMapper<DbObject> mapper = CsvMapperFactory.newInstance().newMapper(DbObject.class);

		ExecutorService service = Executors.newFixedThreadPool(4);
		final AtomicLong sumOfAllIds = new AtomicLong();
		final AtomicLong nbRow = new AtomicLong();
		
		final RowHandler<DbObject> handler = new RowHandler<DbObject>() {
			@Override
			public void handle(DbObject t) throws Exception {
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
		
		final String str = buildCsvContent();
		
		List<Future<Object>> futures = new ArrayList<Future<Object>>(); 
		for(int i = 0; i < NBFUTURE; i++) {
			futures.add(service.submit(new Callable<Object>() {
				@Override
				public Object call() throws Exception {
					mapper.forEach(new StringReader(str), handler);
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
		assertEquals(nbRow.get(), NBFUTURE * NBROW);
		
		int sum = 0;
		for(i = 0 ; i < NBROW ; i++) {
			sum += i;
		}
		
		assertEquals(sumOfAllIds.get(), NBFUTURE * sum);
	}

	private String buildCsvContent() {
		StringBuilder sb = new StringBuilder();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		sb.append("id,name,email,type_name,type_ordinal,creation_time\n");
		for(int i = 0; i < NBROW; i++) {
			sb.append(Long.toString(i)).append(",");
			sb.append("name" + Long.toHexString(i)).append(",");
			sb.append("email" + Long.toHexString(i)).append(",");
			sb.append("type" + ((i % 4) + 1)).append(",");
			sb.append(Long.toString(i % 4)).append(",");
			sb.append(sdf.format(new Date(i * 1000))).append("\n");
		}
		
		final String str = sb.toString();
		return str;
	}
}
