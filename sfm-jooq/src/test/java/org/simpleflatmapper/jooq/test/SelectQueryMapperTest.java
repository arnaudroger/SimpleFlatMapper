package org.simpleflatmapper.jooq.test;

import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jooq.*;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SelectQueryMapperTest {

	@Test
	public void testSelectQuery() throws SQLException {

		Connection conn = DbHelper.objectDb();

		Configuration cfg = new DefaultConfiguration()
				.set(conn)
				.set(SQLDialect.HSQLDB);

		cfg.set(JooqMapperFactory.newInstance().newRecordUnmapperProvider(cfg));

		DSLContext dsl = DSL.using(cfg);

		SelectQueryMapper<Label> mapper = SelectQueryMapperFactory.newInstance().newMapper(Label.class);

		UUID uuid1 = UUID.randomUUID();
		Label label = new Label(1, uuid1, "l1", false);



		dsl.insertInto(Labels.LABELS).columns(Labels.LABELS.ID, Labels.LABELS.NAME, Labels.LABELS.OBSOLETE, Labels.LABELS.UUID)
				.values(1, "l1", false, uuid1).execute();

		List<Label> labels = mapper.asList(dsl.select(Labels.LABELS.ID, Labels.LABELS.NAME, Labels.LABELS.OBSOLETE, Labels.LABELS.UUID).from(Labels.LABELS));

		assertEquals(1, labels.size());
		assertEquals(label, labels.get(0));


	}
}
