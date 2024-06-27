
/*
 * This class represents the interpreter of our compiler. It takes in the input file 
 * to be compiled and the awk program.
 */
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Interpreter {
	HashMap<String, InterpreterDatatype> globalvariable_map = new HashMap<>();
	HashMap<String, FunctionDefinitionNode> function_map = new HashMap<>();
	String FS;
	String OFMT = "%.6g";
	String OFS = "";
	String ORS = "\n";
	ProgramNode program;
	String FILENAME;
	int NR;
	int FNR;
	String test;
	LineManager lines;

	/*
	 * This is the inner class. It takes in the input files in the form of a list of
	 * lines.
	 */
	public class LineManager {
		List<String> input = new LinkedList<>();

		public LineManager(List<String> list) {
			this.input = list;

		}

		/*
		 * This method takes the next line,splits it into parts separated by the field
		 * separator. It then assigns the field values to the data types.
		 */
		public boolean splitAndAssign() {
			if (input.size() == 0) {
				return false;
			}
			String content[];
			content = input.get(0).split(globalvariable_map.get("FS").getValue());
			globalvariable_map.put("NF", new InterpreterDatatype(content.length + ""));
			globalvariable_map.put("$0", new InterpreterDatatype(input.get(0)));
			for (int i = 0; i < content.length; i++) {
				String field = "$" + (i + 1);
				globalvariable_map.put(field, new InterpreterDatatype(content[i]));
			}
			NR++;
			FNR++;
			globalvariable_map.put("NR", new InterpreterDatatype(NR + ""));
			globalvariable_map.put("FNR", new InterpreterDatatype(FNR + ""));
			input.remove(0);
			return true;
		}
	}

// This is the constructor that takes the program and input file first.
	// The last two parameters in this constructor are just used for testing
	// purposes.
	public Interpreter(ProgramNode program, String FILENAME) throws IOException {
		globalvariable_map.put("FS", new InterpreterDatatype(" "));
		globalvariable_map.put("OFMT", new InterpreterDatatype("%.6g"));
		globalvariable_map.put("OFS", new InterpreterDatatype(" "));
		globalvariable_map.put("ORS", new InterpreterDatatype("\n"));

		this.program = program;
		this.FILENAME = FILENAME;
		this.test = test;
		Path path = Path.of(FILENAME);
		lines = new LineManager(Files.readAllLines(path));
		BuiltInFunctionDefinitionNode print = new BuiltInFunctionDefinitionNode(true, "print", "");
		print.execute = (arguments) -> {
			if (arguments.get("arg1") instanceof InterpreterArrayDatatype) {
				InterpreterArrayDatatype type = (InterpreterArrayDatatype) arguments.get("arg1");
				String arr[] = new String[type.getMap().keySet().size()];
				for (int i = 0; i < type.getMap().keySet().size(); i++) {
					arr[i] = type.getMap().get(i + "").getValue();
					System.out.print(arr[i] + " ");
					System.out.println();
				}
				return "";
			}
			return "";
		};
		for (int i = 0; i < program.functions.size(); i++) {
			function_map.put(program.functions.get(i).fname, program.functions.get(i));
		}
		function_map.put("print", print);
		BuiltInFunctionDefinitionNode printf = new BuiltInFunctionDefinitionNode(true, "printf", "");
		printf.execute = (arguments) -> {
			if (arguments.get("arg1") instanceof InterpreterArrayDatatype) {
				InterpreterArrayDatatype type = (InterpreterArrayDatatype) arguments.get("arg1");
				String arr[] = new String[type.getMap().keySet().size()];
				for (int i = 0; i < type.getMap().keySet().size(); i++) {
					arr[i] = type.getMap().get(i + "").getValue();
				}
				for (int j = 0; j < arr.length; j++) {
					System.out.printf("%s", arr[j]);
				}
				return "";
			} else
				return "";
		};
		function_map.put("printf", printf);
		BuiltInFunctionDefinitionNode getline = new BuiltInFunctionDefinitionNode(false, "getline", "");
		getline.execute = (arguments) -> {
			lines.splitAndAssign();
			return "";
		};
		function_map.put("getline", getline);
		BuiltInFunctionDefinitionNode next = new BuiltInFunctionDefinitionNode(false, "next", "");
		next.execute = (arguments) -> {
			lines.splitAndAssign();
			return "";
		};
		function_map.put("next", next);
		BuiltInFunctionDefinitionNode index = new BuiltInFunctionDefinitionNode(false, "index", "");
		index.execute = (arguments) -> {
			String mainString = arguments.get("arg1").getValue();
			String substring = arguments.get("substring").getValue();
			return mainString.indexOf(substring) + "";
		};
		function_map.put("index", index);
		BuiltInFunctionDefinitionNode split = new BuiltInFunctionDefinitionNode(false, "split", "");
		split.execute = (arguments) -> {
			String arg1 = arguments.get("arg1").getValue();
			return arg1.split(globalvariable_map.get("FS").getValue()) + "";
		};
		function_map.put("split", split);

		BuiltInFunctionDefinitionNode length = new BuiltInFunctionDefinitionNode(false, "length", "");
		length.execute = (arguments) -> {
			String arg1 = arguments.get("arg1").getValue();
			return arg1.length() + "";
		};
		function_map.put("length", length);
		BuiltInFunctionDefinitionNode substr = new BuiltInFunctionDefinitionNode(false, "substr", "");
		substr.execute = (arguments) -> {
			String arg1 = arguments.get("arg1").getValue();
			Integer start = Integer.parseInt(arguments.get("start").getValue());
			return arg1.substring(start);
		};
		function_map.put("substr", substr);
		BuiltInFunctionDefinitionNode toupper = new BuiltInFunctionDefinitionNode(false, "toupper", "");
		toupper.execute = (arguments) -> {
			return arguments.get("arg1").getValue().toUpperCase();
		};
		function_map.put("toupper", toupper);
		BuiltInFunctionDefinitionNode tolower = new BuiltInFunctionDefinitionNode(false, "tolower", "");
		tolower.execute = (arguments) -> {
			return arguments.get("arg1").getValue().toLowerCase();
		};
		function_map.put("tolower", tolower);
		BuiltInFunctionDefinitionNode gsub = new BuiltInFunctionDefinitionNode(false, "gsub", "");
		gsub.execute = (arguments) -> {
			Pattern pattern = null;
			Matcher matcher = null;
			for (int i = 0; i < arguments.keySet().size(); i++) {
				if (arguments.containsKey("regular expression")) {
					pattern = Pattern.compile(arguments.get("regular expression").getValue());
					matcher = pattern.matcher(arguments.get("arg1").getValue());
					if (matcher.find()) {
						return matcher.replaceAll("Replaced");
					}
					return "No replacement";
				}
			}

			return "";
		};
		function_map.put("gsub", gsub);
		BuiltInFunctionDefinitionNode match = new BuiltInFunctionDefinitionNode(false, "match", "");
		match.execute = (arguments) -> {
			Pattern pattern = null;
			Matcher matcher = null;
			if (arguments.containsKey("regular expression")) {
				pattern = Pattern.compile(arguments.get("regular expression").getValue());
				matcher = pattern.matcher(arguments.get("arg1").getValue());
				if (matcher.find())
					return matcher.start() + "";
				else
					return "No match";
			}

			return "";
		};
		function_map.put("match", match);

		BuiltInFunctionDefinitionNode sub = new BuiltInFunctionDefinitionNode(false, "sub", "");
		sub.execute = (arguments) -> {
			Pattern pattern = null;
			Matcher matcher = null;
			if (arguments.containsKey("regular expression")) {
				pattern = Pattern.compile(arguments.get("regular expression").getValue());
				matcher = pattern.matcher(arguments.get("arg1").getValue());
				if (matcher.find()) {
					return matcher.replaceFirst("Replaced");
				}
				return "No replacement";
			}
			return "";
		};

		function_map.put("sub", sub);

	}

	/**
	 * This method walks through the entire parsed tree that we previously made. It
	 * takes each node, extracts the values, and implements the necessary
	 * operations. Once it finishes the operations, it assigns the updated values in
	 * the local variables hashmap and the global variables hashmap
	 */
	public InterpreterDatatype GetIDT(Node node, HashMap<String, InterpreterDatatype> local) throws Exception {
		if (node instanceof AssignmentNode) {
			AssignmentNode assignment = (AssignmentNode) node;
			InterpreterDatatype right = GetIDT(assignment.expression, local);
			if (assignment.target instanceof VariableReferenceNode || (assignment.target instanceof OperationNode)) {
				if (assignment.target instanceof OperationNode) {
					OperationNode operation_target = (OperationNode) assignment.target;
					if (operation_target.getOperator().equals(OperationNode.Operations.DOLLAR)) {
						InterpreterDatatype target = GetIDT(operation_target.left, local);
						local.put(target.getValue(), right);
						globalvariable_map.put(target.getValue(), right);
						return right;
					} else {
						throw new Exception();
					}
				} else {
					VariableReferenceNode variable = (VariableReferenceNode) assignment.target;
					if (variable.expression.isPresent()) {
						InterpreterDatatype index = GetIDT(variable.expression.get(), local);
						if (globalvariable_map.containsKey(variable.getValue())) {
							if (globalvariable_map.get(variable.getValue()) instanceof InterpreterArrayDatatype) {
								InterpreterArrayDatatype type = (InterpreterArrayDatatype) globalvariable_map
										.get(variable.getValue());
								type.getMap().put(index.getValue(), right);
								return right;
							} else
								throw new Exception("Duplicate value");
						} else {
							HashMap<String, InterpreterDatatype> array = new HashMap<>();
							array.put(index.getValue(), right);
							globalvariable_map.put(variable.getValue(), new InterpreterArrayDatatype(array));
							return right;
						}
					}
					if (local.containsKey(variable.getValue())) {
						local.put(variable.getValue(), right);
						globalvariable_map.put(variable.getValue(), right);
					}
					globalvariable_map.put(variable.getValue(), right);
					return right;
				}
			}

		} else if (node instanceof FunctionCallNode) {
			FunctionCallNode functioncall = (FunctionCallNode) node;
			return new InterpreterDatatype(RunFunctionCall(functioncall, local));

		} else if (node instanceof ConstantNode) {
			ConstantNode constant = (ConstantNode) node;
			return new InterpreterDatatype(constant.getValue());
		} else if (node instanceof VariableReferenceNode) {
			VariableReferenceNode variable = (VariableReferenceNode) node;
			// System.out.print(globalvariable_map.get(variable.getValue()).getValue());
			if (variable.expression.isPresent()) {
				InterpreterDatatype index = GetIDT(variable.expression.get(), local);
				if (globalvariable_map.get(variable.name) instanceof InterpreterArrayDatatype) {
					InterpreterArrayDatatype type = (InterpreterArrayDatatype) globalvariable_map.get(variable.name);
					return new InterpreterDatatype(type.getMap().get(index.getValue()).getValue());
				} else
					throw new Exception("No array found");
			}
			if (local.containsKey(variable.getValue())) {
				return local.get(variable.getValue());

			}
			if (globalvariable_map.containsKey(variable.getValue())) {
				// System.out.print(globalvariable_map.get(variable.getValue()).getValue());
				return globalvariable_map.get(variable.getValue());
			} else
				throw new Exception("It was not initialized");

		} else if (node instanceof OperationNode) {
			OperationNode constant = (OperationNode) node;
			InterpreterDatatype left = GetIDT(constant.left, local);
			InterpreterDatatype right = null;
			if (!constant.right.isEmpty()) {
				right = GetIDT(constant.right.get(), local);
			}

			if (constant.operation.equals(OperationNode.Operations.ADD)) {
				Float left_operand = Float.parseFloat(left.getValue());
				Float right_operand = Float.parseFloat(right.getValue());
				float sum = left_operand + right_operand;
				return new InterpreterDatatype(sum + "");

			} else if (constant.operation.equals(OperationNode.Operations.SUBTRACT)) {
				Float left_operand = Float.parseFloat(left.getValue());
				Float right_operand = Float.parseFloat(right.getValue());
				float difference = left_operand - right_operand;
				return new InterpreterDatatype(difference + "");

			} else if (constant.operation.equals(OperationNode.Operations.MULTIPLY)) {
				Float left_operand = Float.parseFloat(left.getValue());
				Float right_operand = Float.parseFloat(right.getValue());
				float product = left_operand * right_operand;
				return new InterpreterDatatype(product + "");

			} else if (constant.operation.equals(OperationNode.Operations.DIVIDE)) {
				Float left_operand = Float.parseFloat(left.getValue());
				Float right_operand = Float.parseFloat(right.getValue());
				float quotient = left_operand / right_operand;
				return new InterpreterDatatype(quotient + "");

			} else if (constant.operation.equals(OperationNode.Operations.MODULO)) {
				Float left_operand = Float.parseFloat(left.getValue());
				Float right_operand = Float.parseFloat(right.getValue());
				float quotient = left_operand % right_operand;
				return new InterpreterDatatype(quotient + "");

			} else if (constant.operation.equals(OperationNode.Operations.EXPONENT)) {
				Float left_operand = Float.parseFloat(left.getValue());
				Float right_operand = Float.parseFloat(right.getValue());
				double result = Math.pow(left_operand, right_operand);
				return new InterpreterDatatype(result + "");

			}

			if (constant.operation.equals(OperationNode.Operations.EQ)) {
				int result = 0;
				Float left_operand = null;
				Float right_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
					right_operand = Float.parseFloat(right.getValue());
				} catch (NumberFormatException e) {
					int comparison = left_operand.compareTo(right_operand);
					if (comparison == 0) {
						return new InterpreterDatatype(true + "");
					} else {
						return new InterpreterDatatype(false + "");
					}
				}
				// System.out.println(left_operand);
				// System.out.println(right_operand);
				result = (left_operand.compareTo(right_operand));
				if (result == 0) {
					return new InterpreterDatatype(true + "");
				} else
					return new InterpreterDatatype(false + "");

			} else if (constant.operation.equals(OperationNode.Operations.GT)) {
				int result = 0;
				Float left_operand = null;
				Float right_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
					right_operand = Float.parseFloat(right.getValue());
				} catch (NumberFormatException e) {
					int comparison = left_operand.compareTo(right_operand);
					if (comparison > 0) {
						return new InterpreterDatatype(true + "");
					} else {
						return new InterpreterDatatype(false + "");
					}
				}
				// System.out.println(left_operand);
				// System.out.println(right_operand);
				result = (left_operand.compareTo(right_operand));
				if (result > 0) {
					return new InterpreterDatatype(true + "");
				} else
					return new InterpreterDatatype(false + "");
			} else if (constant.operation.equals(OperationNode.Operations.GE)) {
				int result = 0;
				Float left_operand = null;
				Float right_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
					right_operand = Float.parseFloat(right.getValue());
				} catch (NumberFormatException e) {
					int comparison = left_operand.compareTo(right_operand);
					if (comparison >= 0) {
						return new InterpreterDatatype(true + "");
					} else {
						return new InterpreterDatatype(false + "");
					}
				}
				// System.out.println(left_operand);
				// System.out.println(right_operand);
				result = (left_operand.compareTo(right_operand));
				if (result >= 0) {
					return new InterpreterDatatype(true + "");
				} else
					return new InterpreterDatatype(false + "");
			} else if (constant.operation.equals(OperationNode.Operations.LT)) {
				int result = 0;
				Float left_operand = null;
				Float right_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
					right_operand = Float.parseFloat(right.getValue());
				} catch (NumberFormatException e) {
					int comparison = left_operand.compareTo(right_operand);
					if (comparison < 0) {
						return new InterpreterDatatype(true + "");
					} else {
						return new InterpreterDatatype(false + "");
					}
				}
				// System.out.println(left_operand);
				// System.out.println(right_operand);
				result = (left_operand.compareTo(right_operand));
				if (result < 0) {
					return new InterpreterDatatype(true + "");
				} else
					return new InterpreterDatatype(false + "");

			} else if (constant.operation.equals(OperationNode.Operations.LE)) {
				int result = 0;
				Float left_operand = null;
				Float right_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
					right_operand = Float.parseFloat(right.getValue());
				} catch (NumberFormatException e) {
					int comparison = left_operand.compareTo(right_operand);
					if (comparison <= 0) {
						return new InterpreterDatatype(true + "");
					} else {
						return new InterpreterDatatype(false + "");
					}
				}
				// System.out.println(left_operand);
				// System.out.println(right_operand);
				result = (left_operand.compareTo(right_operand));
				if (result <= 0) {
					return new InterpreterDatatype(true + "");
				} else
					return new InterpreterDatatype(false + "");

			} else if (constant.operation.equals(OperationNode.Operations.NE)) {
				int result = 0;
				Float left_operand = null;
				Float right_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
					right_operand = Float.parseFloat(right.getValue());
				} catch (NumberFormatException e) {
					int comparison = left_operand.compareTo(right_operand);
					if (comparison != 0) {
						return new InterpreterDatatype(true + "");
					} else {
						return new InterpreterDatatype(false + "");
					}
				}
				// System.out.println(left_operand);
				// System.out.println(right_operand);
				result = (left_operand.compareTo(right_operand));
				if (result != 0) {
					return new InterpreterDatatype(true + "");
				} else
					return new InterpreterDatatype(false + "");

			}
			if (constant.operation.equals(OperationNode.Operations.AND)) {
				Float left_operand = null;
				Float right_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
					right_operand = Float.parseFloat(right.getValue());
				} catch (NumberFormatException e) {
					return new InterpreterDatatype(false + "");
				}
				if (left_operand != 0 && right_operand != 0)
					return new InterpreterDatatype(true + "");
				else
					return new InterpreterDatatype(false + "");

			} else if (constant.operation.equals(OperationNode.Operations.OR)) {
				Float left_operand = null;
				Float right_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
					right_operand = Float.parseFloat(right.getValue());
				} catch (NumberFormatException e) {
					if (left_operand instanceof Float || right_operand instanceof Float) {
						return new InterpreterDatatype(true + "");
					} else
						return new InterpreterDatatype(false + "");

				}
				if (left_operand != 0 || right_operand != 0)
					return new InterpreterDatatype(true + "");
				else
					return new InterpreterDatatype(false + "");

			} else if (constant.operation.equals(OperationNode.Operations.NOT)) {
				Float left_operand = null;
				try {
					left_operand = Float.parseFloat(left.getValue());
				} catch (NumberFormatException e) {
					return new InterpreterDatatype(true + "");
				}
				if (left_operand == 0)
					return new InterpreterDatatype(true + "");
				else
					return new InterpreterDatatype(false + "");

			}
			if (constant.operation.equals(OperationNode.Operations.MATCH)) {
				String result = "";
				if (!(constant.right.get() instanceof PatternNode)) {
					throw new Exception("This is not a regular expression");
				}
				Pattern pattern = Pattern.compile(right.getValue());
				Matcher matcher = pattern.matcher(left.getValue());
				if (matcher.find()) {
					return new InterpreterDatatype(true + "");

				}
				return new InterpreterDatatype(false + "");
			} else if (constant.operation.equals(OperationNode.Operations.NOTMATCH)) {
				String result = "";
				if (!(constant.right.get() instanceof PatternNode)) {
					throw new Exception("This is not a regular expression");
				}
				Pattern pattern = Pattern.compile(right.getValue());
				Matcher matcher = pattern.matcher(left.getValue());
				if (matcher.find()) {
					return new InterpreterDatatype(false + "");

				} else
					return new InterpreterDatatype(true + "");

			} else if (constant.operation.equals(OperationNode.Operations.DOLLAR)) {

				return new InterpreterDatatype(globalvariable_map.get("$" + left.getValue()).getValue());

			}
			if (constant.operation.equals(OperationNode.Operations.PREINC)) {
				Float left_operand = Float.parseFloat(left.getValue());
				float leftoperand = ++left_operand;
				if (constant.left instanceof VariableReferenceNode) {
					VariableReferenceNode variable = (VariableReferenceNode) constant.left;
					if (local.containsKey(variable.getValue())) {
						local.put(variable.getValue(), new InterpreterDatatype(leftoperand + ""));
						globalvariable_map.put(variable.getValue(), new InterpreterDatatype(leftoperand + ""));
					} else if (globalvariable_map.containsKey(variable.getValue())) {
						globalvariable_map.put(variable.getValue(), new InterpreterDatatype(leftoperand + ""));
					}
				}
				return new InterpreterDatatype(leftoperand + "");

			} else if (constant.operation.equals(OperationNode.Operations.POSTINC)) {
				Float left_operand = Float.parseFloat(left.getValue());
				float leftoperand = left_operand++;
				if (constant.left instanceof VariableReferenceNode) {
					VariableReferenceNode variable = (VariableReferenceNode) constant.left;
					if (local.containsKey(variable.getValue())) {
						local.put(variable.getValue(), new InterpreterDatatype(left_operand + ""));
						globalvariable_map.put(variable.getValue(), new InterpreterDatatype(left_operand + ""));
					} else if (globalvariable_map.containsKey(variable.getValue())) {
						globalvariable_map.put(variable.getValue(), new InterpreterDatatype(left_operand + ""));
					}
				}
				return new InterpreterDatatype(leftoperand + "");

			} else if (constant.operation.equals(OperationNode.Operations.PREDEC)) {
				Float left_operand = Float.parseFloat(left.getValue());
				float leftoperand = --left_operand;
				if (constant.left instanceof VariableReferenceNode) {
					VariableReferenceNode variable = (VariableReferenceNode) constant.left;
					if (local.containsKey(variable.getValue())) {
						local.put(variable.getValue(), new InterpreterDatatype(leftoperand + ""));
						globalvariable_map.put(variable.getValue(), new InterpreterDatatype(leftoperand + ""));
					} else if (globalvariable_map.containsKey(variable.getValue())) {
						globalvariable_map.put(variable.getValue(), new InterpreterDatatype(leftoperand + ""));
					}
				}
				return new InterpreterDatatype(leftoperand + "");

			} else if (constant.operation.equals(OperationNode.Operations.POSTDEC)) {
				Float left_operand = Float.parseFloat(left.getValue());
				float leftoperand = left_operand--;
				if (constant.left instanceof VariableReferenceNode) {
					VariableReferenceNode variable = (VariableReferenceNode) constant.left;
					if (local.containsKey(variable.getValue())) {
						local.put(variable.getValue(), new InterpreterDatatype(left_operand + ""));
						globalvariable_map.put(variable.getValue(), new InterpreterDatatype(left_operand + ""));

					} else if (globalvariable_map.containsKey(variable.getValue())) {
						globalvariable_map.put(variable.getValue(), new InterpreterDatatype(left_operand + ""));
					}
				}
				return new InterpreterDatatype(leftoperand + "");

			} else if (constant.operation.equals(OperationNode.Operations.UNARYPOS)) {
				Float left_operand = Float.parseFloat(left.getValue());
				return new InterpreterDatatype(left_operand + "");

			} else if (constant.operation.equals(OperationNode.Operations.UNARYNEG)) {
				Float left_operand = Float.parseFloat(left.getValue());
				return new InterpreterDatatype(-left_operand + "");

			}

			if (constant.operation.equals(OperationNode.Operations.CONCATENATION)) {

				return new InterpreterDatatype(left.getValue() + right.getValue());

			}
			if (constant.operation.equals(OperationNode.Operations.IN)) {
				if (constant.right.get() instanceof VariableReferenceNode) {
					VariableReferenceNode exp = (VariableReferenceNode) constant.right.get();
					if (globalvariable_map.get(exp.name) instanceof InterpreterArrayDatatype) {
						InterpreterArrayDatatype type = (InterpreterArrayDatatype) globalvariable_map.get(exp.name);
						InterpreterDatatype index = GetIDT(constant.left, local);
						return new InterpreterDatatype(type.getMap().containsKey(index.getValue()) + "");

					} else
						throw new Exception("No array reference");

				} else
					throw new Exception("No variable reference");

			}
		} else if (node instanceof TernaryNode) {
			TernaryNode condition = (TernaryNode) node;
			InterpreterDatatype if_check = GetIDT(condition.expression.get(), local);
			Boolean check = Boolean.parseBoolean(if_check.getValue());
			if (check) {
				InterpreterDatatype correct_action = GetIDT(condition.true_statement.get(), local);
				return correct_action;
			} else {
				InterpreterDatatype incorrect_action = GetIDT(condition.false_statement.get(), local);
				return incorrect_action;
			}
		} else if (node instanceof PatternNode) {
			throw new Exception("Patterns cannot be passed.");
		} else {
			return null;
		}
		return null;
	}

	/**
	 * This method takes a statement from the awk code we pass in and implements the
	 * operations recorded in the parsed tree
	 * 
	 * @param local     It is the hashmap which stores local variables.
	 * @param statement The statement that we have to process
	 * @return A returntype, which contains the action we are performing and the
	 *         value that is being returned.
	 * @throws Exception
	 */
	public ReturnType ProcessStatement(HashMap<String, InterpreterDatatype> local, StatementNode statement)
			throws Exception {
		if (statement instanceof Break) {
			return new ReturnType(ReturnType.Type.RETURN);
		} else if (statement instanceof Continue) {
			return new ReturnType(ReturnType.Type.CONTINUE);
		} else if (statement instanceof DoWhileNode) {
			DoWhileNode loop = (DoWhileNode) statement;
			ReturnType type = null;
			BlockNode block = loop.block;
			boolean check = Boolean.parseBoolean(GetIDT(loop.condition.get(), local).getValue());
			do {
				type = InterpretListofStatements(block.getList(), local);
				if (type.type.equals(ReturnType.Type.BREAK)) {
					break;
				}
				if (type.type.equals(ReturnType.Type.CONTINUE)) {
					continue;
				}
				if (type.type.equals(ReturnType.Type.RETURN)) {
					return type;
				}

			} while (Boolean.parseBoolean(GetIDT(loop.condition.get(), local).getValue()));
			return type;
		} else if (statement instanceof ForNode) {
			ForNode forStatement = (ForNode) statement;
			BlockNode block = forStatement.block;
			AssignmentNode initial = (AssignmentNode) forStatement.initialization.get();
			ReturnType type = null;
			ProcessStatement(local, initial);
			while (Boolean.parseBoolean(GetIDT(forStatement.condition.get(), local).getValue())) {
				type = InterpretListofStatements(block.getList(), local);
				if (type.type.equals(ReturnType.Type.BREAK)) {
					break;
				}
				if (type.type.equals(ReturnType.Type.CONTINUE)) {
					continue;
				}
				if (type.type.equals(ReturnType.Type.RETURN)) {
					return type;
				}
				ProcessStatement(local, (StatementNode) forStatement.updation.get());
			}
			return type;

		} else if (statement instanceof ForEachNode) {
			ForEachNode forStatement = (ForEachNode) statement;
			BlockNode block = forStatement.block;
			ReturnType type = null;
			while (Boolean.parseBoolean(GetIDT(forStatement.array.get(), local).getValue())) {
				type = InterpretListofStatements(block.getList(), local);
				if (type.type.equals(ReturnType.Type.BREAK)) {
					break;
				}
				if (type.type.equals(ReturnType.Type.CONTINUE)) {
					continue;
				}
				if (type.type.equals(ReturnType.Type.RETURN)) {
					return type;
				}
			}
			return type;
		} else if (statement instanceof Return) {
			Return return_value = (Return) statement;
			if (return_value.statement.isPresent()) {
				return new ReturnType(GetIDT(return_value.statement.get(), local).getValue(), ReturnType.Type.RETURN);
			} else
				return new ReturnType(ReturnType.Type.RETURN);
		} else if (statement instanceof WhileNode) {
			WhileNode loop = (WhileNode) statement;
			BlockNode block = loop.block;
			ReturnType type = null;
			boolean check = Boolean.parseBoolean(GetIDT(loop.condition.get(), local).getValue());
			while (Boolean.parseBoolean(GetIDT(loop.condition.get(), local).getValue())) {
				type = InterpretListofStatements(block.getList(), local);
				if (type.type.equals(ReturnType.Type.BREAK)) {
					break;
				}
				if (type.type.equals(ReturnType.Type.CONTINUE)) {
					continue;
				}
				if (type.type.equals(ReturnType.Type.RETURN)) {
					return type;
				}
			}
			return type;
		} else if (statement instanceof FunctionCallNode) {
			FunctionCallNode function_call = (FunctionCallNode) statement;
			return new ReturnType(RunFunctionCall(function_call, local), ReturnType.Type.NONE);
		} else if (statement instanceof Delete) {
			Delete delete = (Delete) statement;
			if (delete.variable.get() instanceof VariableReferenceNode) {
				VariableReferenceNode array_reference = (VariableReferenceNode) delete.variable.get();
				if (globalvariable_map.get(array_reference.name) instanceof InterpreterArrayDatatype) {
					InterpreterDatatype index = GetIDT(array_reference.expression.get(), local);
					if (array_reference.expression.isPresent()) {
						InterpreterArrayDatatype type = (InterpreterArrayDatatype) globalvariable_map
								.get(array_reference.name);
						InterpreterDatatype removed = type.getMap().remove(index.getValue());
						return new ReturnType(removed.getValue(), ReturnType.Type.NONE);
					} else
						return new ReturnType(globalvariable_map.remove(array_reference.name).getValue(),
								ReturnType.Type.NONE);

				} else
					throw new Exception("It is not an array");

			}
			throw new Exception("The element is not compatible with delete");
		} else if (statement instanceof IfNode) {
			IfNode if_statement = (IfNode) statement;
			BlockNode block = if_statement.block_of_statements;
			ReturnType type = null;
			if (if_statement.conditional.isEmpty()
					|| Boolean.parseBoolean(GetIDT(if_statement.conditional.get(), local).getValue())) {
				type = InterpretListofStatements(block.getList(), local);
				if (!type.type.equals(ReturnType.Type.NONE)) {
					return type;
				}
			}
			if (if_statement.next != null) {
				return ProcessStatement(local, if_statement.next.getList().get(0));
			} else
				return new ReturnType("", ReturnType.Type.NONE);

		} else {
			return new ReturnType(GetIDT(statement, local).getValue(), ReturnType.Type.NONE);
		}

	}

// This is a helper method for a  function call implementation
	public String RunFunctionCall(FunctionCallNode function, HashMap<String, InterpreterDatatype> localmap)
			throws Exception {
		HashMap<String, InterpreterDatatype> functioncallmap = new HashMap<>();
		FunctionDefinitionNode fnode = function_map.get(function.variable);
		if (fnode instanceof BuiltInFunctionDefinitionNode) {
			BuiltInFunctionDefinitionNode funcNode = (BuiltInFunctionDefinitionNode) fnode;
			if (!funcNode.variadic) {
				for (int i = 0; i < function.parameters.size(); i++) {
					functioncallmap.put(fnode.getParameters().get(i),
							GetIDT(function.parameters.get(i).get(), localmap));
				}
				return funcNode.execute.apply(functioncallmap);
			} else {
				for (int i = 0; i < function.parameters.size(); i++) {
					functioncallmap.put(i + "", GetIDT(function.parameters.get(i).get(), localmap));
				}
				localmap.put("arg1", new InterpreterArrayDatatype(functioncallmap));
			}
			return funcNode.execute.apply(localmap);
		} else {
			for (int i = 0; i < function.parameters.size(); i++) {
				functioncallmap.put(fnode.getParameters().get(i), GetIDT(function.parameters.get(i).get(), localmap));
			}
			return InterpretListofStatements(fnode.statement, localmap).value;
		}
	}

	/**
	 * This method takes in a block of code from the awk program, and implements
	 * each statement in it.
	 * 
	 * @param statements: Represents the block of statements.
	 * @param local:      A hashmap used to store local variables.
	 * @return A returntype, which contains the action needed to be performed and
	 *         the value which we need to perform the action on and return.
	 * @throws Exception
	 */
	public ReturnType InterpretListofStatements(LinkedList<StatementNode> statements,
			HashMap<String, InterpreterDatatype> local) throws Exception {
		ReturnType keyword = null;
		for (int i = 0; i < statements.size(); i++) {
			keyword = ProcessStatement(local, statements.get(i));
			if (!keyword.type.equals(ReturnType.Type.NONE)) {
				return keyword;
			}
		}
		return keyword;
	}

// This method takes in the awk programs and starts the complete implementation process.
	public void IntepretProgram(ProgramNode program) throws Exception {
		for (int i = 0; i < program.Begin.size(); i++) {
			InterpretBlock(program.Begin.get(i));
		}
		while (lines.splitAndAssign()) {
			for (int j = 0; j < program.others.size(); j++) {
				InterpretBlock(program.others.get(j));
			}
		}

		for (int i = 0; i < program.End.size(); i++) {
			InterpretBlock(program.End.get(i));
		}
	}

// This method implements block of AWK code. It takes in any block and interprets the list of statements inside.
	public void InterpretBlock(BlockNode block) throws Exception {
		HashMap<String, InterpreterDatatype> local = new HashMap<>();
		if (block.condition.isPresent()) {
			boolean condition = Boolean.parseBoolean(GetIDT(block.condition.get(), null).getValue());
			if (condition) {
				InterpretListofStatements(block.statements, local);
			}
		} else
			InterpretListofStatements(block.statements, local);

	}

// This method is only for testing.
	public static void main(String args[]) throws Exception {
		String test = "# line_loop_example.awk\r\n" + "\r\n" + "{\r\n" + "    print \"Processing line:\", $0\r\n"
				+ "}\r\n" + "\r\n" + "END {\r\n" + "    print \"Finished processing all lines.\"\r\n" + "}\r\n" + "";
		Lexer lexer = new Lexer(test);
		lexer.Lex();
		Parser parser = new Parser(lexer.getList());
		ProgramNode program = parser.Parse();
		// System.out.println(parser.display());
		Interpreter interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		interpreter.IntepretProgram(program);
		// System.out.print(interpreter.hmap.get("match").getValue());
		// lexer.display();
		// System.out.println(program);
		// System.out.println(list.size());
		// HashMap<String, InterpreterDatatype> localmap = new HashMap<>();
		// ReturnType type = null;
		// for (int i = 0; i < list.size(); i++) {
		// System.out.print(list.get(i) + " ");
		// type = interpreter.ProcessStatement(localmap, list.get(i));
		// System.out.println(type.value + " ");

		// }
		// System.out.println(interpreter.globalvariable_map.get("sum").getValue());

	}
}
