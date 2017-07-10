package org.simpleflatmapper.csv.test;

import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperBuilder;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.csv.CsvParser;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.csv.impl.CsvMapperImpl;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.CheckedConsumer;
import org.simpleflatmapper.util.TypeReference;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
//IFJAVA8_START
import java.util.function.Consumer;
import java.util.stream.Stream;
//IFJAVA8_END

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


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
    public void testCsvFieldMappingError() throws IOException {
        CsvMapper<Integer> mapper = CsvMapperFactory.newInstance().newMapper(Integer.class);

        final Iterator<Integer> iterator = mapper.iterator(new StringReader("value\n1\n2\nnnn"));

        assertEquals(1, iterator.next().intValue());
        assertEquals(2, iterator.next().intValue());

        try {
            iterator.next();
        } catch(NumberFormatException e) {
            System.out.println(e.toString());
        }

    }

	@Test
	public void testCsvForEach()
			throws IOException, ParseException {
		CsvMapperBuilder<DbObject> builder = CsvMapperFactory.newInstance().newBuilder(DbObject.class);
		CsvMapperBuilderTest.addDbObjectFields(builder);
		CsvMapperImpl<DbObject> mapper = (CsvMapperImpl<DbObject>) builder.mapper();

		int i = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader3Lines(), new CheckedConsumer<DbObject>() {
			int i = 0;
			@Override
			public void accept(DbObject dbObject) throws Exception {
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

		int i = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader3Lines(), new CheckedConsumer<DbObject>() {
			int i = 1;
			@Override
			public void accept(DbObject dbObject) throws Exception {
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

		int i = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader3Lines(), new CheckedConsumer<DbObject>() {
			int i = 1;
			@Override
			public void accept(DbObject dbObject) throws Exception {
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

	@Test
	public void testStringRoot() throws IOException {
		CsvMapperBuilder<String> builder = new CsvMapperBuilder<String>(String.class);
		builder.addMapping("c1");
		CsvMapper<String> mapper = builder.mapper();
		assertEquals("0", mapper.iterator(new StringReader("0")).next());
	}

	@Test
	public void testMapStringString() throws IOException  {
		Map next = CsvParser.mapTo(new TypeReference<Map<String, String>>() {
		}).iterator("key1,k_2\nv1,v2").next();
		assertEquals("v1", next.get("key1"));
		assertEquals("v2", next.get("k_2"));
	}

	@Test
	public void testMap() throws IOException  {
		Map next = CsvParser.mapTo(Map.class).iterator("key1,k_2\nv1,v2").next();
		assertEquals("v1", next.get("key1"));
		assertEquals("v2", next.get("k_2"));
	}

	@Test
	public void testSetString() throws IOException {
		Set<String> set = CsvParser.mapTo(new TypeReference<Set<String>>() {
		}) .iterator("v1,v2\ns1,s2").next();
		assertEquals(2, set.size());
		assertTrue(set.contains("s1"));
		assertTrue(set.contains("s2"));
	}

	@Test
	public void testArrayInt() throws IOException {
		int[] ints = CsvParser.mapTo(int[].class).iterator("0,2\n1,3").next();
		assertArrayEquals(new int[] {1,0,3}, ints);
	}
	
	
	@Test
	public void testBigXXX() throws IOException {
		Biggy biggy = CsvParser.mapTo(Biggy.class).iterator("bi,bd\n1234567891011121314,123456789.1234556789").next();
		
		assertEquals(new BigInteger("1234567891011121314"), biggy.bi);
		assertEquals(new BigDecimal("123456789.1234556789"), biggy.bd);

	}
	
	public static class Biggy {
		public BigInteger bi;
		public BigDecimal bd;
	}
}
