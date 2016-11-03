package org.simpleflatmapper.datastax.test.utils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.fail;

public class RecorderInvocationHandler implements InvocationHandler {

    private List<Invocation> invocations = new ArrayList<Invocation>();

    private List<Expectation> expectations = new ArrayList<Expectation>();

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Invocation e = new Invocation(method, args);
        invocations.add(e);

        for(Expectation t2 : expectations) {
            if (t2.matches(e)) {
                return t2.result();
            }
        }

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

    public void when(String name, Object... args) {
        expectations.add(new Expectation(name, Arrays.copyOf(args, args.length - 1), args[args.length - 1]));
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

    private class Expectation {

        private final String name;
        private final Object[] args;
        private final Object result;

        private Expectation(String name, Object[] args, Object result) {
            this.name = name;
            this.args = args;
            this.result = result;
        }

        public boolean matches(Invocation e) {
            return
                    e.method.getName().equals(name)
                    && Arrays.equals(e.args, args);
        }

        public Object result() {
            return result;
        }
    }
}
