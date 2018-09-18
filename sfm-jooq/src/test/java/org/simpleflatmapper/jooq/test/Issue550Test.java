package org.simpleflatmapper.jooq.test;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.junit.Test;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;
import org.simpleflatmapper.jooq.SfmRecordMapperProviderFactory;
import org.simpleflatmapper.map.property.ConverterProperty;

import javax.persistence.Column;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.simpleflatmapper.jooq.test.Issue537Test.createField;

public class Issue550Test {


    @Test
    public void test550() throws SQLException {

        SfmRecordMapperProvider recordMapperProvider = 
                SfmRecordMapperProviderFactory
                        .newInstance()
                        .addAlias("DATA", "blob")
                        .newProvider();

        RecordType<Record> rt = mock(RecordType.class);


        Field[] fields = new Field[] {
                createField("id", Long.class),
                createField("data", Blob.class),
        };

        when(rt.size()).thenReturn(fields.length);
        when(rt.fields()).thenReturn(fields);

        RecordMapper<Record, Foo> mapper = recordMapperProvider.provide(rt, Foo.class);

        assertNotNull(mapper);
    }

    public static class Foo
    {

        private long id;
        
        @Column( name="data" )
        private byte[] blob;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public void setBlob(byte[] blob )
        {
            this.blob = Arrays.copyOf(blob, blob.length);
        }

        public byte[] getBlob() {
            return blob;
        }
    }

}
