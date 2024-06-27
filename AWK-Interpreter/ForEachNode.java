import java.util.Optional;

public class ForEachNode extends StatementNode {
	public Optional<Node> array;
	public BlockNode block;

	public ForEachNode(BlockNode block, Optional<Node> array) {
		this.array = array;
		this.block = block;
	}

	public String toString() {
		return "for(" + array.get().toString() + ")" + "{" + block.toString() + "}";
	}
}
