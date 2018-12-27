package org.simpleflatmapper.util;

public enum ErrorDoc {

    /*
     * CS : Constant Source, ie ResultSet to T 
     * CT : Constant Target, ie T to PreparedStatement
     * FM : Field Mapper
     */
    CSFM_GETTER_NOT_FOUND, // could not found a way to get the data from the source 
    PROPERTY_NOT_FOUND, 
    CTFM_GETTER_NOT_FOUND, // could not found a way to get the data from the source T
    CTFM_SETTER_NOT_FOUND; // could not found a way to set the data to the target

    public static final String ERROR_URL = "https://github.com/arnaudroger/SimpleFlatMapper/wiki/Errors_";


    public String toUrl() {
        return ERROR_URL + name();
    }
}
