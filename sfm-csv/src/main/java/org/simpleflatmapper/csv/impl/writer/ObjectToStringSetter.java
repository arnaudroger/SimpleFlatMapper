package org.simpleflatmapper.csv.impl.writer;

import org.simpleflatmapper.converter.Context;
import org.simpleflatmapper.map.setter.ContextualSetter;
import org.simpleflatmapper.reflect.Getter;
import org.simpleflatmapper.reflect.Setter;

public class ObjectToStringSetter<P> implements ContextualSetter<Appendable, P> {
    private final Getter<? super P, ?> getter;

    public ObjectToStringSetter(Getter<? super P, ?> getter) {
        this.getter = getter;
    }

    @Override
    public void set(Appendable target, P value, Context context) throws Exception {
        final Object o = getter.get(value);
        if (o != null) {
            target.append(String.valueOf(o));
        }
    }
}
