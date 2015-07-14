package org.sfm.reflect;


public class GetterOnGetter<O, I, P> implements Getter<O, P> {
    private final Getter<I, P> g2;
    private final Getter<O, I> g1;

    public GetterOnGetter(Getter<O, I> g1, Getter<I, P> g2) {
        this.g1 = g1;
        this.g2 = g2;
    }

    @Override
    public P get(O target) throws Exception {
        I i = g1.get(target);
        if (i != null) {
            return g2.get(i);
        }
        return null;
    }
}
