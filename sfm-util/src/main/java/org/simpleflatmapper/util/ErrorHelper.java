package org.simpleflatmapper.util;

public class ErrorHelper {

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void rethrowUnchecked(Throwable e) throws T {
        throw (T) e;
    }

    public static <T> T rethrow(Throwable e) {
        ErrorHelper.<RuntimeException>rethrowUnchecked(e);
        return null;
    }
}
