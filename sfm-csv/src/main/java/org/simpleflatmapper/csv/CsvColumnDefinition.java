package org.simpleflatmapper.csv;

import org.simpleflatmapper.csv.property.CustomReaderFactoryProperty;
import org.simpleflatmapper.csv.property.CustomReaderProperty;
import org.simpleflatmapper.lightningcsv.StringReader;
import org.simpleflatmapper.map.property.DefaultDateFormatProperty;
import org.simpleflatmapper.map.mapper.ColumnDefinition;
import org.simpleflatmapper.map.property.DateFormatProperty;
import org.simpleflatmapper.map.property.TimeZoneProperty;
import org.simpleflatmapper.reflect.meta.PropertyMeta;
import org.simpleflatmapper.util.Function;
import org.simpleflatmapper.util.Predicate;

import java.lang.reflect.Type;
import java.util.TimeZone;

public class CsvColumnDefinition extends ColumnDefinition<CsvColumnKey, CsvColumnDefinition> {

    public static final CsvColumnDefinition IDENTITY = new CsvColumnDefinition(new Object[0]);
    public static final Function<Object[], ColumnDefinition<CsvColumnKey, ?>> COLUMN_DEFINITION_FACTORY = new Function<Object[], ColumnDefinition<CsvColumnKey, ?>>() {
        @Override
        public ColumnDefinition<CsvColumnKey, ?> apply(Object[] objects) {
            return CsvColumnDefinition.of(objects);
        }
    };

    protected CsvColumnDefinition(Object[] properties) {
        super(properties);
    }


    @Override
    protected CsvColumnDefinition newColumnDefinition(Object[] properties) {
        return CsvColumnDefinition.of(properties);
    }

    public String[] dateFormats() {
        DateFormatProperty[] prop = lookForAll(DateFormatProperty.class);

        String[] patterns = new String[prop.length];
        for(int i = 0; i < prop.length; i++) {
            patterns[i] = prop[i].get();
        }

        if (patterns.length == 0) {
            DefaultDateFormatProperty defaultDateFormatProperty = lookFor(DefaultDateFormatProperty.class);

            if (defaultDateFormatProperty == null) {
                throw new IllegalStateException("No date format specified");
            }

            return new String[] { defaultDateFormatProperty.get() };
        }
        return patterns;
    }

    @Override
    public boolean hasCustomSourceFrom(Type ownerType) {
        return has(CustomReaderProperty.class);
    }

    @Override
    public Type getCustomSourceReturnTypeFrom(Type ownerType) {
        CustomReaderProperty customReaderProperty = lookFor(CustomReaderProperty.class);
        return customReaderProperty != null ? customReaderProperty.getReturnType() : null;
    }

    public CellValueReader<?> getCustomReader() {
        CustomReaderProperty prop = lookFor(CustomReaderProperty.class);
        if (prop != null) {
            return prop.getReader();
        }
        return null;
    }

    public CellValueReaderFactory getCustomCellValueReaderFactory() {
        CustomReaderFactoryProperty prop = lookFor(CustomReaderFactoryProperty.class);
        if (prop != null) {
            return prop.getReaderFactory();
        }
        return null;
    }

    public TimeZone getTimeZone(){
        TimeZoneProperty prop = lookFor(TimeZoneProperty.class);
        if (prop != null) {
            return prop.get();
        }
        return TimeZone.getDefault();
    }

    public boolean hasCustomReaderFactory(){
        return has(CustomReaderFactoryProperty.class);
    }



    public CsvColumnDefinition addDateFormat(String dateFormatDef) {
        return add(new DateFormatProperty(dateFormatDef));
    }

    public CsvColumnDefinition addTimeZone(TimeZone tz) {
        return add(new TimeZoneProperty(tz));
    }

    public CsvColumnDefinition addCustomReader(CellValueReader<?> cellValueReader) {
        return add(new CustomReaderProperty(cellValueReader));
    }

    public CsvColumnDefinition addCustomReader(StringReader<?> cellValueReader) {
        return add(new CustomReaderProperty(cellValueReader));
    }
    public CsvColumnDefinition addCustomCellValueReaderFactory(CellValueReaderFactory cellValueReaderFactory) {
        return add(new CustomReaderFactoryProperty(cellValueReaderFactory));
    }

    public static CsvColumnDefinition identity() {
        return IDENTITY;
    }

    public static CsvColumnDefinition renameDefinition(final String name) {
        return identity().addRename(name);
    }

    public static CsvColumnDefinition dateFormatDefinition(final String dateFormatDef) {
        return identity().addDateFormat(dateFormatDef);
    }

    public static CsvColumnDefinition customReaderDefinition(final CellValueReader<?> cellValueReader) {
        return identity().addCustomReader(cellValueReader);
    }

    public static CsvColumnDefinition customReaderDefinition(final StringReader<?> cellValueReader) {
        return identity().addCustomReader(cellValueReader);
    }

    public static CsvColumnDefinition timeZoneDefinition(final TimeZone timeZone) {
        return identity().addTimeZone(timeZone);
    }

    public static CsvColumnDefinition ignoreDefinition() {
        return identity().addIgnore();
    }

    public static CsvColumnDefinition customCellValueReaderFactoryDefinition(final CellValueReaderFactory cellValueReaderFactory) {
        return identity().addCustomCellValueReaderFactory(cellValueReaderFactory);
    }

    public static CsvColumnDefinition key() {
        return identity().addKey();
    }
    public static CsvColumnDefinition key(Predicate<PropertyMeta<?, ?>> appliesTo) {
        return identity().addKey(appliesTo);
    }

    public static CsvColumnDefinition compose(final CsvColumnDefinition def1, final CsvColumnDefinition def2) {
        return def1.compose(def2);
    }

    public static CsvColumnDefinition of(Object... properties) {
        return new CsvColumnDefinition(properties);
    }
}
