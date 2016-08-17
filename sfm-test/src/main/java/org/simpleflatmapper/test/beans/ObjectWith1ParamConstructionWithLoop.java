package org.simpleflatmapper.test.beans;

public class ObjectWith1ParamConstructionWithLoop {

    public long id;
    public O1P o1p;

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
