import java.util.Optional;

public class WhileNode extends StatementNode {
	Optional<Node> condition;
	BlockNode block;

	public WhileNode(Optional<Node> condition, BlockNode block) {
		this.condition = condition;
		this.block = block;
	}

	public String toString() {
		return "while(" + condition.get().toString() + ")" + "{" + "\n" + block.toString() + "\n " + "}";
	}

}
