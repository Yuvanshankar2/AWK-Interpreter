import java.util.Optional;

/**
 * This class represents an operation that is being done in awk. It consists of
 * a left hand value, an operator, and might have a right-hand value as well.
 * 
 * @author yuvan
 *
 */
public class OperationNode extends StatementNode {
	public enum Operations {
		EQ, NE, LT, LE, GT, GE, AND, OR, NOT, MATCH, NOTMATCH, DOLLAR, PREINC, POSTINC, PREDEC, POSTDEC, UNARYPOS,
		UNARYNEG, IN, EXPONENT, ADD, SUBTRACT, MULTIPLY, DIVIDE, MODULO, CONCATENATION
	};

	Node left;
	Optional<Node> right;
	Operations operation;

	public OperationNode(Node value, Operations operation) {
		left = value;
		this.operation = operation;
		right = Optional.empty();
	}

	public OperationNode.Operations getOperator() {
		return operation;
	}

	public OperationNode(Node left, Optional<Node> right, Operations operation) {
		this.left = left;
		this.right = right;
		this.operation = operation;
	}

	public String toString() {
		if (right.isPresent()) {
			return "Operation Node(" + left.toString() + "," + operation.toString() + "," + right.get().toString()
					+ ")";
		} else
			return "Operation Node(" + left.toString() + "," + operation.toString() + ")";
	}
}
