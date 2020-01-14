package org.simpleflatmapper.jooq.test;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.jooq.DSLContextProvider;
import org.simpleflatmapper.jooq.JooqBiConverter;
import org.simpleflatmapper.jooq.JooqMapperFactory;
import org.simpleflatmapper.jooq.test.books.Label;
import org.simpleflatmapper.jooq.test.books.Labels;
import org.simpleflatmapper.jooq.test.books.LabelsRecord;
import org.simpleflatmapper.test.jdbc.DbHelper;
import org.simpleflatmapper.util.TypeHelper;

import java.lang.reflect.Type;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class JooqBiConverterTest {

	//IFJAVA8_START
	@Test
	public void testUnmapping() throws Exception {
		Connection conn = DbHelper.objectDb();

		Configuration cfg = new DefaultConfiguration()
				.set(conn)
				.set(SQLDialect.HSQLDB);

		List<JooqBiConverter> converters = new ArrayList<>();
		converters.add(new JooqBiConverter<String, JSONObjectTest494.File>() {
			@Override
			public boolean match(Type modelType) {
				return TypeHelper.areEquals(modelType, JSONObjectTest494.File.class);
			}
			@Override
			public JSONObjectTest494.File get(String name, Context context) throws Exception {
				return new JSONObjectTest494.File(0L, name, null);
			}
			@Override
			public String convert(JSONObjectTest494.File file, Context context) throws Exception {
				return file.name;
			}
		});

		DSLContextProvider contextProvider = new DSLContextProvider() {
			@Override
			public DSLContext provide() {
				return DSL.using(cfg);
			}
		};
		JooqMapperFactory mapperFactory = JooqMapperFactory.newInstance()
				.addBiConverters(converters);

		cfg.set(mapperFactory.newRecordMapperProvider())
		   .set(mapperFactory.newRecordUnmapperProvider(contextProvider));

		DSLContext dsl = DSL.using(cfg);

		LabelsRecord labelsRecord = dsl.newRecord(Labels.LABELS);
		labelsRecord.setId(1);
		labelsRecord.setUuid(UUID.randomUUID());
		labelsRecord.setName("label");
		labelsRecord.setObsolete(false);
		labelsRecord.setFile("testFile");

		// test from Record -> Object
		Label label = labelsRecord.into(Label.class);
		assertEquals(labelsRecord.getId(), label.getId());
		assertEquals(labelsRecord.getName(), label.getName());
		assertEquals(labelsRecord.getUuid(), label.getUuid());
		assertEquals(labelsRecord.getObsolete(), label.getObsolete());
		assertEquals(labelsRecord.getFile(), label.getFile().name);

		// test from Object -> Record
		labelsRecord = dsl.newRecord(Labels.LABELS, label);
		assertEquals(label.getId(), labelsRecord.getId());
		assertEquals(label.getName(), labelsRecord.getName());
		assertEquals(label.getUuid(), labelsRecord.getUuid());
		assertEquals(label.getObsolete(), labelsRecord.getObsolete());
		assertEquals(label.getFile().name, labelsRecord.getFile());
	}
	//IFJAVA8_END

}
