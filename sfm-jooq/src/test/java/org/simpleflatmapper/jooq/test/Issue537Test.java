package org.simpleflatmapper.jooq.test;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record;
import org.jooq.RecordMapper;
import org.jooq.RecordType;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;
import org.jooq.impl.DefaultConfiguration;
import org.junit.Test;
import org.simpleflatmapper.jooq.JooqFieldKey;
import org.simpleflatmapper.jooq.SfmRecordMapperProvider;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.test.jdbc.DbHelper;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import javax.money.MonetaryAmount;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class Issue537Test {

    @Test
    public void testMonetaryAmount() throws SQLException, NoSuchMethodException {

        ReflectionService reflectionService = ReflectionService.newInstance();
        reflectionService.registerClassMeta(MonetaryAmount.class, 
                reflectionService.getClassMetaExtraInstantiator(MonetaryAmount.class, Monetary.class.getDeclaredMethod("getDefaultAmountFactory")));
        reflectionService.registerClassMeta(CurrencyUnit.class,
                reflectionService.getClassMetaExtraInstantiator(CurrencyUnit.class, Monetary.class.getDeclaredMethod("getCurrency", String.class, String[].class)));
        
        SfmRecordMapperProvider recordMapperProvider = new SfmRecordMapperProvider(MapperConfig.<JooqFieldKey>fieldMapperConfig(), reflectionService);

        CurrencyUnit usd = Monetary.getCurrency("USD");

        RecordType<Record> rt = mock(RecordType.class);


        Field[] fields = new Field[] {
            createField("currencyAndAmount_number", Double.class),
            createField("currencyAndAmount_currency", String.class)
        };

        when(rt.size()).thenReturn(fields.length);
        when(rt.fields()).thenReturn(fields);

        RecordMapper<Record, Issue537> mapper = recordMapperProvider.provide(rt, Issue537.class);

        assertNotNull(mapper);
        
    }

    @Test
    public void testHsqlDb() throws SQLException, NoSuchMethodException {


        ReflectionService reflectionService = ReflectionService.disableAsm();
        reflectionService.registerClassMeta(MonetaryAmount.class,
                reflectionService.getClassMetaExtraInstantiator(MonetaryAmount.class, Monetary.class.getDeclaredMethod("getDefaultAmountFactory")));
        reflectionService.registerClassMeta(CurrencyUnit.class,
                reflectionService.getClassMetaExtraInstantiator(CurrencyUnit.class, Monetary.class.getDeclaredMethod("getCurrency", String.class, String[].class)));

        SfmRecordMapperProvider recordMapperProvider = new SfmRecordMapperProvider(MapperConfig.<JooqFieldKey>fieldMapperConfig(), reflectionService);
        Connection conn = DbHelper.objectDb();

        DSLContext dsl = DSL
                .using(new DefaultConfiguration().set(conn)
                        .set(SQLDialect.HSQLDB)
                        .set(recordMapperProvider));

        List<Issue537> list = dsl.select()
                .from("issue537").fetchInto(Issue537.class);

        assertEquals(1, list.size());

        Issue537 value = list.get(0);

        assertEquals(100, value.currencyAndAmount.getNumber().doubleValue(), 0.00001);
        assertEquals("USD", value.currencyAndAmount.getCurrency().getCurrencyCode());

    }

    

    public static class Issue537 {
        public final MonetaryAmount currencyAndAmount;

        public Issue537(MonetaryAmount currencyAndAmount) {
            this.currencyAndAmount = currencyAndAmount;
        }
    }

    public static Field createField(String name, Class<?> aClass) {

        Field field = mock(Field.class);
        when(field.getName()).thenReturn(name);
        when(field.getType()).thenReturn(aClass);
        return field;
    }

}
