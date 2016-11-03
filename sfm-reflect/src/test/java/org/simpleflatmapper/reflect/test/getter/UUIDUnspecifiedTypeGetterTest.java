package org.simpleflatmapper.reflect.test.getter;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.getter.UUIDUnspecifiedTypeGetter;
import org.simpleflatmapper.util.UUIDHelper;

import java.io.ByteArrayInputStream;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UUIDUnspecifiedTypeGetterTest {



    @Test
    public void test() throws Exception {

        UUID uuid = UUID.randomUUID();

        @SuppressWarnings("unchecked") Getter<Object, Object> subGetter = mock(Getter.class);
        UUIDUnspecifiedTypeGetter<Object> getter = new UUIDUnspecifiedTypeGetter<Object>(subGetter);

        when(subGetter.get(any())).thenReturn(uuid.toString(),
                UUIDHelper.toBytes(uuid),
                new ByteArrayInputStream(UUIDHelper.toBytes(uuid)), new Date());

        assertEquals(uuid, getter.get(null));
        assertEquals(uuid, getter.get(null));
        assertEquals(uuid, getter.get(null));

        try {
            getter.get(null);
            fail();
        } catch (IllegalArgumentException e) {

        }

        getter.toString();


    }
}