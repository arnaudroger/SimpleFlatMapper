package org.simpleflatmapper.csv.test.parser;

import org.junit.Test;
import org.simpleflatmapper.csv.parser.AbstractCharConsumer;
import org.simpleflatmapper.csv.parser.CharSequenceCharBuffer;
import org.simpleflatmapper.csv.parser.ConfigurableCharConsumer;
import org.simpleflatmapper.csv.parser.NoopCellPreProcessor;
import org.simpleflatmapper.csv.parser.TextFormat;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class AbstractCharConsumerTest {

    @Test
    public void newCharConsumer() throws Exception {
        AbstractCharConsumer abstractCharConsumer;

        abstractCharConsumer = AbstractCharConsumer.newCharConsumer(TextFormat.RFC4180, new CharSequenceCharBuffer("aa,bb,cc"), NoopCellPreProcessor.INSTANCE, false);

        assertTrue(abstractCharConsumer instanceof ConfigurableCharConsumer);

        assertEquals(TextFormat.RFC4180.escapeChar, get("escapeChar", abstractCharConsumer));
        assertEquals(TextFormat.RFC4180.quoteChar, get("quoteChar", abstractCharConsumer));
        assertEquals(TextFormat.RFC4180.separatorChar, get("separatorChar", abstractCharConsumer));
        assertEquals(TextFormat.RFC4180.yamlComment, get("yamlComment", abstractCharConsumer));
        assertEquals(false, get("ignoreLeadingSpace", abstractCharConsumer));


        abstractCharConsumer = AbstractCharConsumer.newCharConsumer(TextFormat.RFC4180, new CharSequenceCharBuffer("aa,bb,cc"), NoopCellPreProcessor.INSTANCE, true);

        assertFalse(abstractCharConsumer instanceof ConfigurableCharConsumer);

        assertEquals(TextFormat.RFC4180.escapeChar, get("escapeChar", abstractCharConsumer));
        assertEquals(TextFormat.RFC4180.quoteChar, get("quoteChar", abstractCharConsumer));
        assertEquals(TextFormat.RFC4180.separatorChar, get("separatorChar", abstractCharConsumer));
        assertEquals(TextFormat.RFC4180.yamlComment, get("yamlComment", abstractCharConsumer));
        assertEquals(false, get("ignoreLeadingSpace", abstractCharConsumer));



    }

    private Object get(String method, AbstractCharConsumer instance) throws Exception {
        Method declaredMethod = instance.getClass().getDeclaredMethod(method);
        declaredMethod.setAccessible(true);
        return declaredMethod.invoke(instance);
    }
}