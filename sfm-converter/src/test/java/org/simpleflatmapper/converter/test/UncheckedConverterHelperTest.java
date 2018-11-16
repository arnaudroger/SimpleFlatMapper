package org.simpleflatmapper.converter.test;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.converter.UncheckedConverterHelper;

import java.io.IOException;

import static org.junit.Assert.*;

public class UncheckedConverterHelperTest {


    @Test
    public void testUncheckConverterFail() {

        ContextualConverter<Object, Object> converter = new ContextualConverter<Object, Object>() {
            @Override
            public Object convert(Object in, Context context) throws IOException {
                throw new IOException("What!");
            }
        };


        try {
            convertUncheck(converter);
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IOException);
        }


    }

    @Test
    public void testUncheckConverterWorj() {

        ContextualConverter<Object, Object> converter = new ContextualConverter<Object, Object>() {
            @Override
            public Object convert(Object in, Context context) throws IOException {
                return "Ok!";
            }
        };

        assertEquals("Ok!", UncheckedConverterHelper.toUnchecked(converter).convert(null,  null));
    }

    private void convertUncheck(ContextualConverter<Object, Object> converter) {
        UncheckedConverterHelper.toUnchecked(converter).convert(null, null);
    }
}