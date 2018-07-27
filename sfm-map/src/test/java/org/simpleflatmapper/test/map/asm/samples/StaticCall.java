package org.simpleflatmapper.test.map.asm.samples;

public class StaticCall {
    
    public Object builder(Object o) {
        return doBuild(o);
    }
    
    
    public static Object doBuild(Object o) {
        return 1;
    }
}
