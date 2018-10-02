package org.simpleflatmapper.lightningcsv.test;

import org.junit.Test;
import org.simpleflatmapper.lightningcsv.impl.AsmUtils;
import org.simpleflatmapper.ow2asm.Opcodes;

import static org.junit.Assert.*;

public class AsmUtilsTest {
    @Test
    public void testAPI() {
        String version = System.getProperty("java.version");
        System.out.println("version = " + version);
        if (version.startsWith("1.6") || version.startsWith("1.7")) {
            assertEquals(Opcodes.ASM5, AsmUtils.API);
        } else {
            //IFJAVA8_START
            assertEquals(Opcodes.ASM7_EXPERIMENTAL, AsmUtils.API);
            if (true) return;
            //IFJAVA8_END
            fail(" fail " + version);
        }
    }
}