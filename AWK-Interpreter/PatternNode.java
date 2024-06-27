/**
 * This class represents the regular expressions in awk. We classify these
 * expressions as pattern nodes.
 * 
 * @author yuvan
 *
 */
public class PatternNode extends Node {
	String value;

	public PatternNode(String value) {
		this.value = value;
	}

	public String toString() {
		return "Pattern node(" + value + ")";
	}
}
