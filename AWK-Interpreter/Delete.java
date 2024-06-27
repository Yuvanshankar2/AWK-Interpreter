import java.util.Optional;

public class Delete extends StatementNode {
	public Optional<Node> variable;

	public Delete(Optional<Node> variable) {
		this.variable = variable;
	}

	public String toString() {
		return "delete " + variable.get().toString();
	}

}
