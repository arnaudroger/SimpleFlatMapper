package org.simpleflatmapper.test.beans;

public class DbBoxedPrimitiveObject implements PrimitiveObject {
	
	protected Boolean pBoolean;
	protected Byte pByte;
	protected Character pCharacter;
	protected Short pShort;
	protected Integer pInt;
	protected Long pLong;
	protected Float pFloat;
	protected Double pDouble;

	public DbBoxedPrimitiveObject(Boolean pBoolean, Byte pByte, Character pCharacter, Short pShort, Integer pInt, Long pLong, Float pFloat, Double pDouble) {
		this.pBoolean = pBoolean;
		this.pByte = pByte;
		this.pCharacter = pCharacter;
		this.pShort = pShort;
		this.pInt = pInt;
		this.pLong = pLong;
		this.pFloat = pFloat;
		this.pDouble = pDouble;
	}

	public Boolean getoBoolean() {
		return pBoolean;
	}
	public Byte getoByte() {
		return pByte;
	}
	public Character getoCharacter() {
		return pCharacter;
	}
	public Short getoShort() {
		return pShort;
	}
	public Integer getoInt() {
		return pInt;
	}
	public Long getoLong() {
		return pLong;
	}
	public Float getoFloat() {
		return pFloat;
	}
	public Double getoDouble() {
		return pDouble;
	}
	
	public boolean ispBoolean() {
		return pBoolean;
	}
	public byte getpByte() {
		return pByte;
	}
	public char getpCharacter() {
		return pCharacter;
	}
	public short getpShort() {
		return pShort;
	}
	public int getpInt() {
		return pInt;
	}
	public long getpLong() {
		return pLong;
	}
	public float getpFloat() {
		return pFloat;
	}
	public double getpDouble() {
		return pDouble;
	}
	

}
