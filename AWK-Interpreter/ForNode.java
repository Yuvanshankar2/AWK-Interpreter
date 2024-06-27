import java.util.Optional;

public class ForNode extends StatementNode {
	public Optional<Node> initialization;
	public Optional<Node> condition;
	public Optional<Node> updation;
	public BlockNode block;
	public Optional<Node> variable;
	public Optional<Node> array;

	public ForNode(Optional<Node> initialization, Optional<Node> condition, Optional<Node> updation, BlockNode block) {
		this.initialization = initialization;
		this.condition = condition;
		this.updation = updation;
		this.block = block;
	}

	public ForNode(Optional<Node> condition, Optional<Node> variable, BlockNode block, Optional<Node> array) {
		condition = Optional.empty();
		this.variable = variable;
		this.array = array;
		this.block = block;
	}

	public String toString() {
		return "for(" + initialization.get().toString() + "; " + condition.get().toString() + "; " + "\n"
				+ updation.get().toString() + ")" + "{" + "\n" + block.toString() + ";" + "\n" + "}";

	}

}
