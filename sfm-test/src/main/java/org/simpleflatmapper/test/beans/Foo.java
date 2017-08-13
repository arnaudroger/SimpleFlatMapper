package org.simpleflatmapper.test.beans;

public class Foo extends Bar {
	private String foo;


	public String getFoo() {
		return foo;
	}

	public void setFoo(String foo) {
		this.foo = foo;
	}

	@Override
	public String toString() {
		return "Foo{" +
				"foo='" + foo + '\'' +
				'}';
	}
}
