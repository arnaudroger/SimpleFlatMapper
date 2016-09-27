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

	public DbPrimitiveObject(boolean pBoolean, byte pByte, char pCharacter, short pShort, int pInt, long pLong, float pFloat, double pDouble) {
		this.pBoolean = pBoolean;
		this.pByte = pByte;
		this.pCharacter = pCharacter;
		this.pShort = pShort;
		this.pInt = pInt;
		this.pLong = pLong;
		this.pFloat = pFloat;
		this.pDouble = pDouble;
	}

	public DbPrimitiveObject() {
	}

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
