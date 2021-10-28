package org.simpleflatmapper.map;

/**
 * After mapping, new object will be instantiated. This validator is used
 * to check if the new object is valid. If it's not valid, null will be
 * set as mapping result.
 */
public interface MapResultValidator {

    boolean isValid();

}
