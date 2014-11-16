package org.sfm.csv;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Iterator;
import java.util.function.Consumer;
//IFJAVA8_START
import java.util.stream.Stream;
//IFJAVA8_END

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.csv.impl.CsvMapperImpl;
import org.sfm.jdbc.DbHelper;

public class CsvMapperImplTest {

	public static Reader dbObjectCsvReader() throws UnsupportedEncodingException {
		Reader sr = new StringReader("1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4");
		return sr;
	}
	
	public static Reader dbObjectCsvReader3Lines() throws UnsupportedEncodingException {
		Reader sr = new StringReader("0,name 0,name0@mail.com,2014-03-04 11:10:03,2,type4\n"
				+ "1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4\n"
				+ "2,name 2,name2@mail.com,2014-03-04 11:10:03,2,type4"
				
				);
		return sr;
	}
	
	@Test
	public void testCsvIterator()
			throws IOException, UnsupportedEncodingException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();
		
		
		Iterator<DbObject> it = mapper.iterate(CsvMapperImplTest.dbObjectCsvReader3Lines());
		DbHelper.assertDbObjectMapping(0, it.next());
		DbHelper.assertDbObjectMapping(1, it.next());
		DbHelper.assertDbObjectMapping(2, it.next());
		assertFalse(it.hasNext());
	}

	//IFJAVA8_START
	@Test
	public void testCsvStream()
			throws IOException, UnsupportedEncodingException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();

		Stream<DbObject> st = mapper.stream(CsvMapperImplTest.dbObjectCsvReader3Lines());
		st.forEach(new Consumer<DbObject>() {
			int i = 0;

			@Override
			public void accept(DbObject t) {
				assertTrue(i < 3);
				try {
					DbHelper.assertDbObjectMapping(i++, t);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});
	}
	//IFJAVA8_END
}
