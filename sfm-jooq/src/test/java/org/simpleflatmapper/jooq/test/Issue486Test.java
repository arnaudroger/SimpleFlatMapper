package org.simpleflatmapper.jooq.test;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.junit.Test;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;

import java.sql.Timestamp;
//IFJAVA8_START
import java.time.Instant;
import java.util.Optional;
//IFJAVA8_END
import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.simpleflatmapper.jooq.test.Issue318Test.createField;

public class Issue486Test {
    
    @Test
    public void noTestJava7() {
        
    }

    //IFJAVA8_START
    @Test
    public void testIssue() {

        SfmRecordMapperProvider recordMapperProvider = new SfmRecordMapperProvider();

        RecordType<Record> rt = mock(RecordType.class);


        Field[] fields = new Field[] {
                createField("t", Timestamp.class),
                createField("id", String.class)
        };

        when(rt.size()).thenReturn(fields.length);
        when(rt.fields()).thenReturn(fields);

        RecordMapper<Record, Issue486Test.Issue486> mapper = recordMapperProvider.provide(rt, Issue486Test.Issue486.class);

        assertNotNull(mapper);


    }

    public static class Issue486 {
        private Optional<Instant> t;
        private UUID id;

        public Optional<Instant> getT() {
            return t;
        }

        public void setT(Optional<Instant> t) {
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




}
