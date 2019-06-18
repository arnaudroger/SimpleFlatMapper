package org.simpleflatmapper.jdbc.impl.getter;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.getter.ContextualGetter;
import org.simpleflatmapper.reflect.Getter;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

public final class CalendarResultSetGetter implements
        Getter<ResultSet, Calendar>,
        ContextualGetter<ResultSet, Calendar>
{
    private final Getter<ResultSet, ? extends Date> dateGetter;

    public CalendarResultSetGetter(Getter<ResultSet, ? extends Date> dateGetter) {
        this.dateGetter = dateGetter;
    }

    @Override
    public Calendar get(ResultSet target) throws Exception {
        Date d = dateGetter.get(target);
        if (d != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            return cal;
        }
        return null;
    }

    @Override
    public Calendar get(ResultSet resultSet, Context context) throws Exception {
        return get(resultSet);
    }

    @Override
    public String toString() {
        return "CalendarResultSetGetter{" +
                "dateGetter=" + dateGetter +
                '}';
    }
}
