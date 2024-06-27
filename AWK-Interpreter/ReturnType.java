
public class ReturnType {
	public enum Type {
		RETURN, BREAK, CONTINUE, NONE
	};

	public String value;
	public Type type;

	public ReturnType(Type type) {
		this.type = type;
	}

	public ReturnType(String value, Type type) {
		this.value = value;
		this.type = type;
	}

	public String toString() {
		if (value.isEmpty()) {
			return type + "";
		}
		return type + value + "";
	}

}
