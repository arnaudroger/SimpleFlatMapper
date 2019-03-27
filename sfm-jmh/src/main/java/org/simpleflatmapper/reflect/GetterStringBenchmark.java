/*
 * Copyright (c) 2005, 2014, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package org.simpleflatmapper.reflect;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 Benchmark                                        Mode  Samples          Score  Score error  Units
 o.s.r.GetterBenchmark.testAsm                   thrpt       20  363012639.635  2849038.594  ops/s
 o.s.r.GetterBenchmark.testDirect                thrpt       20  363985791.551  2517616.568  ops/s
 o.s.r.GetterBenchmark.testField                 thrpt       20  210419538.610  1767008.382  ops/s
 o.s.r.GetterBenchmark.testMethod                thrpt       20  225759720.315  1741949.061  ops/s
 o.s.r.GetterBenchmark.testMethodHandler         thrpt       20  220393009.707  1794715.420  ops/s
 o.s.r.GetterBenchmark.testMethodHandlerExact    thrpt       20  216226842.084  6924124.473  ops/s



 */
@State(Scope.Benchmark)
public class GetterStringBenchmark {


    public static class StringBean {
        public String value;

        public StringBean(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static final class DirectGetter implements Getter<StringBean, String> {
        @Override
        public String get(StringBean stringBean) throws Exception {
            return stringBean.getValue();
        }
    }

    private static final class MethodHandlerGetter implements  GetterThrowable<StringBean, String> {
        private final MethodHandle methodHandle;

        private MethodHandlerGetter(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public String get(StringBean stringBean) throws Throwable {
            return (String) methodHandle.invoke(stringBean);
        }
    }

    private static final class MethodHandlerExactGetter implements  GetterThrowable<StringBean, String> {
        private final MethodHandle methodHandle;

        private MethodHandlerExactGetter(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public String get(StringBean stringBean) throws Throwable {
            return (String) methodHandle.invokeExact(stringBean);
        }
    }

    private static final Getter<StringBean, String> DIRECT_GETTER = new DirectGetter();
    private static final Getter<StringBean, String> METHOD_GETTER;
    private static final Getter<StringBean, String> FIELD_GETTER;
    private static final Getter<StringBean, String> ASM_GETTER;
    private static final GetterThrowable<StringBean, String> MH_GETTER;
    private static final GetterThrowable<StringBean, String> MHE_GETTER;
    private static final GetterThrowable<StringBean, String> MHF_GETTER;
    static  {
        try {
            final Method getValue = StringBean.class.getMethod("getValue");
            final Field value = StringBean.class.getField("value");
            METHOD_GETTER = new ObjectGetterFactory(null).getMethodGetter(getValue);
            FIELD_GETTER = new ObjectGetterFactory(null).getFieldGetter(value);
            ASM_GETTER = new ObjectGetterFactory(new AsmFactoryProvider() {
                AsmFactory asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());
                @Override
                public AsmFactory getAsmFactory(ClassLoader classLoader) {
                    return asmFactory;
                }
            }).getMethodGetter(getValue);
            MH_GETTER = new MethodHandlerGetter(MethodHandles.lookup().unreflect(getValue));
            MHE_GETTER = new MethodHandlerExactGetter(MethodHandles.lookup().unreflect(getValue));
            MHF_GETTER = new MethodHandlerGetter(MethodHandles.lookup().unreflectGetter(value));
        } catch(Exception e) {
            throw new Error(e.getMessage(), e);
        }
    }

    private StringBean stringBean = new StringBean("value");

    @Benchmark
    public String testMethodHandlerExact() throws Throwable {
        return MHE_GETTER.get(stringBean);
    }
    @Benchmark
    public String testMethodHandler() throws Throwable {
        return MH_GETTER.get(stringBean);
    }
    @Benchmark
    public String testMethodHandlerField() throws Throwable {
        return MHF_GETTER.get(stringBean);
    }
    @Benchmark
    public String testMethod() throws Throwable {
        return METHOD_GETTER.get(stringBean);
    }
    @Benchmark
    public String testField() throws Throwable {
        return FIELD_GETTER.get(stringBean);
    }
    @Benchmark
    public String testAsm() throws Throwable {
        return ASM_GETTER.get(stringBean);
    }

    @Benchmark
    public String testDirect() throws Throwable {
        return DIRECT_GETTER.get(stringBean);
    }

    /**
     * Interface representing a Getter of a property of type P on a object of type T.
     * <p>
     * use {@link ObjectGetterFactory} to instantiate.
     * @see ObjectGetterFactory
     * @see org.simpleflatmapper.reflect.getter.MethodGetter
     * @see org.simpleflatmapper.reflect.getter.FieldGetter
     * @param <T> the targeted type
     * @param <P> the property type
     */
    public interface GetterThrowable<T, P> {
        /**
         * Return the property from the specified object.
         * @param target the object to get the property from
         * @return the property
         * @throws Exception if anything goes wrong
         */
        P get(T target) throws Throwable;
    }



}
