package org.simpleflatmapper.test.beans;

public class DbPrimitiveObjectFields implements PrimitiveObject {
	
	public boolean pBoolean;
    public byte pByte;
    public char pCharacter;
    public short pShort;
    public int pInt;
    public long pLong;
    public float pFloat;
    public double pDouble;
	
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
