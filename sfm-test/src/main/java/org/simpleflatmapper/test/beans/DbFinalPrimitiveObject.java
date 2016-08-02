package org.simpleflatmapper.test.beans;

public class DbFinalPrimitiveObject implements PrimitiveObject {
	
	protected final boolean pBoolean;
	protected final byte pByte;
	protected final char pCharacter;
	protected final short pShort;
	protected final int pInt;
	protected final long pLong;
	protected final float pFloat;
	protected final double pDouble;
	
	public DbFinalPrimitiveObject(boolean pBoolean, byte pByte,
			char pCharacter, short pShort, int pInt, long pLong, float pFloat,
			double pDouble) {
		super();
		this.pBoolean = pBoolean;
		this.pByte = pByte;
		this.pCharacter = pCharacter;
		this.pShort = pShort;
		this.pInt = pInt;
		this.pLong = pLong;
		this.pFloat = pFloat;
		this.pDouble = pDouble;
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
