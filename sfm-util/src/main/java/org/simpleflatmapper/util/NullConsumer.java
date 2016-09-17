package org.simpleflatmapper.util;

public class NullConsumer implements Consumer<Object> {

    public static final NullConsumer INSTANCE = new NullConsumer();

    private NullConsumer (){
    }
    @Override
    public void accept(Object o) {
    }
}
