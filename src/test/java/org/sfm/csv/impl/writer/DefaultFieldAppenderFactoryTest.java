package org.sfm.csv.impl.writer;

import org.junit.Test;
import org.sfm.beans.DbObject;
import org.sfm.beans.DbPrimitiveObject;
import org.sfm.beans.DbPrimitiveObjectWithSetter;
import org.sfm.csv.CsvColumnKey;
import org.sfm.map.ColumnDefinition;
import org.sfm.map.FieldMapper;
import org.sfm.map.MappingContextFactory;
import org.sfm.map.column.FormatProperty;
import org.sfm.map.impl.FieldMapperColumnDefinition;
import org.sfm.map.impl.PropertyMapping;
import org.sfm.map.impl.context.KeySourceGetter;
import org.sfm.map.impl.context.MappingContextFactoryBuilder;
import org.sfm.reflect.ReflectionService;
import org.sfm.reflect.meta.ClassMeta;
import org.sfm.reflect.meta.DefaultPropertyNameMatcher;
import org.sfm.reflect.meta.PropertyMeta;

import java.sql.SQLException;
import java.text.DecimalFormat;

import static org.junit.Assert.*;

public class DefaultFieldAppenderFactoryTest {



    private DefaultFieldAppenderFactory defaultFieldAppenderFactory = DefaultFieldAppenderFactory.instance();

    private ClassMeta<DbObject> dbObjectClassMeta = ReflectionService.newInstance().getClassMeta(DbObject.class);
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

    @Test
    public void testBooleanAppender() throws Exception {
        testFieldMapperForClassAndProp("true", "pBoolean", dbPrimitiveObjectClassMeta);
    }
    @Test
    public void testByteAppender() throws Exception {
        testFieldMapperForClassAndProp("13", "pByte", dbPrimitiveObjectClassMeta);
    }
    @Test
    public void testCharAppender() throws Exception {
        testFieldMapperForClassAndProp("14", "pCharacter", dbPrimitiveObjectClassMeta);
    }
    @Test
    public void testShortAppender() throws Exception {
        testFieldMapperForClassAndProp("17", "pShort", dbPrimitiveObjectClassMeta);
    }
    @Test
    public void testFloatAppender() throws Exception {
        testFieldMapperForClassAndProp("3.1", "pFloat", dbPrimitiveObjectClassMeta);
    }
    @Test
    public void testIntegerAppender() throws Exception {
        testFieldMapperForClassAndProp("15", "pInt", dbPrimitiveObjectClassMeta);
    }
    @Test
    public void testDoubleAppender() throws Exception {
        testFieldMapperForClassAndProp("3.14", "pDouble", dbPrimitiveObjectClassMeta);
    }

    @Test
    public void testDoubleWithFormatterAppender() throws Exception {
        MappingContextFactoryBuilder<DbPrimitiveObjectWithSetter, CsvColumnKey> builder = getMappingContextBuilder();
        FieldMapperColumnDefinition<CsvColumnKey, DbPrimitiveObjectWithSetter> format = FieldMapperColumnDefinition.<CsvColumnKey, DbPrimitiveObjectWithSetter>identity().add(new FormatProperty(new DecimalFormat("0.0")));
        FieldMapper<DbPrimitiveObjectWithSetter, Appendable> fieldMapper =
                defaultFieldAppenderFactory.newFieldAppender(newPropertyMapping("pDouble", dbPrimitiveObjectClassMeta, format),
                        CsvCellWriter.DEFAULT_WRITER, builder);
        testFieldMapper("3.1", fieldMapper, dbPrimitiveObject, builder.newFactory());    }
    @Test
    public void testLongAppender() throws Exception {
        testFieldMapperForClassAndProp("16", "pLong", dbPrimitiveObjectClassMeta);
    }

    public void testFieldMapperForClassAndProp(String expected, String propName, ClassMeta<DbPrimitiveObjectWithSetter> classMeta) throws Exception {
        MappingContextFactoryBuilder<DbPrimitiveObjectWithSetter, CsvColumnKey> builder = getMappingContextBuilder();
        FieldMapper<DbPrimitiveObjectWithSetter, Appendable> fieldMapper = defaultFieldAppenderFactory.newFieldAppender(newPropertyMapping(propName, classMeta), CsvCellWriter.DEFAULT_WRITER, builder);
        testFieldMapper(expected, fieldMapper, dbPrimitiveObject, builder.newFactory());
    }

    private <T> void testFieldMapper(String expected, FieldMapper<T, Appendable> fieldMapper, T source, MappingContextFactory<T> dbObjectMappingContextFactory) throws Exception {
        StringBuilder sb = new StringBuilder();
        fieldMapper.mapTo(source, sb, dbObjectMappingContextFactory.newContext());
        assertEquals(expected, sb.toString());
    }

    private <T> PropertyMapping<T, String, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> newPropertyMapping(String col, ClassMeta<T> classMeta) {
        return newPropertyMapping(col, classMeta, FieldMapperColumnDefinition.<CsvColumnKey, T>identity());
    }

    private <T> PropertyMapping<T, String, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>> newPropertyMapping(String col, ClassMeta<T> classMeta, FieldMapperColumnDefinition<CsvColumnKey, T> columnDefinition) {
        PropertyMeta<T, String> propertyMeta = classMeta.newPropertyFinder().findProperty(DefaultPropertyNameMatcher.of(col));
        if (propertyMeta == null) throw new IllegalArgumentException("cannot find prop " + col);
        return new PropertyMapping<T, String, CsvColumnKey, FieldMapperColumnDefinition<CsvColumnKey, T>>(
                propertyMeta,
                new CsvColumnKey(col, 1),
                columnDefinition);
    }

    public <T> MappingContextFactoryBuilder<T, CsvColumnKey> getMappingContextBuilder() {
        return new MappingContextFactoryBuilder<T, CsvColumnKey>(new KeySourceGetter<CsvColumnKey, T>() {
            @Override
            public Object getValue(CsvColumnKey key, T source) throws SQLException {
                return null;
            }
        });
    }

}