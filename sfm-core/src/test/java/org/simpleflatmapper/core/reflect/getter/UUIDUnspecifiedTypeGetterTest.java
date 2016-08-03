package org.simpleflatmapper.core.reflect.getter;

import org.junit.Test;
import org.simpleflatmapper.core.reflect.Getter;
import org.simpleflatmapper.core.utils.UUIDHelper;

import java.io.ByteArrayInputStream;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UUIDUnspecifiedTypeGetterTest {



    @Test
    public void test() throws Exception {

        UUID uuid = UUID.randomUUID();

        Getter subGetter = mock(Getter.class);
        UUIDUnspecifiedTypeGetter<Object> getter = new UUIDUnspecifiedTypeGetter<Object>(subGetter);

        when(subGetter.get(any())).thenReturn(uuid.toString(), UUIDHelper.toBytes(uuid), new ByteArrayInputStream(UUIDHelper.toBytes(uuid)));

        assertEquals(uuid, getter.get(null));
        assertEquals(uuid, getter.get(null));
        assertEquals(uuid, getter.get(null));

        getter.toString();


    }
}