package org.simpleflatmapper.test.beans;


public class TestAffinityObject {
    public SubObject fromString;
    public SubObject fromInt;


    public static class SubObject {


        public String str;
        public long i;

        public static SubObject valueOf(String str) {
            final SubObject subObject = new SubObject();
            subObject.str = str;
            return subObject;
        }
        public static SubObject valueOf(long i) {
            final SubObject subObject = new SubObject();
            subObject.i = i;
            return subObject;
        }
    }
}
