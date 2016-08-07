package org.simpleflatmapper.converter;

import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.*;

public class ConvertingTypesTest {

    @Test
    public void testScoreExactMatch() {
        ConvertingTypes stringToDate = new ConvertingTypes(String.class, Date.class);
        ConvertingTypes dateToString  = new ConvertingTypes(Date.class, String.class);

        assertEquals(256 * 256 + 256, stringToDate.score(stringToDate));
        assertEquals(256 * 256 + 256, dateToString.score(dateToString));
        assertEquals(-1, stringToDate.score(dateToString));
        assertEquals(-1, dateToString.score(stringToDate));
    }


    @Test
    public void testTestScoreHierarchyFrom() {
        ConvertingTypes objectToByte = new ConvertingTypes(Object.class, Byte.class);
        ConvertingTypes numberToByte = new ConvertingTypes(Number.class, Byte.class);
        ConvertingTypes longToByte = new ConvertingTypes(Long.class, Byte.class);

        assertTrue(objectToByte.score(longToByte) > 0);
        assertTrue(numberToByte.score(longToByte) > 0);

        assertTrue(numberToByte.score(longToByte) < longToByte.score(longToByte));
        assertTrue(objectToByte.score(longToByte) < numberToByte.score(longToByte));
    }

    @Test
    public void testTestScoreHierarchyTo() {
        ConvertingTypes objectToNumber = new ConvertingTypes(Object.class, Number.class);
        ConvertingTypes objectToLong = new ConvertingTypes(Object.class, Long.class);

        ConvertingTypes objectToObject = new ConvertingTypes(Object.class, Object.class);

        assertTrue(objectToObject.score(objectToNumber) < 0);
        assertTrue(objectToNumber.score(objectToNumber) > 0);
        assertTrue(objectToLong.score(objectToNumber) > 0);

        assertTrue(objectToNumber.score(objectToNumber) > objectToNumber.score(objectToLong));
    }

    @Test
    public void testCharSequenceEnum() {
        ConvertingTypes csToEnum = new ConvertingTypes(CharSequence.class, Enum.class);
        ConvertingTypes stringToEnum = new ConvertingTypes(Object.class, Long.class);

    }
}