package org.sfm.csv;

import org.junit.Before;
import org.junit.Test;
import org.sfm.beans.DbFinalObject;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPartialFinalObject;
import org.sfm.jdbc.DbHelper;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;
import org.sfm.reflect.TypeReference;
import org.sfm.tuples.Tuple2;
import org.sfm.utils.ListHandler;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CsvMapperOptionalTest {
	private CsvMapperFactory csvMapperFactory;
	private CsvMapperFactory csvMapperFactoryNoAsm;

	@Before
	public void setUp() {
		csvMapperFactory = CsvMapperFactory.newInstance().failOnAsm(true);
		csvMapperFactoryNoAsm = CsvMapperFactory.newInstance().useAsm(false);
	}


    @Test
    public void testStaticMapperDbObjectToStringNoAsm() throws Exception {
        CsvMapperBuilder<Optional<DbObject>> builder = csvMapperFactoryNoAsm.newBuilder(new TypeReference<Optional<DbObject>>() {
		}.getType());
        addDbObjectFields(builder);
    }

    @Test
    public void testStaticMapperDbFinalObjectToString() throws Exception {
        CsvMapperBuilder<Optional<DbObject>> builder = csvMapperFactory.useAsm(false).newBuilder(new TypeReference<Optional<DbFinalObject>>(){}.getType());
        addDbObjectFields(builder);
    }

    @Test
	public void testMapDbObject() throws Exception {
		testMapDbObject(csvMapperFactory.newBuilder(new TypeReference<Optional<DbFinalObject>>(){}.getType()));
	}

	@Test
	public void testMapDbObjectNoAsm() throws Exception {
		testMapDbObject(csvMapperFactoryNoAsm.newBuilder(new TypeReference<Optional<DbFinalObject>>(){}.getType()));
	}

	private void testMapDbObject(CsvMapperBuilder<Optional<DbFinalObject>> builder)
			throws IOException, ParseException {
		addDbObjectFields(builder);
		CsvMapper<Optional<DbFinalObject>> mapper = builder.mapper();
		
		List<Optional<DbFinalObject>> list = mapper.forEach(CsvMapperImplTest.dbObjectCsvReader(), new ListHandler<Optional<DbFinalObject>>()).getList();
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
		.addMapping("creationTime", CsvColumnDefinition.timeZoneDefinition(TimeZone.getTimeZone("Europe/London")))
		.addMapping("typeOrdinal")
		.addMapping("typeName");
	}


}
