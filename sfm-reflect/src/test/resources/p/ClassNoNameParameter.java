package p;


public class ClassNoNameParameter {
	private String name;
	private int value;

	public ClassNoNameParameter(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public static ClassNoNameParameter of(String name) {
		return new ClassNoNameParameter(name, 0);
	}
}
