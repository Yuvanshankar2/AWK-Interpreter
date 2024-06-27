import java.util.Optional;

public class Return extends StatementNode {
	Optional<Node> statement;

	public Return(Optional<Node> statement) {
		this.statement = statement;
	}

	public String toString() {
		return "return" + " " + statement.get().toString() + ";";
	}
}
