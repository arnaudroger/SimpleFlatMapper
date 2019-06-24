package org.simpleflatmapper.test.map.context;

import org.junit.Before;
import org.junit.Test;
import org.simpleflatmapper.map.MappingContext;
import org.simpleflatmapper.map.context.KeyAndPredicate;
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
        builder = new MappingContextFactoryBuilder<Object[], SampleFieldKey>(getKeySourceGetter(), true);
    }
    @Test
    public void testEmpty() {
        assertTrue(builder.hasNoKeys());
        assertSame(MappingContext.EMPTY_FACTORY, builder.build());
        assertNotNull(builder.toString());
    }


    @Test
    public void testSuppliers() {
        int i = builder.addSupplier(new ConstantSupplier<String>("hh"));

        assertTrue(builder.hasNoKeys());
        MappingContextFactory<Object[]> mappingContextFactory = builder.build();
        assertTrue(mappingContextFactory instanceof ValuedMappingContextFactory);

        assertEquals("hh", mappingContextFactory.newContext().context(i));
    }

    @Test
    public void testKeys() throws Exception {
        builder.addKey(new KeyAndPredicate<Object[], SampleFieldKey>(new SampleFieldKey("k1", 0), null));

        assertFalse(builder.hasNoKeys());
        MappingContextFactory<Object[]> mappingContextFactory = builder.build();


        assertTrue(mappingContextFactory instanceof BreakDetectorMappingContextFactory);

        assertNull(mappingContextFactory.newContext().context(0));


        assertTrue(builder.nullChecker().test(new Object[] { null}));
        assertFalse(builder.nullChecker().test(new Object[] { 123 }));

    }

    @Test
    public void testKeysAndSuppliers() {
        int i = builder.addSupplier( new ConstantSupplier<String>("hh"));
        builder.addKey(new KeyAndPredicate<Object[], SampleFieldKey>(new SampleFieldKey("k1", 0), null));

        assertFalse(builder.hasNoKeys());
        MappingContextFactory<Object[]> mappingContextFactory = builder.build();
        assertTrue(mappingContextFactory instanceof BreakDetectorMappingContextFactory);

        assertEquals("hh", mappingContextFactory.newContext().context(i));
    }

    @Test
    public void testKeysWithSubBuilder() {
        builder.addKey(new KeyAndPredicate<Object[], SampleFieldKey>(new SampleFieldKey("k1", 0), null));
        MappingContextFactoryBuilder<Object[], SampleFieldKey> subBuilder = builder.newBuilder(Arrays.asList(new KeyAndPredicate<Object[], SampleFieldKey>(new SampleFieldKey("k2", 3), null)), null, null);

        subBuilder.newBuilder(Arrays.asList(new KeyAndPredicate<Object[], SampleFieldKey>(new SampleFieldKey("k3", 6), null)),  null,null);

        MappingContextFactory<Object[]> mappingContextFactory = builder.build();

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