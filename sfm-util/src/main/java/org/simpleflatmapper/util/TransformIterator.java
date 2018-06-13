package org.simpleflatmapper.util;

import java.util.Iterator;

public final class TransformIterator<I, O> implements Iterator<O> {
		private final Iterator<I> it;
		private final Function<? super I, ? extends O> transformer;

		public TransformIterator(Iterator<I> it, Function<? super I, ? extends O> transformer) {
			this.it = it;
			this.transformer = transformer;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}
		
		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public O next() {
			return transformer.apply(it.next());
		}
	}

