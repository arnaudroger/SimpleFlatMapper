package org.simpleflatmapper.test.beans;

public class Db1GenericObject<T extends Bar, V extends Foo> {

	private int id;
	private T barObject;
	private V fooObject;
	private Pair<T, V> pair;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public T getBarObject() {
		return barObject;
	}
	public void setBarObject(T barObject) {
		this.barObject = barObject;
	}
	public V getFooObject() {
		return fooObject;
	}
	public void setFooObject(V fooObject) {
		this.fooObject = fooObject;
	}
	public Pair<T, V> getPair() {
		return pair;
	}
	public void setPair(Pair<T, V> pair) {
		this.pair = pair;
	}
}
