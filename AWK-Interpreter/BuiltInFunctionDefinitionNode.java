
/*
 * This class represents a built in function in AWK
 */
import java.util.HashMap;
import java.util.function.Function;

public class BuiltInFunctionDefinitionNode extends FunctionDefinitionNode {
	boolean variadic;
	String name;
	ProgramNode program;
	String optional;

	public BuiltInFunctionDefinitionNode(boolean variadic, String name, String optional) {
		super(name);
		if (!variadic) {
			if (name.equals("match") || name.equals("gsub") || name.equals("sub")) {
				addParam("arg1");
				addParam("regular expression");
			} else if (name.equals("split") || name.equals("substring")) {
				addParam("arg1");
				addParam("start");
			} else {
				addParam("arg1");
			}
		} else {

		}

		this.optional = optional;
		this.variadic = variadic;
	}

// This method checks if each function definition has the right number of parameters.

// This is the lambda expression that implements each built in function.
	public Function<HashMap<String, InterpreterDatatype>, String> execute;
}
