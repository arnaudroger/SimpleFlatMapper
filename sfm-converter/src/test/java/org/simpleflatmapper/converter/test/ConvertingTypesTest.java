package org.simpleflatmapper.converter.test;

import org.junit.Test;
import org.simpleflatmapper.converter.ConvertingTypes;

import java.util.Date;

import static org.junit.Assert.*;

public class ConvertingTypesTest {

    @Test
    public void testScoreExactMatch() {
        ConvertingTypes stringToDate = new ConvertingTypes(String.class, Date.class);
        ConvertingTypes dateToString  = new ConvertingTypes(Date.class, String.class);

        assertEquals(256, stringToDate.score(stringToDate).getFromScore());
        assertEquals(256, stringToDate.score(stringToDate).getToScore());
        assertEquals(256 * 256 + 256, stringToDate.score(stringToDate).getScore());
        assertEquals(256 * 256 + 256, dateToString.score(dateToString).getScore());
        assertEquals(-1, stringToDate.score(dateToString).getScore());
        assertEquals(-1, dateToString.score(stringToDate).getScore());
    }


    @Test
    public void testTestScoreHierarchyFrom() {
        ConvertingTypes objectToByte = new ConvertingTypes(Object.class, Byte.class);
        ConvertingTypes numberToByte = new ConvertingTypes(Number.class, Byte.class);
        ConvertingTypes longToByte = new ConvertingTypes(Long.class, Byte.class);

        assertTrue(objectToByte.score(longToByte).getScore() > 0);
        assertTrue(numberToByte.score(longToByte).getScore() > 0);

        assertTrue(numberToByte.score(longToByte).getScore() < longToByte.score(longToByte).getScore());
        assertTrue(objectToByte.score(longToByte).getScore() < numberToByte.score(longToByte).getScore());
    }

    @Test
    public void testTestScoreHierarchyTo() {
        ConvertingTypes objectToNumber = new ConvertingTypes(Object.class, Number.class);
        ConvertingTypes objectToLong = new ConvertingTypes(Object.class, Long.class);

        ConvertingTypes objectToObject = new ConvertingTypes(Object.class, Object.class);

        assertTrue(objectToObject.score(objectToNumber).getScore() < 0);
        assertTrue(objectToNumber.score(objectToNumber).getScore() > 0);
        assertTrue(objectToLong.score(objectToNumber).getScore() > 0);

        assertTrue(objectToNumber.score(objectToNumber).getScore() > objectToNumber.score(objectToLong).getScore());
    }

    @Test
    public void testCharSequenceEnum() {
        ConvertingTypes csToEnum = new ConvertingTypes(CharSequence.class, Enum.class);
        ConvertingTypes stringToEnum = new ConvertingTypes(Object.class, Long.class);

    }
}