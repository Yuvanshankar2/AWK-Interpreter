import java.util.LinkedList;

/**
 * This class represents the functions of an awk program. It contains a name,
 * parameters, and some statements. We use the linkedlists below to add a
 * statement or parameter every time we make them.
 * 
 * @author yuvan
 *
 */
public class FunctionDefinitionNode extends Node {
	String fname;
	LinkedList<StatementNode> statement;
	LinkedList<String> parameters;

	public FunctionDefinitionNode(String name) {
		fname = name;
		parameters = new LinkedList<>();
		statement = new LinkedList<>();
	}

	public void addStatement(StatementNode line) {
		statement.add(line);
	}

	public void addParam(String parameter) {
		parameters.add(parameter);
	}

	public String getName() {
		return fname;
	}

	public LinkedList<StatementNode> getStatement() {
		return statement;
	}

	public LinkedList<String> getParameters() {
		return parameters;
	}

	public String toString() {
		String function_call = "function" + " " + fname + "(";
		for (int i = 0; i < parameters.size(); i++) {
			function_call += parameters.get(i);
			if (i < parameters.size() - 1) {
				function_call += ",";
			}
		}
		function_call += ")";
		function_call += "{";
		for (int i = 0; i < statement.size(); i++) {
			function_call += statement.get(i) + " ";
		}
		function_call += "}";
		return function_call;
	}
}
