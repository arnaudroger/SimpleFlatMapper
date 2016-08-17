package org.simpleflatmapper.test.beans;

public class FinalObjectWith1ParamConstruction {

    public final long id;
    public final O1P o1p;
    public final O2P o2p;

    public FinalObjectWith1ParamConstruction(long id, O1P o1p, O2P o2p) {
        this.id = id;
        this.o1p = o1p;
        this.o2p = o2p;
    }

    public static class O1P {
        private final String value;

        public O1P(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    public static class O2P {
        private final O1P o1p;

        public O2P(O1P o1p) {
            this.o1p = o1p;
        }

        public O1P getO1p() {
            return o1p;
        }
    }
}
