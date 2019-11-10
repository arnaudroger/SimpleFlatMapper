package org.simpleflatmapper.jooq.test;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jooq.*;
import org.simpleflatmapper.test.beans.DbExtendedType;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JooqUnmapperTest {
//
//	@Test
//	@SuppressWarnings("unchecked")
//	public void testCacheMapper() {
//
//		SfmRecordUnmapperProvider recordMapperProvider = SfmRecordMapperProviderFactory.newInstance().newRecordUnapperProvider();;
//		RecordType rt = mock(RecordType.class);
//		Field field1 = mock(Field.class);
//		when(field1.getName()).thenReturn("id");
//		when(field1.getType()).thenReturn(long.class);
//		when(rt.size()).thenReturn(1);
//		when(rt.fields()).thenReturn(new Field[] {field1});
//
//		JooqRecordUnmapperWrapper provider1 =
//				(JooqRecordUnmapperWrapper) recordMapperProvider.<DbObject, Record>provide(DbObject.class, rt);
//		JooqRecordUnmapperWrapper provider2 =
//				(JooqRecordUnmapperWrapper) recordMapperProvider.<DbObject, Record>provide(DbObject.class, rt);
//		assertSame(provider1.getMapper(), provider2.getMapper());
//	}
//
//	@Test
//	public void testUnmapping() throws Exception {
//		Connection conn = DbHelper.objectDb();
//
//		DSLContext dsl = DSL
//				.using(new DefaultConfiguration().set(conn)
//						.set(SQLDialect.HSQLDB)
//						.set(SfmRecordMapperProviderFactory.newInstance().newRecordUnapperProvider()));
//
//		Label label = new Label(1, UUID.randomUUID(), "label", false);
//
//		LabelsRecord labelsRecord = dsl.newRecord(Labels.LABELS, label);
//
//		assertEquals(label.getId(), labelsRecord.getId());
//		assertEquals(label.getName(), labelsRecord.getName());
//		assertEquals(label.getUuid(), labelsRecord.getUuid());
//		assertEquals(label.getObsolete(), labelsRecord.getObsolete());
//
//
//	}
}
