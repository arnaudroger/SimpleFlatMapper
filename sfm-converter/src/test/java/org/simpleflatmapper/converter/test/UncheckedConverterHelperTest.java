package org.simpleflatmapper.converter.test;

import org.junit.Assert;
import org.junit.Test;
import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.Converter;
import org.simpleflatmapper.converter.UncheckedConverterHelper;

import java.io.IOException;

import static org.junit.Assert.*;

public class UncheckedConverterHelperTest {


    @Test
    public void testUncheckConverterFail() {

        Converter<Object, Object> converter = new Converter<Object, Object>() {
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

        Converter<Object, Object> converter = new Converter<Object, Object>() {
            @Override
            public Object convert(Object in, Context context) throws IOException {
                return "Ok!";
            }
        };

        Assert.assertEquals("Ok!", UncheckedConverterHelper.toUnchecked(converter).convert(null,  null));
    }

    private void convertUncheck(Converter<Object, Object> converter) {
        UncheckedConverterHelper.toUnchecked(converter).convert(null, null);
    }
}