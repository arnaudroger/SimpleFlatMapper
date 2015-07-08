package org.sfm.map.impl;

import org.sfm.map.*;
import org.sfm.utils.*;

import java.util.Iterator;

//IFJAVA8_START
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
//IFJAVA8_END


public abstract class AbstractEnumarableMapper<S, T, E extends Exception> implements EnumarableMapper<S, T, E> {

    protected final RowHandlerErrorHandler errorHandler;

    public AbstractEnumarableMapper(RowHandlerErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @Override
	public final <H extends RowHandler<? super T>> H forEach(final S source, final H handler)
			throws MappingException {
        try  {
            Enumarable<T> enumarable = newEnumarableOfT(source);
            while(enumarable.next()) {
                handler.handle(enumarable.currentValue());
            }
            return handler;
        } catch(Exception e) {
            return ErrorHelper.rethrow(e);
        }
	}

    @Override
	public final Iterator<T> iterator(S source) throws MappingException, E {
		return new EnumarableIterator<T>(newEnumarableOfT(source));
	}

    //IFJAVA8_START
	@Override
	public final Stream<T> stream(S source) throws MappingException, E {
		return StreamSupport.stream(new EnumarableSpliterator<T>(newEnumarableOfT(source)), false);
	}
    //IFJAVA8_END

    protected abstract Enumarable<T> newEnumarableOfT(S source) throws E;


}
