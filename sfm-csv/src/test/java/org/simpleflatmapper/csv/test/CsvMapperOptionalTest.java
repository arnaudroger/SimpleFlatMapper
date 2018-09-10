package org.simpleflatmapper.csv.test;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.csv.CsvMapper;
import org.simpleflatmapper.csv.CsvMapperBuilder;
import org.simpleflatmapper.csv.CsvMapperFactory;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.map.MapperBuildingException;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.tuple.Tuple2;
import org.simpleflatmapper.util.ListCollector;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;

public class CsvMapperOptionalTest {
	private CsvMapperFactory csvMapperFactory;
	private CsvMapperFactory csvMapperFactoryNoAsm;

	@Before
	public void setUp() {
		csvMapperFactory = CsvMapperFactory.newInstance().failOnAsm(true);
		csvMapperFactoryNoAsm = CsvMapperFactory.newInstance().useAsm(false);
	}

    @Test
	public void testMapDbObject() throws Exception {
		testMapDbObject(csvMapperFactory.newBuilder(new TypeReference<Optional<DbFinalObject>>(){}.getType()));
	}

	@Test
	public void testMapDbObjectNoAsm() throws Exception {
		testMapDbObject(csvMapperFactoryNoAsm.newBuilder(new TypeReference<Optional<DbFinalObject>>(){}.getType()));
	}

    @Test
    public void testMapOptionalInteger() throws IOException {
        final CsvMapper<Optional<Integer>> mapper = CsvMapperFactory.newInstance().newBuilder(new TypeReference<Optional<Integer>>() {
        }).addMapping("str1").mapper();

        final Iterator<Optional<Integer>> iterator = mapper.iterator(new StringReader("1\n\n2"));


        Optional<Integer> optional = iterator.next();

        assertEquals(1, optional.get().intValue());
        optional = iterator.next();

        assertFalse(optional.isPresent());

        optional = iterator.next();
        assertEquals(2, optional.get().intValue());

        assertFalse(iterator.hasNext());
    }

    @Test
    public void testMapOptionIntegerTwoPropFails() {
        try {
            CsvMapperFactory.newInstance().newBuilder(new TypeReference<Optional<Integer>>() {
            })
            .addMapping("str1")
            .addMapping("str2");
            fail();
        } catch(MapperBuildingException e) {
            // expecter
        }
    }

    @Test
    public void testMapListOptionInteger() throws IOException {
        final CsvMapper<List<Optional<Integer>>> mapper = CsvMapperFactory.newInstance().newBuilder(new TypeReference<List<Optional<Integer>>>() {
        })
        .addMapping("v0")
        .addMapping("v1")
        .mapper();

        final Iterator<List<Optional<Integer>>> iterator = mapper.iterator(new StringReader("1,2\n3"));
        List<Optional<Integer>> optionalList = iterator.next();
        assertEquals(2, optionalList.size());
        assertEquals(1, optionalList.get(0).get().intValue());
        assertEquals(2, optionalList.get(1).get().intValue());

        optionalList = iterator.next();
        assertEquals(2, optionalList.size());
        assertEquals(3, optionalList.get(0).get().intValue());
        assertFalse(optionalList.get(1).isPresent());
    }

	private void testMapDbObject(CsvMapperBuilder<Optional<DbFinalObject>> builder)
			throws IOException, ParseException {
		addDbObjectFields(builder);
		CsvMapper<Optional<DbFinalObject>> mapper = builder.mapper();

		List<Optional<DbFinalObject>> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListCollector<Optional<DbFinalObject>>()).getList();
		assertEquals(1, list.size());
		DbHelper.assertDbObjectMapping(list.get(0).get());
    }

    @Test
    public void testMapTypeReferenceDynamic() throws IOException {
        Tuple2<String, String> next = CsvMapperFactory
                .newInstance()
                .newMapper(new TypeReference<Tuple2<String, String>>() {
                })
                .iterator(new StringReader("e0,e1\nv0,v1")).next();
        assertEquals("v0", next.first());
        assertEquals("v1", next.second());
    }

    @Test
    public void testMapTypeReferenceBuild() throws IOException {
        Tuple2<String, String> next = CsvMapperFactory
                .newInstance()
                .newBuilder(new TypeReference<Tuple2<String, String>>() {
                }).addMapping("e0").addMapping("e1")
                .mapper()
                .iterator(new StringReader("v0,v1")).next();
        assertEquals("v0", next.first());
        assertEquals("v1", next.second());
    }

	public static void addDbObjectFields(CsvMapperBuilder<?> builder) {
		builder.addMapping("id")
		.addMapping("name")
		.addMapping("email")
		.addMapping("creationTime")
		.addMapping("typeOrdinal")
		.addMapping("typeName");
	}


}
