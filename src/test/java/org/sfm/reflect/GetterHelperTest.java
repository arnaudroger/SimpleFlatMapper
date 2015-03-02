package org.sfm.reflect;

import org.junit.Test;

import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class GetterHelperTest {
    @Test
    public void testFieldModifiersMatchers() {
        assertTrue(GetterHelper.fieldModifiersMatches(Modifier.PUBLIC));
        assertFalse(GetterHelper.fieldModifiersMatches(Modifier.FINAL| Modifier.PUBLIC));
        assertFalse(GetterHelper.fieldModifiersMatches(Modifier.STATIC| Modifier.PUBLIC));
    }
    @Test
    public void testMethodModifiersMatchers() {
        assertTrue(GetterHelper.methodModifiersMatches(Modifier.PUBLIC));
        assertTrue(GetterHelper.methodModifiersMatches(Modifier.FINAL | Modifier.PUBLIC));
        assertFalse(GetterHelper.methodModifiersMatches(Modifier.STATIC | Modifier.PUBLIC));
    }

    @Test
    public void testGetPropertyNameFromMethodName() {
        assertEquals("name", GetterHelper.getPropertyNameFromMethodName("getName"));
        assertEquals("name", GetterHelper.getPropertyNameFromMethodName("isName"));
    }


}
