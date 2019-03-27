package org.simpleflatmapper.reflect.test;

import org.simpleflatmapper.reflect.asm.AsmFactory;
import org.simpleflatmapper.reflect.asm.AsmFactoryProvider;

public class Utils {

    public static final AsmFactoryProvider TEST_ASM_FACTORY_PROVIDER = new AsmFactoryProvider() {
        @Override
        public AsmFactory getAsmFactory(ClassLoader classLoader) {
            return new AsmFactory(Thread.currentThread().getContextClassLoader());
        }
    };
}
