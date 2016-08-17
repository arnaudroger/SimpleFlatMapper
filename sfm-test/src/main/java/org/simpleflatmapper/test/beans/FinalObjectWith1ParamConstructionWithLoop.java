package org.simpleflatmapper.test.beans;

public class FinalObjectWith1ParamConstructionWithLoop {

    public final long id;
    public final O1P o1p;

    public FinalObjectWith1ParamConstructionWithLoop(long id, O1P o1p) {
        this.id = id;
        this.o1p = o1p;
    }

    public static class O1P {
        private final String value;

        public O1P(O1P value) {
            this.value = value.value;
        }

        public String getValue() {
            return value;
        }
    }

}
