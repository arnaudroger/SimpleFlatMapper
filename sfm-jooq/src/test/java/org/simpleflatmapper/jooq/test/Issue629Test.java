package org.simpleflatmapper.jooq.test;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.junit.Test;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;
import org.simpleflatmapper.jooq.SfmRecordMapperProviderFactory;

import java.sql.SQLException;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.simpleflatmapper.jooq.test.Issue537Test.createField;

public class Issue629Test {


    @Test
    public void test629() throws SQLException {

        SfmRecordMapperProvider recordMapperProvider = 
                SfmRecordMapperProviderFactory
                        .newInstance()
                        .addAliasForType(Bar.class, "barId", "id")
                        .addAliasForType(Zoom.class, "zoomId", "id")
                        .newProvider();

        RecordType<Record> rt = mock(RecordType.class);

        Field[] fields = new Field[] {
                createField("id", Long.class),
        };

        when(rt.size()).thenReturn(fields.length);
        when(rt.fields()).thenReturn(fields);

        RecordMapper<Record, Foo> fooMapper = recordMapperProvider.provide(rt, Foo.class);
        assertNotNull(fooMapper);

        fields = new Field[] {
                createField("barId", Long.class),
        };

        reset(rt);
        when(rt.size()).thenReturn(fields.length);
        when(rt.fields()).thenReturn(fields);

        RecordMapper<Record, Bar> barMapper = recordMapperProvider.provide(rt,  Bar.class);
        assertNotNull(fooMapper);


        fields = new Field[] {
                createField("zoomId", Long.class),
        };

        reset(rt);
        when(rt.size()).thenReturn(fields.length);
        when(rt.fields()).thenReturn(fields);

        RecordMapper<Record, Zoom> zoomMapper = recordMapperProvider.provide(rt, Zoom.class);
        assertNotNull(fooMapper);
    }

    public static class Foo
    {
        public long id;
    }
    public static class Bar
    {
        public long id;
    }
    public static class Zoom
    {
        public long id;
    }
}
