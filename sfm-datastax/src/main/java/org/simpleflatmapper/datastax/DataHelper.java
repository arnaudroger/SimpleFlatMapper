package org.simpleflatmapper.datastax;

import com.datastax.driver.core.GettableByIndexData;
import com.datastax.driver.core.SettableByIndexData;

import java.lang.reflect.Method;
import java.util.Date;

/***
 * Bridge method to 3.1 api
 */
public class DataHelper {

    private static final Method setTimestampMethod;
    private static final Method getTimestampMethod;

    private static final Method setTimeMethod;
    private static final Method getTimeMethod;

    private static final Method setDateMethod;
    private static final Method getDateMethod;

    private static final Method setSmallIntMethod;
    private static final Method getSmallIntMethod;

    private static final Method setTinyIntMethod;
    private static final Method getTinyIntMethod;


    static {
        setTimestampMethod = getSetTimestampMethod();
        getTimestampMethod = getGetTimestampMethod();

        setTimeMethod = getSetTimeMethod();
        getTimeMethod = getGetTimeMethod();

        setDateMethod = getSetDateMethod();
        getDateMethod = getGetDateMethod();

        setSmallIntMethod = getSetSmallIntMethod();
        getSmallIntMethod = getGetSmallIntMethod();

        setTinyIntMethod = getSetTinyIntMethod();
        getTinyIntMethod = getGetTinyIntMethod();

    }

    private static Method getGetTinyIntMethod() {
        try {
            return GettableByIndexData.class.getMethod("getByte", int.class);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private static Method getSetTinyIntMethod() {
        try {
            return SettableByIndexData.class.getMethod("setByte", int.class, byte.class);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private static Method getGetSmallIntMethod() {
        try {
            return GettableByIndexData.class.getMethod("getShort", int.class);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private static Method getSetSmallIntMethod() {
        try {
            return SettableByIndexData.class.getMethod("setShort", int.class, short.class);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private static Method getGetDateMethod() {
        try {
            return GettableByIndexData.class.getMethod("getDate", int.class);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private static Method getSetDateMethod() {
        if (DataTypeHelper.localDateClass == null) return null;
        try {
            return SettableByIndexData.class.getMethod("setDate", int.class, DataTypeHelper.localDateClass);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }


    private static Method getGetTimeMethod() {
        try {
            return GettableByIndexData.class.getMethod("getTime", int.class);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private static Method getSetTimeMethod() {
        try {
            return SettableByIndexData.class.getMethod("setTime", int.class, long.class);
        } catch (NoSuchMethodException e) {
        }
        return null;
    }

    private static Method getGetTimestampMethod() {
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

    private static Method getSetTimestampMethod() {
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
        setTimestampMethod.invoke(data, index, date);
    }

    public static Date getTimestamp(int index, GettableByIndexData data) throws Exception {
        return (Date) getTimestampMethod.invoke(data, index);
    }

    public static void setTime(int index, long time, SettableByIndexData data) throws Exception {
        if (setTimeMethod == null) throw new UnsupportedOperationException();
        setTimeMethod.invoke(data, index, time);
    }

    public static long getTime(int index, GettableByIndexData data) throws Exception {
        if (getTimeMethod == null) throw new UnsupportedOperationException();
        return (Long) getTimeMethod.invoke(data, index);
    }

    public static void setDate(int index, Object localDate, SettableByIndexData data) throws Exception {
        if (setDateMethod == null) throw new UnsupportedOperationException();
        setDateMethod.invoke(data, index, localDate);
    }

    public static Object getDate(int index, GettableByIndexData data) throws Exception {
        if (getDateMethod == null) throw new UnsupportedOperationException();
        return getDateMethod.invoke(data, index);
    }

    public static void setShort(int index, short s, SettableByIndexData data) throws Exception {
        if (setSmallIntMethod == null) throw new UnsupportedOperationException();
        setSmallIntMethod.invoke(data, index, s);
    }

    public static short getShort(int index, GettableByIndexData data) throws Exception {
        if (getSmallIntMethod == null) throw new UnsupportedOperationException();
        return (Short)getSmallIntMethod.invoke(data, index);
    }

    public static void setByte(int index, byte b, SettableByIndexData data) throws Exception {
        if (setTinyIntMethod == null) throw new UnsupportedOperationException();
        setTinyIntMethod.invoke(data, index, b);
    }

    public static byte getByte(int index, GettableByIndexData data) throws Exception {
        if (getTinyIntMethod == null) throw new UnsupportedOperationException();
        return (Byte)getTinyIntMethod.invoke(data, index);
    }

    public static boolean hasShortAccessor() {
        return getSmallIntMethod != null;
    }

    public static boolean hasByteAccessor() {
        return getTinyIntMethod != null;
    }
}
