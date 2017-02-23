package org.simpleflatmapper.test.map.context;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.test.map.SampleFieldKey;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.map.context.impl.BreakDetectorMappingContextFactory;
import org.simpleflatmapper.map.context.impl.ValuedMappingContextFactory;
import org.simpleflatmapper.util.ConstantSupplier;

import java.util.Arrays;

import static org.junit.Assert.*;

public class MappingContextFactoryBuilderTest {


    private MappingContextFactoryBuilder<Object[], SampleFieldKey> builder;

    @Before
    public void setUp() {
        builder = new MappingContextFactoryBuilder<Object[], SampleFieldKey>(getKeySourceGetter());
    }
    @Test
    public void testEmpty() {
        assertTrue(builder.hasNoKeys());
        assertSame(MappingContext.EMPTY_FACTORY, builder.newFactory());
        assertNotNull(builder.toString());
    }


    @Test
    public void testSuppliers() {
        builder.addSupplier(1, new ConstantSupplier<String>("hh"));

        assertTrue(builder.hasNoKeys());
        MappingContextFactory<Object[]> mappingContextFactory = builder.newFactory();
        assertTrue(mappingContextFactory instanceof ValuedMappingContextFactory);

        assertEquals("hh", mappingContextFactory.newContext().context(1));
        assertNull(mappingContextFactory.newContext().context(0));
    }

    @Test
    public void testKeys() throws Exception {
        builder.addKey(new SampleFieldKey("k1", 0));

        assertFalse(builder.hasNoKeys());
        MappingContextFactory<Object[]> mappingContextFactory = builder.newFactory();


        assertTrue(mappingContextFactory instanceof BreakDetectorMappingContextFactory);

        assertNull(mappingContextFactory.newContext().context(0));


        assertTrue(builder.nullChecker().test(new Object[] { null}));
        assertFalse(builder.nullChecker().test(new Object[] { 123 }));

    }

    @Test
    public void testKeysAndSuppliers() {
        builder.addSupplier(1, new ConstantSupplier<String>("hh"));
        builder.addKey(new SampleFieldKey("k1", 0));

        assertFalse(builder.hasNoKeys());
        MappingContextFactory<Object[]> mappingContextFactory = builder.newFactory();
        assertTrue(mappingContextFactory instanceof BreakDetectorMappingContextFactory);

        assertEquals("hh", mappingContextFactory.newContext().context(1));
        assertNull(mappingContextFactory.newContext().context(0));
    }

    @Test
    public void testKeysWithSubBuilder() {
        builder.addKey(new SampleFieldKey("k1", 0));
        MappingContextFactoryBuilder<Object[], SampleFieldKey> subBuilder = builder.newBuilder(Arrays.asList(new SampleFieldKey("k2", 3)), null);

        subBuilder.newBuilder(Arrays.asList(new SampleFieldKey("k3", 6)), null);

        MappingContextFactory<Object[]> mappingContextFactory = builder.newFactory();

        MappingContext<Object[]> mappingContext = mappingContextFactory.newContext();

    }

    private KeySourceGetter<SampleFieldKey, Object[]> getKeySourceGetter() {
        return new KeySourceGetter<SampleFieldKey, Object[]>() {
            @Override
            public Object getValue(SampleFieldKey key, Object[] source) throws Exception {
                return source[key.getIndex()];
            }
        };
    }
}