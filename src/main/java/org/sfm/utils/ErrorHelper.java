package org.sfm.utils;

public class ErrorHelper {

    @SuppressWarnings("unchecked")
    private static <T extends Throwable> void rethrowUchecked(Throwable e) throws T {
        throw (T) e;
    }

    public static <T> T rethrow(Throwable e) {
        ErrorHelper.<RuntimeException>rethrowUchecked(e);
        return null;
    }
}
