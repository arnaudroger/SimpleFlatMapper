package org.simpleflatmapper.test.beans;

public class ObjectWith1ParamConstruction {

    public long id;
    public O1P o1p;
    public O2P o2p;

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
