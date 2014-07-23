package org.sfm.reflect;

import java.lang.invoke.MethodHandle;

public final class MethodHandleSetter<T, P> implements Setter<T, P> {

	private final MethodHandle method;
	private final Class<? extends P> type;
	
	@SuppressWarnings("unchecked")
	public MethodHandleSetter(MethodHandle method) {
		this.method = method;
		this.type = (Class<? extends P>) method.type().parameterType(1);
	}

	public void set(T target, P value) throws Exception {
		try {
			method.invoke(target, value);
		} catch(Exception e) {
			throw e;
		} catch(Error e) {
			throw e;
		} catch(Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Class<? extends P> getPropertyType() {
		return type;
	}
	
	public MethodHandle getMethodHandle() {
		return method;
	}
}
