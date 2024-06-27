import java.util.Optional;

public class IfNode extends StatementNode {
	public Optional<Node> conditional;
	public BlockNode block_of_statements;
	public BlockNode next;

	public IfNode(Optional<Node> conditional, BlockNode block_of_statements, BlockNode next) {
		this.conditional = conditional;
		this.block_of_statements = block_of_statements;
		this.next = next;
	}

	public String toString() {
		if (next != null) {
			return "if(" + conditional.get().toString() + ")" + block_of_statements.toString() + " " + "else" + " "
					+ next.toString();
		} else {
			return "if(" + conditional.get().toString() + ")" + block_of_statements.toString();
		}
	}

}
