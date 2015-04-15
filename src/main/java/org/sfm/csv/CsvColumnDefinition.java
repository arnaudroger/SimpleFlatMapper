package org.sfm.csv;

import org.sfm.csv.column.CustomReaderFactoryProperty;
import org.sfm.csv.column.CustomReaderProperty;
import org.sfm.map.column.ColumnDefinitionImpl;
import org.sfm.map.column.ColumnProperty;
import org.sfm.map.column.DateFormatProperty;
import org.sfm.map.column.TimeZoneProperty;
import org.sfm.reflect.meta.PropertyMeta;
import org.sfm.utils.Predicate;

import java.lang.reflect.Type;
import java.util.TimeZone;

public class CsvColumnDefinition extends ColumnDefinitionImpl<CsvColumnKey, CsvColumnDefinition> {

    public static final CsvColumnDefinition IDENTITY = new CsvColumnDefinition(new ColumnProperty[0]);

    protected CsvColumnDefinition(ColumnProperty[] properties) {
        super(properties);
    }

    @Override
    protected CsvColumnDefinition newColumnDefinition(ColumnProperty[] properties) {
        return new CsvColumnDefinition(properties);
    }

    public String dateFormat() {
        DateFormatProperty prop = lookFor(DateFormatProperty.class);
        if (prop != null) {
            return prop.getPattern();
        }
        return null;
    }


    public boolean hasCustomSource() {
        return has(CustomReaderProperty.class) ;
    }

    public Type getCustomSourceReturnType() {
        return lookFor(CustomReaderProperty.class).getReturnType();
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
            return prop.getTimeZone();
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

}
