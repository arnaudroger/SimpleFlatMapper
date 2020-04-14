package org.simpleflatmapper.jooq.test;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;
import org.simpleflatmapper.jooq.SfmRecordMapperProviderFactory;
import org.simpleflatmapper.test.jdbc.DbHelper;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
//IFJAVA8_START
import java.time.LocalDateTime;
import java.time.ZoneId;
//IFJAVA8_END
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Issue318Test {

    //IFJAVA8_START
    @SuppressWarnings("unchecked")
    @Test
    public void testLocalDateTimeFromTimestamp() throws SQLException {

        SfmRecordMapperProvider recordMapperProvider = new SfmRecordMapperProvider();

        RecordType<Record> rt = mock(RecordType.class);


        Field[] fields = new Field[] {
            createField("t", Timestamp.class),
            createField("id", String.class)
            };

        when(rt.size()).thenReturn(fields.length);
        when(rt.fields()).thenReturn(fields);

        RecordMapper<Record, Issue318> mapper = recordMapperProvider.provide(rt, Issue318.class);

        assertNotNull(mapper);
    }

    @Test
    public void testHsqlDb() throws SQLException {
        Connection conn = DbHelper.objectDb();

        DSLContext dsl = DSL
                .using(new DefaultConfiguration().set(conn)
                        .set(SQLDialect.HSQLDB)
                        .set(SfmRecordMapperProviderFactory.newInstance().ignorePropertyNotFound().newProvider()));

        List<Issue318> list = dsl.select()
                .from("issue318").fetchInto(Issue318.class);

        assertEquals(1, list.size());

        Issue318 value = list.get(0);

        assertTrue(Math.abs(value.getT().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli() - System.currentTimeMillis()) < 10000);
        assertNotNull(value.getId());

    }

    public static class Issue318 {
        private LocalDateTime t;
        private UUID id;

        public LocalDateTime getT() {
            return t;
        }

        public void setT(LocalDateTime t) {
            this.t = t;
        }

        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }
    }
    //IFJAVA8_END

    public static Field createField(String name, Class<?> aClass) {

        Field field = mock(Field.class);
        when(field.getName()).thenReturn(name);
        when(field.getType()).thenReturn(aClass);
        return field;
    }

}
