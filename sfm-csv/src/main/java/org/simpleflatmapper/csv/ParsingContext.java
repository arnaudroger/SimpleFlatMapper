package org.simpleflatmapper.csv;

public class ParsingContext {

	private final Object[] context;

	public ParsingContext(Object[] context) {
        this.context = context;
	}
	
    public Object getContext(int index) {
        return context == null ? null : context[index];
    }
}
