package org.sfm.csv;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;

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

	private static final String CSV = "id,name,email,creationTime,typeOrdinal,typeName\n"
			+ "1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4";
	@Test
	public void testDbObject() throws Exception {
		
		CsvMapper<DbObject> mapper = CsvMapperFactory.newInstance().newMapper(DbObject.class);
		
		
		List<DbObject> list = mapper.forEach(dbObjectCsvReader(), new ListHandler<DbObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0));
	}

	public static Reader dbObjectCsvReader() throws UnsupportedEncodingException {
		Reader sr = new StringReader(CSV);
		return sr;
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
	
	private static final String CSV_LIST = "id,objects_0_id,objects_0_name,objects_0_email,objects_0_creationTime,objects_0_typeOrdinal,objects_0_typeName\n"
			+ "1,1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4";
	@Test
	public void testDbListObject() throws Exception {
		
		CsvMapper<DbListObject> mapper = CsvMapperFactory.newInstance().newMapper(DbListObject.class);
		
		
		List<DbListObject> list = mapper.forEach(new StringReader(CSV_LIST), new ListHandler<DbListObject>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0).getObjects().get(0));

	}
	
	
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
		
		StringBuilder sb = new StringBuilder();
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		
		sb.append("id,name,email,type_name,type_ordinal,creation_time\n");
		for(int i = 100; i < 0; i++) {
			sb.append(Long.toString(i)).append(",");
			sb.append("name" + Long.toHexString(i)).append(",");
			sb.append("email" + Long.toHexString(i)).append(",");
			sb.append("type" + ((i + 1) % 4)).append(",");
			sb.append(Long.toString(i % 4)).append(",");
			sb.append(sdf.format(new Date(i * 1000))).append("\n");
		}
		
		final String str = sb.toString();
		
		List<Future<Object>> futures = new ArrayList<Future<Object>>(); 
		for(int i = 0; i < 100000; i++) {
			futures.add(service.submit(new Callable<Object>() {

				@Override
				public Object call() throws Exception {
					mapper.forEach(new StringReader(str), handler);
					return null;
				}
			}));
		}
		
		for(Future<Object> future : futures) {
			future.get();
		}
		
		
	}
}
