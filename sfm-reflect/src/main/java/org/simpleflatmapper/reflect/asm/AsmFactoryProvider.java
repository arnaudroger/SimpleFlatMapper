package org.simpleflatmapper.reflect.asm;

public interface AsmFactoryProvider {
    AsmFactory getAsmFactory(ClassLoader classLoader);
}
