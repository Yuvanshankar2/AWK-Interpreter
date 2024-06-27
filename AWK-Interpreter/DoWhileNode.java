import java.util.Optional;

public class DoWhileNode extends StatementNode {
	public Optional<Node> condition;
	public BlockNode block;

	public DoWhileNode(Optional<Node> condition, BlockNode block) {
		this.condition = condition;
		this.block = block;
	}

	public String toString() {
		return "do" + "\n" + block.toString() + "\n" + "while(" + condition.get().toString();
	}
}
