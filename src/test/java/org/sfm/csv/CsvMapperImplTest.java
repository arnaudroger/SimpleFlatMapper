package org.sfm.csv;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.csv.impl.CsvMapperImpl;
import org.sfm.jdbc.DbHelper;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.RowHandler;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Iterator;
//IFJAVA8_START
import java.util.function.Consumer;
import java.util.stream.Stream;
//IFJAVA8_END

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;


public class CsvMapperImplTest {

	public static Reader dbObjectCsvReader() throws UnsupportedEncodingException {
		return new StringReader("1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4");
	}
	
	public static Reader dbObjectCsvReader3Lines() throws UnsupportedEncodingException {
		return new StringReader("0,name 0,name0@mail.com,2014-03-04 11:10:03,2,type4\n"
				+ "1,name 1,name1@mail.com,2014-03-04 11:10:03,2,type4\n"
				+ "2,name 2,name2@mail.com,2014-03-04 11:10:03,2,type4"
				
				);
	}


	@Test
	public void testCsvForEach()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();

		int i = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader3Lines(), new RowHandler<DbObject>() {
			int i = 0;
			@Override
			public void handle(DbObject dbObject) throws Exception {
				DbHelper.assertDbObjectMapping(i++, dbObject);
			}
		}).i;

		assertEquals(3, i);

	}

	@Test
	public void testCsvForEachSkip()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();

		int i = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader3Lines(), new RowHandler<DbObject>() {
			int i = 1;
			@Override
			public void handle(DbObject dbObject) throws Exception {
				DbHelper.assertDbObjectMapping(i++, dbObject);
			}
		}, 1).i;

		assertEquals(3, i);
	}
	@Test
	public void testCsvForEachSkipAndLimit()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();

		int i = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader3Lines(), new RowHandler<DbObject>() {
			int i = 1;
			@Override
			public void handle(DbObject dbObject) throws Exception {
				DbHelper.assertDbObjectMapping(i++, dbObject);
			}
		}, 1, 1).i;

		assertEquals(2, i);
	}

	@Test
	public void testCsvIterator()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();
		
		
		Iterator<DbObject> it = mapper.iterator(CsvMapperImplTest.dbObjectCsvReader3Lines());
		DbHelper.assertDbObjectMapping(0, it.next());
		DbHelper.assertDbObjectMapping(1, it.next());
		DbHelper.assertDbObjectMapping(2, it.next());
		assertFalse(it.hasNext());
	}


	@Test
	public void testCsvIteratorWithSkip()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();


		Iterator<DbObject> it = mapper.iterator(CsvMapperImplTest.dbObjectCsvReader3Lines(), 1);
		DbHelper.assertDbObjectMapping(1, it.next());
		DbHelper.assertDbObjectMapping(2, it.next());
		assertFalse(it.hasNext());
	}


	//IFJAVA8_START
	@Test
	public void testCsvStream()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();

		Stream<DbObject> st = mapper.stream(CsvMapperImplTest.dbObjectCsvReader3Lines());
		i = 0;
		st.forEach(new Consumer<DbObject>() {

			@Override
			public void accept(DbObject t) {
				try {
					DbHelper.assertDbObjectMapping(i++, t);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});
		assertEquals(3, i);
	}

	int i;
	@Test
	public void testCsvStreamWithSkip()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();

		Stream<DbObject> st = mapper.stream(CsvMapperImplTest.dbObjectCsvReader3Lines(), 1);
		i = 1;
		st.forEach(new Consumer<DbObject>() {

			@Override
			public void accept(DbObject t) {
				try {
					DbHelper.assertDbObjectMapping(i++, t);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});

		assertEquals(3, i);
	}

	@Test
	public void testCsvStreamTryAdvance()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();

		Stream<DbObject> st = mapper.stream(CsvMapperImplTest.dbObjectCsvReader3Lines());
		i = 0;
		st.limit(1).forEach(new Consumer<DbObject>() {

			@Override
			public void accept(DbObject t) {
				try {
					DbHelper.assertDbObjectMapping(i++, t);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});
		assertEquals(1, i);
	}
	//IFJAVA8_END


    public enum TypeRoot {
        type1   ("1"), type2   ("2"), type3   ("3"), type4   ("4");

        private String value;
        TypeRoot(String ... values) { this.value = values[0]; }
        public String getValue() { return value;  }
    }


    @Test
    public void testEnumRoot() throws IOException {
        CsvMapperBuilder<TypeRoot> builder = new CsvMapperBuilder<TypeRoot>(TypeRoot.class);
        builder.addMapping("c1");

        CsvMapper<TypeRoot> mapper = builder.mapper();


        assertEquals(TypeRoot.type1, mapper.iterator(new StringReader("0")).next());
    }

}
