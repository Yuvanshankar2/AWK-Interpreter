import java.util.Optional;

/**
 * This class represents the ternary operation of the awk language.
 * 
 * @author yuvan
 *
 */
public class TernaryNode extends StatementNode {
	Optional<Node> true_statement;
	Optional<Node> false_statement;
	Optional<Node> expression;

	public TernaryNode(Optional<Node> expression, Optional<Node> true_statement, Optional<Node> false_statement) {
		this.expression = expression;
		this.true_statement = true_statement;
		this.false_statement = false_statement;
	}

	public String toString() {
		return "TernaryNode( " + expression.get().toString() + "," + "?" + "," + true_statement.get().toString() + ":"
				+ "," + false_statement.get().toString() + ")";
	}

}
