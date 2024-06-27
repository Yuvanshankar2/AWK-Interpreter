import java.util.LinkedList;
import java.util.Optional;

public class FunctionCallNode extends StatementNode {
	public String variable;
	public LinkedList<Optional<Node>> parameters;

	public FunctionCallNode(String variable, LinkedList<Optional<Node>> parameters) {
		this.variable = variable;
		this.parameters = parameters;
	}

	public String toString() {
		String param = " ";
		for (int i = 0; i < parameters.size(); i++) {
			if (i == parameters.size() - 1) {
				param += parameters.get(i).get().toString();
			} else
				param += parameters.get(i).get().toString() + ",";
		}
		if (variable.equals("print")) {
			return "FunctionNode" + "(" + variable + " " + param + ")";
		} else if (variable.equals("printf")) {
			return "FunctionNode" + "(" + variable + " " + param + ")";
		} else if (variable.equals("getline")) {
			return "FunctionNode" + "(" + variable + " " + param + ")";
		} else if (variable.equals("next")) {
			return "FunctionNode" + "(" + variable + ")";
		} else if (variable.equals("exit")) {
			return "FunctionNode" + "(" + variable + ")";
		} else if (variable.equals("nextfile")) {
			return "FunctionNode" + "(" + variable + ")";
		} else
			return "FunctionNode(" + variable + "(" + param + ")" + ")";
	}

}
