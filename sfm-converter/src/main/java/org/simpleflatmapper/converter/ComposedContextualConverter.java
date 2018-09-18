package org.simpleflatmapper.converter;

public class ComposedContextualConverter<I, J, O> implements ContextualConverter<I, O> {

    public final ContextualConverter<? super I, ? extends J> c1;
    public final ContextualConverter<? super J, ? extends O> c2;

    public ComposedContextualConverter(ContextualConverter<? super I, ? extends J> c1, ContextualConverter<? super J, ? extends O> c2) {
        if (c1 == null || c2 == null)
            throw new NullPointerException();
        this.c1 = c1;
        this.c2 = c2;
    }


    @Override
    public O convert(I in, Context context) throws Exception {
        return c2.convert(c1.convert(in, context), context);
    }

    @Override
    public String toString() {
        return "ComposedConverter{" +
                "c1=" + c1 +
                ", c2=" + c2 +
                '}';
    }
    
    public int depth() {
        int i = 2;
        if (c1 instanceof ComposedContextualConverter) {
            i += ((ComposedContextualConverter) c1).depth();
        }
        if (c2 instanceof ComposedContextualConverter) {
            i += ((ComposedContextualConverter) c2).depth();
        }
        return i;
    }
}
