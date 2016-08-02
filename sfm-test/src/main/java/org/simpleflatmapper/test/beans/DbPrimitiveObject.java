package org.simpleflatmapper.test.beans;

public class DbPrimitiveObject implements PrimitiveObject {
	
	protected boolean pBoolean;
	protected byte pByte;
	protected char pCharacter;
	protected short pShort;
	protected int pInt;
	protected long pLong;
	protected float pFloat;
	protected double pDouble;
	
	@Override
	public boolean ispBoolean() {
		return pBoolean;
	}
	@Override
	public byte getpByte() {
		return pByte;
	}
	@Override
	public char getpCharacter() {
		return pCharacter;
	}
	@Override
	public short getpShort() {
		return pShort;
	}
	@Override
	public int getpInt() {
		return pInt;
	}
	@Override
	public long getpLong() {
		return pLong;
	}
	@Override
	public float getpFloat() {
		return pFloat;
	}
	@Override
	public double getpDouble() {
		return pDouble;
	}
	

}
