package org.simpleflatmapper.map.fieldmapper;

import org.simpleflatmapper.map.MappingContext;

public class BoxedByteFieldMapperGetter<T> implements ByteFieldMapperGetter<T>, FieldMapperGetter<T, Byte> {


    private final FieldMapperGetter<? super T, ? extends Byte> delegate;

    public BoxedByteFieldMapperGetter(FieldMapperGetter<? super T, ? extends Byte> delegate) {
        this.delegate = delegate;
    }

    @Override
    public byte getByte(T target, MappingContext<?> mappingContext) throws Exception {
        final Byte value = get(target, mappingContext);
        if (value != null) {
            return value.byteValue();
        }
        return 0;
    }

    @Override
    public Byte get(T target, MappingContext<?> context) throws Exception {
        return delegate.get(target, context);
    }
}
