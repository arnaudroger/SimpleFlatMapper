package org.sfm.datastax;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.SettableByIndexData;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

public class DataHelper {

    private static final Method setDateMethod;
    private static final Method getDateMethod;

    static {
        setDateMethod = getSetDateMethod();
        getDateMethod = getGetDateMethod();
    }

    private static Method getGetDateMethod() {
        try {
            return GettableByIndexData.class.getMethod("getTimestamp", int.class);
        } catch (NoSuchMethodException e) {
            try {
                return GettableByIndexData.class.getMethod("getDate", int.class);
            } catch (NoSuchMethodException e1) {
                throw new IllegalStateException("Unable to lookup getDate or getTimestamp on " + GettableByIndexData.class);
            }
        }
    }

    private static Method getSetDateMethod() {
        try {
            return SettableByIndexData.class.getMethod("setTimestamp", int.class, Date.class);
        } catch (NoSuchMethodException e) {
            try {
                return SettableByIndexData.class.getMethod("setDate", int.class, Date.class);
            } catch (NoSuchMethodException e1) {
                throw new IllegalStateException("Unable to lookup setDate or setTimestamp on " + SettableByIndexData.class);
            }
        }
    }

    public static void setTimestamp(int index, Date date, SettableByIndexData data) throws Exception {
        setDateMethod.invoke(data, index, date);
    }

    public static Date getTimestamp(int index, GettableByIndexData data) throws Exception {
        return (Date)getDateMethod.invoke(data, index);
    }
}
