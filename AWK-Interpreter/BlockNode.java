import java.util.LinkedList;
import java.util.Optional;

/**
 * This class represents the block of statements in an awk program. It contains
 * a set of braces and some statements. We use the linkedlist below to add
 * statements every time we make them from the tokens.
 * 
 * @author yuvan
 *
 */
public class BlockNode extends Node {
	public LinkedList<StatementNode> statements;
	public Optional<Node> condition;

	public BlockNode() {
		statements = new LinkedList();
		condition = Optional.empty();
	}

	public void addStatement(StatementNode statement) {
		statements.add(statement);
	}

	public void setCondition(Optional<Node> condition) {
		this.condition = condition;
	}

	public LinkedList<StatementNode> getList() {
		return statements;
	}

	public String toString() {
		String block = "";
		if (condition.isPresent()) {
			block += condition.get();
		}
		for (int i = 0; i < statements.size(); i++) {
			block += "{" + statements.get(i) + " }";
		}
		return block;
	}
}
