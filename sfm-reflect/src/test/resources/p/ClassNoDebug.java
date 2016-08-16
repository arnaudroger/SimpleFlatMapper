package p;


import java.util.Date;

public class ClassNoDebug {
	private final String name;
	private final int value;
	private final Date date;
	private final E accessMode;

	public ClassNoDebug(String name,
						int value,
						Date date,
						E accessMode) {
		if (date == null) throw new NullPointerException();
		if (accessMode == null) throw new NullPointerException();
		this.name = name;
		this.value = value;
		this.date = date;
		this.accessMode = accessMode;
	}

	public String getName() {
		return name;
	}

	public int getValue() {
		return value;
	}

	public Date getDate() {
		return date;
	}

	public E getAccessMode() {
		return accessMode;
	}

	public static enum E { A, B, C };
}
