package org.simpleflatmapper.converter.impl.time;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.converter.ContextualConverter;
import org.simpleflatmapper.util.ErrorHelper;
import org.simpleflatmapper.util.Function;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


public class ObjectToJavaZonedDateTimeConverter implements ContextualConverter<Object, ZonedDateTime> {

    private final ZoneId zone;

    public ObjectToJavaZonedDateTimeConverter(ZoneId zoneId) {
        this.zone = zoneId;
    }

    @Override
    public ZonedDateTime convert(Object o, Context context) throws Exception {
        if (o == null) {
            return null;
        }

        if (o instanceof Date) {
            return Instant.ofEpochMilli(((Date) o).getTime()).atZone(zone);
        }

        if (o instanceof Instant) {
            return((Instant)o).atZone(zone);
        }

        if (o instanceof ZonedDateTime) {
            return (ZonedDateTime) o;
        }

        if (o instanceof LocalDateTime) {
            return ((LocalDateTime)o).atZone(zone);
        }

        if (o instanceof TemporalAccessor) {
            return ZonedDateTime.from((TemporalAccessor) o).withZoneSameLocal(zone);
        }

        Instant i = getWithCustomAccessor(o);
        if (i != null)
            return i.atZone(zone);

        throw new IllegalArgumentException("Cannot convert " + o + " to ZonedDateTime");
    }

    /*
    To deal with oracle special handling of TIMESTAMPTZ
    that are returned when calling getObject
    will just look for a method returning j.s.Timestamp or j.u.Date
     */

    private static final Function<Object, Instant> failingAccessor = new Function<Object, Instant>() {
        @Override
        public Instant apply(Object o) {
            return null;
        }
    };

    private static final ConcurrentHashMap<Class<?>, Function<Object, Instant>> customAccessors =
            new ConcurrentHashMap<Class<?>, Function<Object, Instant>>();

    public static Instant getWithCustomAccessor(Object o) {
        Function<Object, Instant> objectDateFunction = customAccessors.computeIfAbsent(o.getClass(), new java.util.function.Function<Class<?>, Function<Object, Instant>>() {
            @Override
            public Function<Object, Instant> apply(Class<?> aClass) {
                return calculateCustomAccessor(aClass);
            }
        });

        return objectDateFunction.apply(o);
    }

    private static Function<Object, Instant> calculateCustomAccessor(Class<?> aClass) {

        Method[] methods = aClass.getMethods();
        for(int i = 0; i < methods.length; i++) {
            Method m = methods[i];
            int mod = m.getModifiers();
            int parameterCount = getParameterCount(m);
            if (!Modifier.isStatic(mod) && Modifier.isPublic(mod) && parameterCount == 0
                    && ("java.sql.Timestamp".equals(m.getReturnType().getName())
                    || "java.util.Date".equals(m.getReturnType().getName()))
            ) {
                return new MethodAccessor(m);
            }
        }

        return failingAccessor;
    }

    private static int getParameterCount(Method m) {
        return m.getParameterTypes().length;
    }

    private static class MethodAccessor implements Function<Object, Instant> {
        private final Method m;

        public MethodAccessor(Method m) {
            this.m = m;
        }

        @Override
        public Instant apply(Object o) {
            try {
                return Instant.ofEpochMilli(((Date) m.invoke(o)).getTime());
            } catch (Exception e) {
                return ErrorHelper.rethrow(e);
            }
        }
    }
}
