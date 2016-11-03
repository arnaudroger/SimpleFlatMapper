package org.simpleflatmapper.reflect.test.asm;

import org.junit.Test;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.asm.InjectedParam;
import org.simpleflatmapper.reflect.asm.InstantiatorKey;
import org.simpleflatmapper.reflect.primitive.IntGetter;
import org.simpleflatmapper.test.beans.DbFinalObject;
import org.simpleflatmapper.test.beans.DbObject;

import java.io.InputStream;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class InstantiatorKeyTest {

    @Test
    public void testEqualsOnSameKey() throws NoSuchMethodException {
        InstantiatorKey<Date> k = new InstantiatorKey<Date>(DbObject.class, Date.class);
        assertTrue(k.equals(k));
    }

    @Test
    public void testEqualsOnSameSourceAndTargetValues() throws NoSuchMethodException {
        InstantiatorKey<Date> k1 = new InstantiatorKey<Date>(DbObject.class, Date.class);
        InstantiatorKey<Date> k2 = new InstantiatorKey<Date>(DbObject.class, Date.class);
        assertTrue(k1.equals(k2));
    }

    @Test
    public void testEqualsOnSameSourceAndTargetAndInjectParamValues() throws NoSuchMethodException {
        final InjectedParam[] injectedParameters = createInjectedParameters("param", Getter.class);
        InstantiatorKey<Date> k1 = new InstantiatorKey<Date>(DbObject.class.getConstructor(), injectedParameters, Date.class);
        InstantiatorKey<Date> k2 = new InstantiatorKey<Date>(DbObject.class.getConstructor(), injectedParameters, Date.class);
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
        InstantiatorKey<Date> k1 = new InstantiatorKey<Date>(DbObject.class, Date.class);
        InstantiatorKey<InputStream> k2 = new InstantiatorKey<InputStream>(DbObject.class, InputStream.class);
        InstantiatorKey<Object> k3 = new InstantiatorKey<Object>(DbObject.class, null);
        assertFalse(k1.equals(k2));
        assertFalse(k1.equals(k3));
        assertFalse(k3.equals(k1));
    }

    @Test
    public void testNotEqualsOnSameSourceAndTargetAndDiffInjectParamValues() throws NoSuchMethodException {
        InstantiatorKey<Date> k1 = new InstantiatorKey<Date>(DbObject.class.getConstructor(), createInjectedParameters("param"), Date.class);
        InstantiatorKey<Date> k2 = new InstantiatorKey<Date>(DbObject.class.getConstructor(), createInjectedParameters("param2"), Date.class);
        InstantiatorKey<Date> k3 = new InstantiatorKey<Date>(DbObject.class.getConstructor(), null, Date.class);
        InstantiatorKey<Date> k4 = new InstantiatorKey<Date>(DbObject.class.getConstructor(), createInjectedParameters("param", IntGetter.class), Date.class);
        assertFalse(k1.equals(k2));
        assertFalse(k1.equals(k3));
        assertFalse(k3.equals(k1));
        assertFalse(k4.equals(k1));
    }

    @Test
    public void testNotEqualsDiffConstructor() throws NoSuchMethodException {
        InstantiatorKey<Date> k1 = new InstantiatorKey<Date>(DbObject.class.getConstructor(), createInjectedParameters("param"), Date.class);
        InstantiatorKey<Date> k2 = new InstantiatorKey<Date>(DbFinalObject.class.getDeclaredConstructors()[0], createInjectedParameters("param"), Date.class);
        InstantiatorKey<Date> k3 = new InstantiatorKey<Date>(null, createInjectedParameters( "param"), Date.class);
        assertFalse(k1.equals(k2));
        assertFalse(k1.equals(k3));
        assertFalse(k3.equals(k1));
    }

    @Test
    public void testNotEqualsOnNull() throws NoSuchMethodException {
        InstantiatorKey<Date> k1 = new InstantiatorKey<Date>(DbObject.class, Date.class);
        assertFalse(k1.equals(null));
    }

    @Test
    public void testNotEqualsOnDiffClass() throws NoSuchMethodException {
        InstantiatorKey<Date> k1 = new InstantiatorKey<Date>(DbObject.class, Date.class);
        assertFalse(k1.equals(new Object()));
    }

}