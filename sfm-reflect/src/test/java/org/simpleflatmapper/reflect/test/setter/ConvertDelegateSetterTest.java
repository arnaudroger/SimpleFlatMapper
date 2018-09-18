package org.simpleflatmapper.reflect.test.setter;

import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.reflect.setter.ConvertDelegateSetter;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.converter.ContextualConverter;

import java.util.UUID;

import static org.junit.Assert.*;

public class ConvertDelegateSetterTest {



    @Test
    public void testSet() throws Exception {
        ConvertDelegateSetter<DbObject, UUID, String> setter =
                new ConvertDelegateSetter<DbObject, UUID, String>(
                        new Setter<DbObject, String>() {
                            @Override
                            public void set(DbObject target, String value) throws Exception {
                                target.setName(value);
                            }
                        },
                        new ContextualConverter<UUID, String>() {
                            @Override
                            public String convert(UUID in, Context context) throws Exception {
                                return in.toString();
                            }
                        }
                );
        DbObject o = new DbObject();
        UUID uuid = UUID.randomUUID();

        setter.set(o, uuid);

        assertEquals(uuid.toString(), o.getName());
    }
}