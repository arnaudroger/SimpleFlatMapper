package org.simpleflatmapper.jooq.test;

import org.jooq.Configuration;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordType;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jooq.DSLContextProvider;
import org.simpleflatmapper.jooq.JooqMapperFactory;
import org.simpleflatmapper.jooq.JooqRecordUnmapperWrapper;
import org.simpleflatmapper.jooq.SfmRecordUnmapperProvider;
import org.simpleflatmapper.jooq.test.books.Label;
import org.simpleflatmapper.jooq.test.books.Labels;
import org.simpleflatmapper.jooq.test.books.LabelsRecord;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class JooqUnmapperTest {

	//IFJAVA8_START
	@Test
	@SuppressWarnings("unchecked")
	public void testCacheMapper() {

		SfmRecordUnmapperProvider recordMapperProvider = JooqMapperFactory.newInstance()
				.newRecordUnmapperProvider((Configuration) null);
		RecordType rt = mock(RecordType.class);
		Field field1 = mock(Field.class);
		when(field1.getName()).thenReturn("id");
		when(field1.getType()).thenReturn(long.class);
		when(rt.size()).thenReturn(1);
		when(rt.fields()).thenReturn(new Field[] {field1});

		JooqRecordUnmapperWrapper provider1 =
				(JooqRecordUnmapperWrapper) recordMapperProvider.<DbObject, Record>provide(DbObject.class, rt);
		JooqRecordUnmapperWrapper provider2 =
				(JooqRecordUnmapperWrapper) recordMapperProvider.<DbObject, Record>provide(DbObject.class, rt);
		assertSame(provider1.getMapper(), provider2.getMapper());
	}

	@Test
	public void testUnmapping() throws Exception {
		Connection conn = DbHelper.objectDb();

		Configuration cfg = new DefaultConfiguration()
				.set(conn)
				.set(SQLDialect.HSQLDB);

		cfg.set(JooqMapperFactory.newInstance().newRecordUnmapperProvider(new DSLContextProvider() {
			@Override
			public DSLContext provide() {
				return DSL.using(cfg);
			}
		}));

		DSLContext dsl = DSL.using(cfg);

		Label label = new Label(1, UUID.randomUUID(), "label", false);

		LabelsRecord labelsRecord = dsl.newRecord(Labels.LABELS, label);

		assertEquals(label.getId(), labelsRecord.getId());
		assertEquals(label.getName(), labelsRecord.getName());
		assertEquals(label.getUuid(), labelsRecord.getUuid());
		assertEquals(label.getObsolete(), labelsRecord.getObsolete());


	}
	//IFJAVA8_END

}
