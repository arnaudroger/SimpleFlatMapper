package org.simpleflatmapper.csv.test.writer;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.simpleflatmapper.lightningcsv.CsvCellWriter;
import org.simpleflatmapper.reflect.TypeAffinity;
import org.simpleflatmapper.reflect.meta.PropertyFinder;
import org.simpleflatmapper.test.beans.DbObject;
import org.simpleflatmapper.test.beans.DbPrimitiveObjectWithSetter;
import org.simpleflatmapper.csv.CsvColumnKey;
import org.simpleflatmapper.csv.mapper.FieldMapperToAppendableFactory;
import org.simpleflatmapper.map.FieldMapper;
import org.simpleflatmapper.map.context.MappingContextFactory;
import org.simpleflatmapper.map.property.DateFormatProperty;
import org.simpleflatmapper.map.property.FormatProperty;
import org.simpleflatmapper.map.property.FieldMapperColumnDefinition;
import org.simpleflatmapper.map.mapper.PropertyMapping;
import org.simpleflatmapper.map.context.KeySourceGetter;
import org.simpleflatmapper.map.context.MappingContextFactoryBuilder;
import org.simpleflatmapper.reflect.ReflectionService;
import org.simpleflatmapper.reflect.meta.ClassMeta;
import org.simpleflatmapper.reflect.meta.DefaultPropertyNameMatcher;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.ConstantPredicate;

import java.text.DecimalFormat;
import java.util.UUID;

import static org.junit.Assert.*;

public class FieldMapperToAppendableFactoryTest {

    private FieldMapperToAppendableFactory defaultFieldAppenderFactory = new FieldMapperToAppendableFactory(CsvCellWriter.DEFAULT_WRITER);

    private ClassMeta<DbPrimitiveObjectWithSetter> dbPrimitiveObjectClassMeta = ReflectionService.newInstance().getClassMeta(DbPrimitiveObjectWithSetter.class);

    DbObject dbObject = new DbObject();
    {
        dbObject.setId(2222);
    }
    DbPrimitiveObjectWithSetter dbPrimitiveObject = new DbPrimitiveObjectWithSetter();
    {
        dbPrimitiveObject.setpBoolean(true);
        dbPrimitiveObject.setpByte((byte) 13);
        dbPrimitiveObject.setpCharacter((char) 14);
        dbPrimitiveObject.setpDouble(3.14);
        dbPrimitiveObject.setpFloat((float) 3.1);
        dbPrimitiveObject.setpInt(15);
        dbPrimitiveObject.setpLong(16);
        dbPrimitiveObject.setpShort((short) 17);
    }


    static class JodaObject  {
        public DateTime dateTime;
        public LocalDate localDate;
        public LocalDateTime localDateTime;
        public LocalTime localTime;
    }

    static class UUIDObject {
        public UUID uuid;
    }

    JodaObject jodaObject = new JodaObject();

    {
        jodaObject.dateTime = ISODateTimeFormat.dateTime().parseDateTime("2014-06-07T15:04:06.008+02:00");
        jodaObject.localDate = jodaObject.dateTime.toLocalDate();
        jodaObject.localDateTime = jodaObject.dateTime.toLocalDateTime();
        jodaObject.localTime = jodaObject.dateTime.toLocalTime();
    }

    ClassMeta<JodaObject> jodaObjectClassMeta = ReflectionService.newInstance().getClassMeta(JodaObject.class);


    @Test
    public void testUUID() throws  Exception {
        UUID uuid = UUID.randomUUID();
        UUIDObject object = new UUIDObject();
        object.uuid = uuid;
        testFieldMapperForClassAndProp(uuid.toString(), "uuid",
                ReflectionService.newInstance().getClassMeta(UUIDObject.class), object);
    }

    @Test
    public void testJodaDateTime() throws  Exception {
        testFieldMapperForClassAndProp(jodaObject.dateTime.toString(), "dateTime", jodaObjectClassMeta, jodaObject);
    }

    @Test
    public void testJodaDateTimeWithFormater() throws  Exception {
        MappingContextFactoryBuilder<JodaObject, CsvColumnKey> builder = getMappingContextBuilder();
        FieldMapperColumnDefinition<CsvColumnKey> format = FieldMapperColumnDefinition.<CsvColumnKey>identity().add(DateTimeFormat.forPattern("yyyyMMdd"));
        FieldMapper<JodaObject, Appendable> fieldMapper =
                defaultFieldAppenderFactory.newFieldMapper(newPropertyMapping("dateTime", jodaObjectClassMeta, format),
                        builder, null);
        testFieldMapper("20140607", fieldMapper, jodaObject, builder.build());
    }

    @Test
    public void testJodaDateTimeWithDateFormat() throws  Exception {
        MappingContextFactoryBuilder<JodaObject, CsvColumnKey> builder = getMappingContextBuilder();
        FieldMapperColumnDefinition<CsvColumnKey> format = FieldMapperColumnDefinition.<CsvColumnKey>identity().add(new DateFormatProperty("yyyyMMdd"));
        FieldMapper<JodaObject, Appendable> fieldMapper =
                defaultFieldAppenderFactory.newFieldMapper(newPropertyMapping("dateTime", jodaObjectClassMeta, format),
                        builder, null);
        testFieldMapper("20140607", fieldMapper, jodaObject, builder.build());
    }


    @Test
    public void testBooleanAppender() throws Exception {
        testFieldMapperForClassAndPropPrimitives("true", "pBoolean");
    }
    @Test
    public void testByteAppender() throws Exception {
        testFieldMapperForClassAndPropPrimitives("13", "pByte");
    }
    @Test
    public void testCharAppender() throws Exception {
        testFieldMapperForClassAndPropPrimitives("14", "pCharacter");
    }
    @Test
    public void testShortAppender() throws Exception {
        testFieldMapperForClassAndPropPrimitives("17", "pShort");
    }
    @Test
    public void testFloatAppender() throws Exception {
        testFieldMapperForClassAndPropPrimitives("3.1", "pFloat");
    }
    @Test
    public void testIntegerAppender() throws Exception {
        testFieldMapperForClassAndPropPrimitives("15", "pInt");
    }
    @Test
    public void testDoubleAppender() throws Exception {
        testFieldMapperForClassAndPropPrimitives("3.14", "pDouble");
    }

    @Test
    public void testDoubleWithFormatterAppender() throws Exception {
        MappingContextFactoryBuilder<DbPrimitiveObjectWithSetter, CsvColumnKey> builder = getMappingContextBuilder();
        FieldMapperColumnDefinition<CsvColumnKey> format = FieldMapperColumnDefinition.<CsvColumnKey>identity().add(new FormatProperty(new DecimalFormat("0.0")));
        FieldMapper<DbPrimitiveObjectWithSetter, Appendable> fieldMapper =
                defaultFieldAppenderFactory.newFieldMapper(newPropertyMapping("pDouble", dbPrimitiveObjectClassMeta, format),
                        builder, null);
        testFieldMapper("3.1", fieldMapper, dbPrimitiveObject, builder.build());
    }
    @Test
    public void testLongAppender() throws Exception {
        testFieldMapperForClassAndPropPrimitives("16", "pLong");
    }
    public void testFieldMapperForClassAndPropPrimitives(String expected, String propName) throws Exception {
        testFieldMapperForClassAndProp(expected, propName, dbPrimitiveObjectClassMeta, dbPrimitiveObject);
    }
    public <T> void testFieldMapperForClassAndProp(String expected, String propName, ClassMeta<T> classMeta, T object) throws Exception {
        MappingContextFactoryBuilder<T, CsvColumnKey> builder = getMappingContextBuilder();
        FieldMapper<T, Appendable> fieldMapper = defaultFieldAppenderFactory.newFieldMapper(newPropertyMapping(propName, classMeta), builder, null);
        testFieldMapper(expected, fieldMapper, object, builder.build());
    }

    private <T> void testFieldMapper(String expected, FieldMapper<T, Appendable> fieldMapper, T source, MappingContextFactory<T> dbObjectMappingContextFactory) throws Exception {
        StringBuilder sb = new StringBuilder();
        fieldMapper.mapTo(source, sb, dbObjectMappingContextFactory.newContext());
        assertEquals(expected, sb.toString());
    }

    private <T> PropertyMapping<T, String, CsvColumnKey> newPropertyMapping(String col, ClassMeta<T> classMeta) {
        return newPropertyMapping(col, classMeta, FieldMapperColumnDefinition.<CsvColumnKey>identity());
    }

    private <T> PropertyMapping<T, String, CsvColumnKey> newPropertyMapping(String col, ClassMeta<T> classMeta, FieldMapperColumnDefinition<CsvColumnKey> columnDefinition) {
        PropertyMeta<T, String> propertyMeta = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of(col), new Object[0], (TypeAffinity)null, PropertyFinder.PropertyFilter.trueFilter());
        if (propertyMeta == null) throw new IllegalArgumentException("cannot find prop " + col);
        return new PropertyMapping<T, String, CsvColumnKey>(
                propertyMeta,
                new CsvColumnKey(col, 1),
                columnDefinition);
    }

    public <T> MappingContextFactoryBuilder<T, CsvColumnKey> getMappingContextBuilder() {
        return new MappingContextFactoryBuilder<T, CsvColumnKey>(new KeySourceGetter<CsvColumnKey, T>() {
            @Override
            public Object getValue(CsvColumnKey key, T source) throws Exception {
                return null;
            }
        }, true);
    }

}
