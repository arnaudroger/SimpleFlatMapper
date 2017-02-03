package org.simpleflatmapper.reflect.test.asm;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.asm.BiInstantiatorKey;
import org.simpleflatmapper.reflect.asm.InjectedParam;
import org.simpleflatmapper.reflect.asm.InstantiatorKey;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;

import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class BiInstantiatorKeyTest {

    @Test
    public void testEqualsOnSameKey() throws NoSuchMethodException {
        BiInstantiatorKey k = new BiInstantiatorKey(DbObject.class, String.class, Date.class);
        assertTrue(k.equals(k));
    }

    @Test
    public void testEqualsOnSameSourceAndTargetValues() throws NoSuchMethodException {
        BiInstantiatorKey k1 = new BiInstantiatorKey(DbObject.class, String.class, Date.class);
        BiInstantiatorKey k2 = new BiInstantiatorKey(DbObject.class, String.class, Date.class);
        assertTrue(k1.equals(k2));
    }

    @Test
    public void testEqualsOnSameSourceAndTargetAndInjectParamValues() throws NoSuchMethodException {
        final InjectedParam[] injectedParameters = createInjectedParameters("param", Getter.class);
        BiInstantiatorKey k1 = new BiInstantiatorKey(DbObject.class.getConstructor(), injectedParameters, Date.class, String.class);
        BiInstantiatorKey k2 = new BiInstantiatorKey(DbObject.class.getConstructor(), injectedParameters, Date.class, String.class);
        assertTrue(k1.equals(k2));
    }
    private InjectedParam[] createInjectedParameters(String param) {
        return createInjectedParameters(param, Getter.class);
    }
    private InjectedParam[] createInjectedParameters(String param, Class<?> getterClass) {
        return new InjectedParam[] {new InjectedParam(param, getterClass)};
    }

    @Test
    public void testNotEqualsOnDiffSourceAndSameTargetValues() throws NoSuchMethodException {
        BiInstantiatorKey k1 = new BiInstantiatorKey(DbObject.class, Date.class, String.class);
        BiInstantiatorKey k2 = new BiInstantiatorKey(DbObject.class, InputStream.class, String.class);
        BiInstantiatorKey k3 = new BiInstantiatorKey(DbObject.class, null, String.class);
        BiInstantiatorKey k4 = new BiInstantiatorKey(DbObject.class, Date.class, Object.class);
        assertFalse(k1.equals(k2));
        assertFalse(k1.equals(k3));
        assertFalse(k3.equals(k1));
        assertFalse(k4.equals(k1));
    }

    @Test
    public void testNotEqualsOnSameSourceAndTargetAndDiffInjectParamValues() throws NoSuchMethodException {
        BiInstantiatorKey k1 = new BiInstantiatorKey(DbObject.class.getConstructor(), createInjectedParameters("param"), Date.class, String.class);
        BiInstantiatorKey k2 = new BiInstantiatorKey(DbObject.class.getConstructor(), createInjectedParameters("param2"), Date.class, String.class);
        BiInstantiatorKey k3 = new BiInstantiatorKey(DbObject.class.getConstructor(), null, Date.class, String.class);
        BiInstantiatorKey k4 = new BiInstantiatorKey(DbObject.class.getConstructor(), createInjectedParameters("param", IntGetter.class), Date.class, String.class);
        assertFalse(k1.equals(k2));
        assertFalse(k1.equals(k3));
        assertFalse(k3.equals(k1));
        assertFalse(k4.equals(k1));
    }

    @Test
    public void testNotEqualsDiffConstructor() throws NoSuchMethodException {
        BiInstantiatorKey k1 = new BiInstantiatorKey(DbObject.class.getConstructor(), createInjectedParameters("param"), Date.class, String.class);
        BiInstantiatorKey k2 = new BiInstantiatorKey(DbFinalObject.class.getDeclaredConstructors()[0], createInjectedParameters("param"), Date.class, String.class);
        BiInstantiatorKey k3 = new BiInstantiatorKey(null, createInjectedParameters( "param"), Date.class, String.class);
        assertFalse(k1.equals(k2));
        assertFalse(k1.equals(k3));
        assertFalse(k3.equals(k1));
    }

    @Test
    public void testNotEqualsOnNull() throws NoSuchMethodException {
        BiInstantiatorKey k1 = new BiInstantiatorKey(DbObject.class, Date.class, String.class);
        assertFalse(k1.equals(null));
    }

    @Test
    public void testNotEqualsOnDiffClass() throws NoSuchMethodException {
        BiInstantiatorKey k1 = new BiInstantiatorKey(DbObject.class, Date.class, String.class);
        assertFalse(k1.equals(new Object()));
    }

}