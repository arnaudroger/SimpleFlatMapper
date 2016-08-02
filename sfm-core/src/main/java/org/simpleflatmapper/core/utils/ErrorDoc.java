package org.simpleflatmapper.core.utils;

public class ErrorDoc {
    public static String toUrl(String errorCode) {
        return "https://github.com/arnaudroger/SimpleFlatMapper/wiki/Errors_" + errorCode;
    }
}
