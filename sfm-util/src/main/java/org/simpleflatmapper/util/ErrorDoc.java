package org.simpleflatmapper.util;

public final class ErrorDoc {

    public static final String ERROR_URL = "https://github.com/arnaudroger/SimpleFlatMapper/wiki/Errors_";

    private ErrorDoc() {}

    public static String toUrl(String errorCode) {
        return ERROR_URL + errorCode;
    }
}
