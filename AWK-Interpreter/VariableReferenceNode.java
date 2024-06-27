import java.util.Optional;

/**
 * This class represents a variable which could be a name(word),or an array
 * index reference.
 * 
 * @author yuvan
 *
 */
public class VariableReferenceNode extends Node {
	String name;
	Optional<Node> expression;

	public VariableReferenceNode(String name) {
		this.name = name;
		expression = Optional.empty();
	}

	public String getValue() {
		return name;
	}

	public VariableReferenceNode(String name, Optional<Node> expression) {
		this.name = name;
		this.expression = expression;
	}

	public String toString() {
		if (expression.isPresent()) { // We have to deal with the null case.
			return "VariableReference Node(" + name + "," + expression.get().toString() + ")";
		} else
			return "VariableReference Node(" + name + ")";
	}
}
