package org.simpleflatmapper.core.reflect.getter;

import org.junit.Test;
import org.simpleflatmapper.util.UUIDHelper;

import java.util.UUID;

import static org.junit.Assert.*;

public class BytesUUIDGetterTest {
    @Test
    public void get() throws Exception {
        UUID uuid = UUID.randomUUID();
        BytesUUIDGetter<Object> getter = new BytesUUIDGetter<Object>(new ConstantGetter<Object, byte[]>(UUIDHelper.toBytes(uuid)));

        assertEquals(uuid, getter.get(null));

        getter.toString();
    }

}