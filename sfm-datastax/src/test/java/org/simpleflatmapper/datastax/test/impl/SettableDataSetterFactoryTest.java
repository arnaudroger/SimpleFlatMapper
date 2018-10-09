package org.simpleflatmapper.datastax.test.impl;

import com.datastax.driver.core.*;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.ReadablePartial;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.simpleflatmapper.datastax.impl.SettableDataSetterFactory;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.datastax.DatastaxColumnKey;
import org.simpleflatmapper.map.MapperConfig;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.property.TimeZoneProperty;
import org.simpleflatmapper.converter.joda.JodaDateTimeZoneProperty;
//IFJAVA8_START
import org.simpleflatmapper.map.property.time.JavaZoneIdProperty;
//IFJAVA8_END
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.Setter;
import org.simpleflatmapper.util.TypeReference;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.reflect.primitive.DoubleSetter;
import org.simpleflatmapper.reflect.primitive.FloatSetter;
import org.simpleflatmapper.reflect.primitive.IntSetter;
import org.simpleflatmapper.reflect.primitive.LongSetter;
import org.simpleflatmapper.tuple.Tuple2;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;

//IFJAVA8_START
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
//IFJAVA8_END
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("unchecked")
public class SettableDataSetterFactoryTest {


    private final MapperConfig<DatastaxColumnKey, ?> mapperConfig = MapperConfig.<DatastaxColumnKey, Row>fieldMapperConfig();
    private final ReflectionService reflectionService = ReflectionService.newInstance();
    SettableDataSetterFactory factory = new SettableDataSetterFactory(mapperConfig, reflectionService);
    private int index;

    SettableData statement;

    @Before
    public void setUp() {
        statement = mock(BoundStatement.class);
        index = 0;
    }

    @Test
    public void testUDT() throws Exception {
        UserType ut = newDBObjectUserType();

        Setter<SettableByIndexData, DbObject> setter = factory.getSetter(newPM(DbObject.class, ut));

        DbObject object = DbObject.newInstance();

        setter.set(statement, object);

        ArgumentCaptor<UDTValue> argumentCaptor = ArgumentCaptor.forClass(UDTValue.class);
        verify(statement).setUDTValue(eq(0), argumentCaptor.capture());

        UDTValue value = argumentCaptor.getValue();

        assertUdtEqualsDbObject(value, object);

        setter.set(statement, null);
        verify(statement).setToNull(0);
    }

    protected void assertUdtEqualsDbObject(UDTValue value, DbObject object) {
        assertEquals(object.getId(), value.getLong(0));
        assertEquals(object.getName(), value.getString(1));
        assertEquals(object.getEmail(), value.getString(2));
        assertEquals(object.getCreationTime(), value.getTimestamp(3));
        assertEquals(object.getTypeName().name(), value.getString(4));
        assertEquals(object.getTypeOrdinal().ordinal(), value.getInt(5));
    }

    protected UserType newDBObjectUserType() throws Exception {
        UserType.Field id = newField("id", DataType.bigint());
        UserType.Field name = newField("name", DataType.text());
        UserType.Field email = newField("email", DataType.text());
        UserType.Field creationTime =  newField("creation_time", DataType.timestamp());
        UserType.Field typeName = newField("type_name", DataType.text());
        UserType.Field typeOrdinal = newField("type_ordinal", DataType.cint());


        return newUserType(id, name, email, creationTime, typeName, typeOrdinal);
    }

    private UserType newUserType(UserType.Field... fields) throws Exception {
        Constructor<?> constructor = UserType.class.getDeclaredConstructor(DataType.Name.class, String.class, String.class, boolean.class,ProtocolVersion.class, CodecRegistry.class, UserType.Field[].class, Map.class);
        constructor.setAccessible(true);
        return (UserType) constructor.newInstance(DataType.Name.UDT, "ks", "name", true, ProtocolVersion.V5, CodecRegistry.DEFAULT_INSTANCE, fields, null);

    }
    private UserType.Field newField(String name, DataType type) throws Exception {
        Constructor<?> constructor = UserType.Field.class.getDeclaredConstructor(String.class, DataType.class);
        constructor.setAccessible(true);
        return (UserType.Field) constructor.newInstance(name, type);
    }

    @Test
    public void testUDTValue() throws Exception {
        UDTValue bd = mock(UDTValue.class);
        UserType udtType = mock(UserType.class);

        Setter<SettableByIndexData, UDTValue> setter = factory.getSetter(newPM(UDTValue.class, udtType));
        setter.set(statement, bd);
        setter.set(statement, null);

        verify(statement).setUDTValue(0, bd);
        verify(statement).setToNull(0);
    }
    @Test
    public void testSet() throws Exception {
        Set<String> values = new HashSet<String>(Arrays.asList("v1", "v2"));
        Setter<SettableByIndexData, Set<String>> setter = factory.getSetter(newPM(new TypeReference<Set<String>>() {}.getType(), DataType.set(DataType.text())));

        setter.set(statement, values);
        setter.set(statement, null);

        verify(statement).setSet(0, values);
        verify(statement).setToNull(0);
    }

    @Test
    public void testSetOfTuple() throws Exception {
        TupleType ut = TupleType.of(ProtocolVersion.V3, CodecRegistry.DEFAULT_INSTANCE, DataType.text(), DataType.cint());

        Set<Tuple2<String, Integer>> values =
                new HashSet<Tuple2<String, Integer>>(
                        Arrays.asList(new Tuple2<String, Integer>("aa", 1),
                                new Tuple2<String, Integer>("bb", 2))) ;
        Setter<SettableByIndexData, Set<Tuple2<String, Integer>>> setter = factory.getSetter(newPM(new TypeReference<Set<Tuple2<String, Integer>>>() {}.getType(), DataType.set(ut)));

        setter.set(statement, values);
        setter.set(statement, null);

        ArgumentCaptor<Set> captor = ArgumentCaptor.forClass(Set.class);

        verify(statement).setSet(eq(0), captor.capture());

        Set value = captor.getValue();
        assertEquals(2, value.size());

        for(Object o : value) {
            TupleValue tv = (TupleValue) o;
            String str = tv.getString(0);
            int i = tv.getInt(1);
            switch (i) {
                case 1:
                    assertEquals("aa", str);
                    break;
                case 2:
                    assertEquals("bb", str);
                    break;
                default: fail();
            }
        }

        verify(statement).setToNull(0);
    }


    @Test
    public void testList() throws Exception {
        List<String> values = Arrays.asList("v1", "v2");
        Setter<SettableByIndexData, List<String>> setter = factory.getSetter(newPM(new TypeReference<List<String>>() {}.getType(), DataType.list(DataType.text())));

        setter.set(statement, values);
        setter.set(statement, null);

        verify(statement).setList(0, values);
        verify(statement).setToNull(0);
    }

    @Test
    public void testListOfUDT() throws Exception {
        UserType ut = newDBObjectUserType();

        List<DbObject> values = Arrays.asList(DbObject.newInstance(), DbObject.newInstance());
        Setter<SettableByIndexData, List<DbObject>> setter = factory.getSetter(newPM(new TypeReference<List<DbObject>>() {}.getType(), DataType.list(ut)));

        setter.set(statement, values);
        setter.set(statement, null);

        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);

        verify(statement).setList(eq(0), captor.capture());

        assertUdtEqualsDbObject((UDTValue) captor.getValue().get(0), values.get(0));
        assertUdtEqualsDbObject((UDTValue) captor.getValue().get(1), values.get(1));

        verify(statement).setToNull(0);
    }

    @Test
    public void testMap() throws Exception {
        DataType map = DataType.map(DataType.text(), DataType.text());

        Map<String, String> values = new HashMap<String, String>();
        values.put("aa", "bb");

        Setter<SettableByIndexData, Map<String, String>> setter = factory.getSetter(newPM(new TypeReference<Map<String, String>>() {}.getType(), map));

        setter.set(statement, values);
        setter.set(statement, null);

        verify(statement).setMap(0, values);
        verify(statement).setToNull(0);
    }

    @Test
    public void testMapWithConverter() throws Exception {
        TupleType ut = TupleType.of(ProtocolVersion.V3, CodecRegistry.DEFAULT_INSTANCE, DataType.text(), DataType.cint());

        DataType map = DataType.map(DataType.text(), ut);

        Map<Integer, Tuple2<String, Integer>> values = new HashMap<Integer, Tuple2<String, Integer>>();
        values.put(1, new Tuple2<String, Integer>("aa", 2));

        Setter<SettableByIndexData, Map<Integer, Tuple2<String, Integer>>> setter = factory.getSetter(newPM(new TypeReference<Map<Integer, Tuple2<String, Integer>>>() {}.getType(), map));

        setter.set(statement, values);
        setter.set(statement, null);

        ArgumentCaptor<Map> captor = ArgumentCaptor.forClass(Map.class);

        verify(statement).setMap(eq(0), captor.capture());

        Map value = captor.getValue();

        assertEquals(1, value.size());
        TupleValue tv = (TupleValue) value.get("1");
        assertEquals("aa", tv.getString(0));
        assertEquals(2, tv.getInt(1));

        verify(statement).setToNull(0);
    }



    @Test
    public void testTuple() throws Exception {
        TupleType tupleType = TupleType.of(ProtocolVersion.V3, CodecRegistry.DEFAULT_INSTANCE, DataType.text(), DataType.cint());
        TupleValue bd = tupleType.newValue("vvv", 15);

        Setter<SettableByIndexData, Tuple2> setter = factory.getSetter(newPM(new TypeReference<Tuple2<String, Integer>>() {}.getType(), tupleType));
        setter.set(statement, new Tuple2<String, Integer>("vvv", 15));
        setter.set(statement, null);

        verify(statement).setTupleValue(0, bd);
        verify(statement).setToNull(0);
    }

    @Test
    public void testTupleValue() throws Exception {
        TupleValue bd = mock(TupleValue.class);

        Setter<SettableByIndexData, TupleValue> setter = factory.getSetter(newPM(TupleValue.class, TupleType.of(ProtocolVersion.V3, CodecRegistry.DEFAULT_INSTANCE, DataType.text(), DataType.text())));
        setter.set(statement, bd);
        setter.set(statement, null);

        verify(statement).setTupleValue(0, bd);
        verify(statement).setToNull(0);
    }

    @Test
    public void testBigDecimal() throws Exception {
        BigDecimal bd = new BigDecimal("3.33");

        Setter<SettableByIndexData, BigDecimal> setter = factory.getSetter(newPM(BigDecimal.class, DataType.decimal()));
        setter.set(statement, bd);
        setter.set(statement, null);

        verify(statement).setDecimal(0, bd);
        verify(statement).setToNull(0);
    }

    @Test
    public void testBigInteger() throws Exception {
        BigInteger bi = new BigInteger("333");

        Setter<SettableByIndexData, BigInteger> setter = factory.getSetter(newPM(BigInteger.class, DataType.varint()));
        setter.set(statement, bi);
        setter.set(statement, null);

        verify(statement).setVarint(0, bi);
        verify(statement).setToNull(0);
    }

    @Test
    public void testInetAddress() throws Exception {
        InetAddress inetAddress = InetAddress.getByAddress(new byte[] {127, 0, 0, 1});

        Setter<SettableByIndexData, InetAddress> setter = factory.getSetter(newPM(InetAddress.class, DataType.inet()));
        setter.set(statement, inetAddress);
        setter.set(statement, null);

        verify(statement).setInet(0, inetAddress);
        verify(statement).setToNull(0);
    }



    @Test
    public void testUUID() throws Exception {
        UUID value = UUID.randomUUID();

        Setter<SettableByIndexData, UUID> setter = factory.getSetter(newPM(UUID.class, DataType.uuid()));
        setter.set(statement, value);
        setter.set(statement, null);

        verify(statement).setUUID(0, value);
        verify(statement).setToNull(0);
    }

    @Test
    public void testUUIDFromString() throws Exception {
        UUID value = UUID.randomUUID();

        Setter<SettableByIndexData, String> setter = factory.getSetter(newPM(String.class, DataType.uuid()));
        setter.set(statement, value.toString());
        setter.set(statement, null);

        verify(statement).setUUID(0, value);
        verify(statement).setToNull(0);
    }

    @Test
    public void testByteSetter() throws Exception {
        Setter<SettableByIndexData, Byte> setter = factory.getSetter(newPM(byte.class, DataType.cint()));

        setter.set(statement, (byte)3);
        setter.set(statement, null);

        verify(statement).setInt(0, 3);
        verify(statement).setToNull(0);
    }

    @Test
    public void testShortSetter() throws Exception {
        Setter<SettableByIndexData, Short> setter = factory.getSetter(newPM(short.class, DataType.cint()));

        setter.set(statement, (short)3);
        setter.set(statement, null);

        verify(statement).setInt(0, 3);
        verify(statement).setToNull(0);
    }

    @Test
    public void testFloatSetter() throws Exception {
        Setter<SettableByIndexData, Float> setter = factory.getSetter(newPM(float.class, DataType.cfloat()));
        assertTrue(setter instanceof FloatSetter);

        setter.set(statement, 3.0f);
        setter.set(statement, null);

        verify(statement).setFloat(0, 3.0f);
        verify(statement).setToNull(0);
    }

    @Test
    public void testDoubleSetter() throws Exception {
        Setter<SettableByIndexData, Double> setter = factory.getSetter(newPM(double.class, DataType.cdouble()));
        assertTrue(setter instanceof DoubleSetter);

        setter.set(statement, 3.0);
        setter.set(statement, null);

        verify(statement).setDouble(0, 3.0);
        verify(statement).setToNull(0);
    }

    @Test
    public void testIntSetter() throws Exception {
        Setter<SettableByIndexData, Integer> setter = factory.getSetter(newPM(int.class, DataType.cint()));
        assertTrue(setter instanceof IntSetter);

        setter.set(statement, 3);
        setter.set(statement, null);

        verify(statement).setInt(0, 3);
        verify(statement).setToNull(0);
    }

    @Test
    public void testIntSetterWithLongSource() throws Exception {
        Setter<SettableByIndexData, Long> setter = factory.getSetter(newPM(long.class, DataType.cint()));

        setter.set(statement, 3l);
        setter.set(statement, null);

        verify(statement).setInt(0, 3);
        verify(statement).setToNull(0);
    }

    @Test
    public void testLongSetterWithIntSource() throws Exception {
        Setter<SettableByIndexData, Integer> setter = factory.getSetter(newPM(int.class, DataType.bigint()));
        setter.set(statement, 3);
        setter.set(statement, null);

        verify(statement).setLong(0, 3l);
        verify(statement).setToNull(0);
    }

    @Test
    public void testLongSetter() throws Exception {
        Setter<SettableByIndexData, Long> setter = factory.getSetter(newPM(long.class, DataType.bigint()));
        assertTrue(setter instanceof LongSetter);

        setter.set(statement, 3l);
        setter.set(statement, null);

        verify(statement).setLong(0, 3l);
        verify(statement).setToNull(0);
    }


    @Test
    public void testStringSetter() throws Exception {
        Setter<SettableByIndexData, String> setter = factory.getSetter(newPM(String.class, DataType.text()));

        setter.set(statement, "str");
        setter.set(statement, null);

        verify(statement).setString(0, "str");
        verify(statement).setToNull(0);
    }

    @Test
    public void tesDate() throws Exception {
        Setter<SettableByIndexData, Date> setter = factory.getSetter(newPM(Date.class, DataType.timestamp()));

        Date date = new Date();

        setter.set(statement, date);
        setter.set(statement, null);

        verify(statement).setTimestamp(0, date);
        verify(statement).setToNull(0);
    }

    //IFJAVA8_START

    @Test
    public void testJava8TimeLDT() throws Exception {
        Setter<SettableByIndexData, LocalDateTime> setter = factory.getSetter(newPM(LocalDateTime.class, DataType.timestamp()));

        LocalDateTime ldt = LocalDateTime.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setTimestamp(0, Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant()));
        verify(statement).setToNull(0);


        final ZoneId zoneId = ZoneId.ofOffset("UTC", ZoneOffset.ofHours(2));
        Setter<SettableByIndexData, LocalDateTime> setterTz = factory.getSetter(newPM(LocalDateTime.class, DataType.timestamp(), new JavaZoneIdProperty(zoneId)));

        setterTz.set(statement, ldt);
        verify(statement).setTimestamp(1, Date.from(ldt.atZone(zoneId).toInstant()));

    }

    @Test
    public void testJava8TimeLD() throws Exception {
        Setter<SettableByIndexData, LocalDate> setter = factory.getSetter(newPM(LocalDate.class, DataType.date()));

        LocalDate ldt = LocalDate.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setDate(0, com.datastax.driver.core.LocalDate.fromYearMonthDay(ldt.getYear(), ldt.getMonthValue(), ldt.getDayOfMonth()));
        verify(statement).setToNull(0);
    }

    @Test
    public void testJava8TimeLT() throws Exception {
        Setter<SettableByIndexData, LocalTime> setter = factory.getSetter(newPM(LocalTime.class, DataType.time()));

        LocalTime ldt = LocalTime.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setTime(0, ldt.toNanoOfDay());
        verify(statement).setToNull(0);
    }

    @Test
    public void testJava8TimeODT() throws Exception {
        Setter<SettableByIndexData, OffsetDateTime> setter = factory.getSetter(newPM(OffsetDateTime.class, DataType.timestamp()));

        OffsetDateTime ldt = OffsetDateTime.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setTimestamp(0, Date.from(ldt.toInstant()));
        verify(statement).setToNull(0);
    }

    @Test
    public void testJava8TimeOT() throws Exception {
        Setter<SettableByIndexData, OffsetTime> setter = factory.getSetter(newPM(OffsetTime.class, DataType.time()));

        OffsetTime ldt = OffsetTime.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setTime(0, ldt.toLocalTime().toNanoOfDay());
        verify(statement).setToNull(0);
    }

    @Test
    public void testJava8TimeZDT() throws Exception {
        Setter<SettableByIndexData, ZonedDateTime> setter = factory.getSetter(newPM(ZonedDateTime.class, DataType.timestamp()));

        ZonedDateTime ldt = ZonedDateTime.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setTimestamp(0, Date.from(ldt.toInstant()));
        verify(statement).setToNull(0);
    }

    @Test
    public void testJava8TimeYearMonth() throws Exception {
        Setter<SettableByIndexData, YearMonth> setter = factory.getSetter(newPM(YearMonth.class, DataType.date()));

        YearMonth ldt = YearMonth.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setDate(0, com.datastax.driver.core.LocalDate.fromYearMonthDay(ldt.getYear(), ldt.getMonthValue(), 1));
        verify(statement).setToNull(0);
    }

    @Test
    public void testJava8TimeYear() throws Exception {
        Setter<SettableByIndexData, Year> setter = factory.getSetter(newPM(Year.class, DataType.date()));

        Year ldt = Year.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setDate(0, com.datastax.driver.core.LocalDate.fromYearMonthDay(ldt.getValue(), 1, 1));
        verify(statement).setToNull(0);
    }

    @Test
    public void testJava8TimeInstant() throws Exception {
        Setter<SettableByIndexData, Instant> setter = factory.getSetter(newPM(Instant.class, DataType.timestamp()));

        Instant ldt = Instant.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setTimestamp(0, Date.from(ldt));
        verify(statement).setToNull(0);
    }
    //IFJAVA8_END

    @Test
    public void testJodaLDT() throws Exception {
        long time = System.currentTimeMillis();
        testJodaDate(new org.joda.time.LocalDateTime(time), new Date(time));
    }

    private <T extends ReadablePartial> void testJodaDate(T joda, Date date) throws Exception {
        Setter<SettableByIndexData, T> setter = factory.getSetter(newPM(joda.getClass(), DataType.timestamp()));
        setter.set(statement, joda);
        verify(statement).setTimestamp(0, date);
        setter.set(statement, null);
        verify(statement).setToNull(0);

        DateTimeZone tz2 = getNonDefaultDateTimeZone(date);

        Setter<SettableByIndexData, T> setterTZ =
                factory.getSetter(newPM(joda.getClass(), DataType.timestamp(), new JodaDateTimeZoneProperty(tz2)));
        setterTZ.set(statement, joda);
        verify(statement).setTimestamp(1, new Date(date.getTime() + TimeUnit.HOURS.toMillis(2)));

        TimeZone jtz = tz2.toTimeZone();

        Setter<SettableByIndexData, T> setterJTZ =
                factory.getSetter(newPM(joda.getClass(), DataType.timestamp(), new TimeZoneProperty(jtz)));
        setterJTZ.set(statement, joda);
        verify(statement).setTimestamp(1, new Date(date.getTime() + TimeUnit.HOURS.toMillis(2)));

    }

    private DateTimeZone getNonDefaultDateTimeZone(Date date) {
        return DateTimeZone.forOffsetHours((int) (TimeUnit.MILLISECONDS.toHours(DateTimeZone.getDefault().getOffset(date.getTime())) - 2));
    }

    @Test
    public void testJodaDT() throws Exception {
        Setter<SettableByIndexData, org.joda.time.DateTime> setter = factory.getSetter(newPM(org.joda.time.DateTime.class, DataType.timestamp()));

        org.joda.time.DateTime ldt = org.joda.time.DateTime.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setTimestamp(0, ldt.toDate());
        verify(statement).setToNull(0);
    }

    @Test
    public void testJodaLD() throws Exception {
        PropertyMapping<?, ?, DatastaxColumnKey> pm = newPM(org.joda.time.LocalDate.class, DataType.date());
        Setter<SettableByIndexData, org.joda.time.LocalDate> setter = factory.getSetter(pm);

        org.joda.time.LocalDate ldt = org.joda.time.LocalDate.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setDate(0, com.datastax.driver.core.LocalDate.fromYearMonthDay(ldt.getYear(), ldt.getMonthOfYear(), ldt.getDayOfMonth()));
        verify(statement).setToNull(0);
    }

    @Test
    public void testJodaLT() throws Exception {
        PropertyMapping<?, ?, DatastaxColumnKey> pm = newPM(org.joda.time.LocalTime.class, DataType.time());
        Setter<SettableByIndexData, org.joda.time.LocalTime> setter = factory.getSetter(pm);

        org.joda.time.LocalTime ldt = org.joda.time.LocalTime.fromMillisOfDay(3600000);

        setter.set(statement, ldt);
        setter.set(statement, null);

        verify(statement).setTime(0, TimeUnit.SECONDS.toNanos(3600));
        verify(statement).setToNull(0);

        final Date date = ldt.toDateTimeToday().toDate();
        DateTimeZone tz2 = getNonDefaultDateTimeZone(date);
        DateTime dateTime = ldt.toDateTimeToday(tz2);
        final Date dateTz = dateTime.toDate();

        Setter<SettableByIndexData, org.joda.time.LocalTime> setterTZ =
                factory.getSetter(newPM(org.joda.time.LocalTime.class, DataType.timestamp(), new JodaDateTimeZoneProperty(tz2)));
        setterTZ.set(statement, ldt);
        verify(statement).setTimestamp(1, new Date(dateTz.getTime()));

    }


    @Test
    public void testJodaInstant() throws Exception {
        Setter<SettableByIndexData, org.joda.time.Instant> setter = factory.getSetter(newPM(org.joda.time.Instant.class, DataType.timestamp()));

        org.joda.time.Instant ldt = org.joda.time.Instant.now();

        setter.set(statement, ldt);
        setter.set(statement, null);

        final Date date = ldt.toDate();
        verify(statement).setTimestamp(0, date);
        verify(statement).setToNull(0);
    }

    @SuppressWarnings("unchecked")
    private <T, P> PropertyMapping<?, ?, DatastaxColumnKey> newPM(Type clazz, DataType datatype, Object... properties) {
        PropertyMeta<T, P> propertyMeta = mock(PropertyMeta.class);
        when(propertyMeta.getPropertyType()).thenReturn(clazz);
        return
                new PropertyMapping<T, P, DatastaxColumnKey>(
                        propertyMeta,
                        new DatastaxColumnKey("col", index++, datatype),
                        FieldMapperColumnDefinition.<DatastaxColumnKey>of(properties));
    }
}