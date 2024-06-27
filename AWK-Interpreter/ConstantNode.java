public class ConstantNode extends Node {
	String value;

	public ConstantNode(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return "Constant node(" + value + ")";
	}
}
