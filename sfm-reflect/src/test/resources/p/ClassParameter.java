package p;


public class ClassParameter {
	private String name;
	private int value;

	public ClassParameter(String name, int value) {
		this.name = name;
		this.value = value;
	}

	public static ClassParameter of(String name) {
		return new ClassParameter(name, 0);
	}
}
