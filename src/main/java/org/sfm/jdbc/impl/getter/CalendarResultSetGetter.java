package org.sfm.jdbc.impl.getter;

import org.sfm.reflect.Getter;

import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

public class CalendarResultSetGetter implements Getter<ResultSet, Calendar> {
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
}
