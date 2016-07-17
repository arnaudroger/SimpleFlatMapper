package org.sfm.datastax.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RecorderInvocationHandler implements InvocationHandler {

    private List<Invocation> invocations = new ArrayList<>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        invocations.add(new Invocation(method, args));
        Class<?> returnType = method.getReturnType();
        if (returnType.isPrimitive()) {
            if (short.class.equals(returnType)) {
                return (short)0;
            }
            if (int.class.equals(returnType)) {
                return 0;
            }
            if (long.class.equals(returnType)) {
                return 0l;
            }
            if (float.class.equals(returnType)) {
                return 0f;
            }
            if (double.class.equals(returnType)) {
                return 0d;
            }
            if (byte.class.equals(returnType)) {
                return (byte)0;
            }
            if (boolean.class.equals(returnType)) {
                return false;
            }
        }
        if (returnType.equals(BigDecimal.class)) {
            return BigDecimal.ONE;
        }
        if (returnType.equals(BigInteger.class)) {
            return BigInteger.ONE;
        }
        return null;
    }

    public void invokedOnce(String name, Object... args) {
        for(Invocation invocation : invocations) {
            if (invocation.method.getName().equals(name) && Arrays.equals(args, invocation.args)) {
                return;
            }
        }
        fail(" did not call " + name + " call " + invocations);
    }

    public void reset() {
        invocations.clear();
    }


    private static class Invocation {
        private final Method method;
        private final Object[] args;

        private Invocation(Method method, Object[] args) {
            this.method = method;
            this.args = args;
        }

        @Override
        public String toString() {
            return "Invocation{" +
                    "method=" + method +
                    ", args=" + Arrays.toString(args) +
                    '}';
        }
    }
}
