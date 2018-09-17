package org.simpleflatmapper.jooq.test;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.junit.Test;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;
import org.simpleflatmapper.jooq.SfmRecordMapperProviderFactory;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.simpleflatmapper.jooq.test.Issue537Test.createField;

public class Issue550Test {


    @Test
    public void testLocalDateTimeFromTimestamp() throws SQLException {

        SfmRecordMapperProvider recordMapperProvider = 
                SfmRecordMapperProviderFactory
                        .newInstance()
                        .addAlias("DATA", "blob")
//                        .addColumnProperty("DATA", ConverterProperty.of(new Converter<Blob, byte[]>() {
//                            @Override
//                            public byte[] convert(Blob in, Context c) throws Exception {
//                                return in.getBytes(0, Long.valueOf(in.length()).intValue());
//                            }
//                        }))
                        .newProvider();

        RecordType<Record> rt = mock(RecordType.class);


        Field[] fields = new Field[] {
                createField("data", Blob.class),
        };

        when(rt.size()).thenReturn(fields.length);
        when(rt.fields()).thenReturn(fields);

        RecordMapper<Record, LobData> mapper = recordMapperProvider.provide(rt, LobData.class);

        assertNotNull(mapper);
    }

    @Table( name="T_FILESTORE_LOB" )
    public static class LobData extends  TS
    {
        private static final long serialVersionUID = 3572155198489328497L;

        @Id
        //@BusinessKey( required=false )
        //@Column( name="FK_METADATA_ID" )
        private Long id;

        @MapsId
        @JoinColumn( name="FK_METADATA_ID", referencedColumnName="ID" )
        @OneToOne( fetch= FetchType.EAGER, optional=false )
        private MetaData metaData;

        @Lob
        @Column( name="data" )
        private byte[] blob;

        public void setId(Long id) {
            this.id = id;
        }

        public Long getId() {
            return id;
        }

        public void setMetaData(MetaData metaData )
        {
            this.metaData = metaData;
        }

        public MetaData getMetaData()
        {
            return metaData;
        }

        public void setBlob( byte[] blob )
        {
            this.blob = Arrays.copyOf(blob, blob.length);
        }

        public void setBlob( Blob b ) throws SQLException {
            setBlob(b.getBytes(0, Long.valueOf(b.length()).intValue()));
        }

        public byte[] getBlob() {
            return blob;
        }
    }

    public static class MetaData {
    }

    public static class TS {
        private long ts;

        public long getTs() {
            return ts;
        }

        public void setTs(long ts) {
            this.ts = ts;
        }
    }
}
