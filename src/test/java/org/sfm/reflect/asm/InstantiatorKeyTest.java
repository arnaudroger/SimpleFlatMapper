package org.sfm.reflect.asm;

import org.junit.Test;
import org.sfm.beans.DbObject;

import java.io.InputStream;
import java.sql.ResultSet;

import static org.junit.Assert.*;


public class InstantiatorKeyTest {

    @Test
    public void testEqualsOnSameKey() throws NoSuchMethodException {
        InstantiatorKey k = new InstantiatorKey(DbObject.class, ResultSet.class);
        assertTrue(k.equals(k));
    }

    @Test
    public void testEqualsOnSameSourceAndTargetValues() throws NoSuchMethodException {
        InstantiatorKey k1 = new InstantiatorKey(DbObject.class, ResultSet.class);
        InstantiatorKey k2 = new InstantiatorKey(DbObject.class, ResultSet.class);
        assertTrue(k1.equals(k2));
    }

    @Test
    public void testNotEqualsOnDiffSourceAndSameTargetValues() throws NoSuchMethodException {
        InstantiatorKey k1 = new InstantiatorKey(DbObject.class, ResultSet.class);
        InstantiatorKey k2 = new InstantiatorKey(DbObject.class, InputStream.class);
        InstantiatorKey k3 = new InstantiatorKey(DbObject.class, null);
        assertFalse(k1.equals(k2));
        assertFalse(k1.equals(k3));
        assertFalse(k3.equals(k1));
    }

    @Test
    public void testNotEqualsOnNull() throws NoSuchMethodException {
        InstantiatorKey k1 = new InstantiatorKey(DbObject.class, ResultSet.class);
        assertFalse(k1.equals(null));
    }

    @Test
    public void testNotEqualsOnDiffClass() throws NoSuchMethodException {
        InstantiatorKey k1 = new InstantiatorKey(DbObject.class, ResultSet.class);
        assertFalse(k1.equals(new Object()));
    }

}