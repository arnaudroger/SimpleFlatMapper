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
import org.simpleflatmapper.reflect.primitive.IntGetter;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *
 Benchmark                                        Mode  Samples          Score  Score error  Units
 o.s.r.GetterIntBenchmark.testAsm                   thrpt       20  1011880392.054  11381047.993  ops/s
 o.s.r.GetterIntBenchmark.testDirect                thrpt       20   996770398.348   7380912.381  ops/s
 o.s.r.GetterIntBenchmark.testField                 thrpt       20   170318384.194   2171748.235  ops/s
 o.s.r.GetterIntBenchmark.testMethod                thrpt       20   115383691.949   1672164.717  ops/s
 o.s.r.GetterIntBenchmark.testMethodHandler         thrpt       20   219963745.595   2187439.290  ops/s
 o.s.r.GetterIntBenchmark.testMethodHandlerExact    thrpt       20   225040386.828  12770026.817  ops/s
 o.s.r.GetterIntBenchmark.testMethodHandlerField    thrpt       20   236349757.697   3087164.306  ops/s

 */
@State(Scope.Benchmark)
public class GetterIntBenchmark {


    public static class IntBean {
        public int value;

        public IntBean(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    private static final class DirectGetter implements IntGetter<IntBean> {
        @Override
        public int getInt(IntBean intBean) throws Exception {
            return intBean.getValue();
        }
    }

    private static final class MethodHandlerGetter implements  GetterThrowable<IntBean> {
        private final MethodHandle methodHandle;

        private MethodHandlerGetter(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public int get(IntBean intBean) throws Throwable {
            return (int) methodHandle.invoke(intBean);
        }
    }

    private static final class MethodHandlerExactGetter implements  GetterThrowable<IntBean> {
        private final MethodHandle methodHandle;

        private MethodHandlerExactGetter(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public int get(IntBean intBean) throws Throwable {
            return (int) methodHandle.invokeExact(intBean);
        }
    }

    private static final IntGetter<IntBean> DIRECT_GETTER = new DirectGetter();
    private static final IntGetter<IntBean> METHOD_GETTER;
    private static final IntGetter<IntBean> FIELD_GETTER;
    private static final IntGetter<IntBean> ASM_GETTER;
    private static final GetterThrowable<IntBean> MH_GETTER;
    private static final GetterThrowable<IntBean> MHE_GETTER;
    private static final GetterThrowable<IntBean> MHF_GETTER;
    static  {
        try {
            final Method getValue = IntBean.class.getMethod("getValue");
            final Field value = IntBean.class.getField("value");
            METHOD_GETTER = ObjectGetterFactory.toIntGetter(new ObjectGetterFactory(null).getMethodGetter(getValue));
            FIELD_GETTER = ObjectGetterFactory.toIntGetter(new ObjectGetterFactory(null).getFieldGetter(value));
            ASM_GETTER = ObjectGetterFactory.toIntGetter(new ObjectGetterFactory(new AsmFactoryProvider() {
                AsmFactory asmFactory = new AsmFactory(Thread.currentThread().getContextClassLoader());
                @Override
                public AsmFactory getAsmFactory(ClassLoader classLoader) {
                    return asmFactory;
                }
            }).getMethodGetter(getValue));
            MH_GETTER = new MethodHandlerGetter(MethodHandles.lookup().unreflect(getValue));
            MHE_GETTER = new MethodHandlerExactGetter(MethodHandles.lookup().unreflect(getValue));
            MHF_GETTER = new MethodHandlerGetter(MethodHandles.lookup().unreflectGetter(value));
        } catch(Exception e) {
            throw new Error(e.getMessage(), e);
        }
    }

    private IntBean intBean = new IntBean(5);

    @Benchmark
    public int testMethodHandlerExact() throws Throwable {
        return MHE_GETTER.get(intBean);
    }
    @Benchmark
    public int testMethodHandler() throws Throwable {
        return MH_GETTER.get(intBean);
    }
    @Benchmark
    public int testMethodHandlerField() throws Throwable {
        return MHF_GETTER.get(intBean);
    }
    @Benchmark
    public int testMethod() throws Throwable {
        return METHOD_GETTER.getInt(intBean);
    }
    @Benchmark
    public int testField() throws Throwable {
        return FIELD_GETTER.getInt(intBean);
    }
    @Benchmark
    public int testAsm() throws Throwable {
        return ASM_GETTER.getInt(intBean);
    }

    @Benchmark
    public int testDirect() throws Throwable {
        return DIRECT_GETTER.getInt(intBean);
    }

    public interface GetterThrowable<T> {
        int get(T target) throws Throwable;
    }

}
