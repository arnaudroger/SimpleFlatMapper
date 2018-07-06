package org.simpleflatmapper.map.mapper;

import org.simpleflatmapper.map.EnumerableMapper;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.map.ConsumerErrorHandler;
import org.simpleflatmapper.util.Enumerable;
import org.simpleflatmapper.util.EnumerableIterator;
import org.simpleflatmapper.util.CheckedConsumer;

import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.simpleflatmapper.util.EnumerableSpliterator;

//IFJAVA8_END


public abstract class AbstractEnumerableMapper<SET, T, E extends Exception> implements EnumerableMapper<SET, T, E> {

    protected final ConsumerErrorHandler errorHandler;

    public AbstractEnumerableMapper(ConsumerErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    
    @Override
	public final <H extends CheckedConsumer<? super T>> H forEach(final SET source, final H handler)
			throws E, MappingException {
        final Enumerable<T> enumerable = enumerate(source);
        while(enumerable.next()) {
            final T t = enumerable.currentValue();
            handleT(handler, t);
        }
        return handler;
	}
	
    private <H extends CheckedConsumer<? super T>> void handleT(H handler, T t) {
        try {
            handler.accept(t);
        } catch(Throwable e) {
            errorHandler.handlerError(e, t);
        }
    }

    @Override
	public final Iterator<T> iterator(SET source) throws MappingException, E {
		return new EnumerableIterator<T>(enumerate(source));
	}

    //IFJAVA8_START
	@Override
	public final Stream<T> stream(SET source) throws MappingException, E {
		return StreamSupport.stream(new EnumerableSpliterator<T>(enumerate(source)), false);
	}
    //IFJAVA8_END



}
