import java.util.LinkedList;
import java.util.Optional;

/**
 * This is the parser class. It takes in a list of tokens from the lexer and
 * represents these tokens in sequence in the form of a tree.
 * 
 * @author yuvan
 *
 */
public class Parser {
	public TokenManager manage;

	// The constructor of the class that takes the list of tokens.
	public Parser(LinkedList<Token> list) {
		manage = new TokenManager(list);
	}

	/**
	 * This method essentially checksthe list for separators and ignores them in a
	 * way as they are not needed in some structures.
	 * 
	 * @return check: The variable used to check if a token is a separator.
	 */
	public boolean AcceptSeparators() {
		boolean check = false;
		while (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.SEPERATOR).isPresent()) {
			check = true;
		}

		return check;
	}

	/**
	 * This method builds structures out of the list of tokens such as functions and
	 * returns the ProgramNode, the head of the "tree" which contains all types of
	 * structures.
	 * 
	 * @return program: An instance of the ProgramNode.
	 * @throws Exception
	 */
	public ProgramNode Parse() throws Exception {
		ProgramNode program = new ProgramNode();
		while (manage.MoreTokens()) {
			AcceptSeparators();
			if (!parseFunction(program) && !parseAction(program)) {
				throw new Exception();

			}
			AcceptSeparators();
		}
		return program;
	}

	/**
	 * This method checks if a group of tokens from the list match the definition of
	 * a function If it does, the method returns true, makes a function out of the
	 * tokens and adds them to the contents of the ProgramNode. If it does not, the
	 * methods returns false.
	 * 
	 * @param program : An instance of the programNode.
	 * @return True, if it is a fucntion. False, if it is not.
	 * @throws Exception
	 */
	public boolean parseFunction(ProgramNode program) throws Exception {
		AcceptSeparators();
		if (manage.MatchAndRemove(Token.Tokentype.FUNCTION).isEmpty()) {
			return false;
		}
		AcceptSeparators();
		Optional<Token> opttoken = manage.MatchAndRemove(Token.Tokentype.WORD);
		String name;
		Token token;
		if (opttoken.isPresent()) {
			token = opttoken.get();
			name = token.getVal();
		} else {
			return false;
		}
		AcceptSeparators();
		if (manage.MatchAndRemove(Token.Tokentype.OPEN_PARA).isEmpty()) {
			return false;
		}
		Optional<Token> parameters = manage.peek(0);
		FunctionDefinitionNode function = new FunctionDefinitionNode(name);
		boolean error = false;
		while (true) {
			AcceptSeparators();
			Optional<Token> token1 = manage.MatchAndRemove(Token.Tokentype.WORD);
			if (token1.isPresent()) {
				error = true;
				token = token1.get();
				String param = token.getVal();
				function.addParam(param);
			}
			if (manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isPresent()) {
				if (error == false) {
					return false;
				} else {
					break;
				}
			}
			AcceptSeparators();
			if (manage.MatchAndRemove(Token.Tokentype.COMMA).isPresent()) {
				if (error == false) {
					return false;
				} else {
					error = false;
					continue;
				}
			} else {
				return false;
			}
		}
		BlockNode block = parseBlock();
		function.statement = block.statements;
		program.addFunction(function);
		return true;
	}

	/**
	 * This method takes a group of tokens and checks if they match a BEGIN block,
	 * or an END block, or any other block of content in the middle of an awk code.
	 * It returns true if the tokens match any of these structures. It returns false
	 * if does not match any of these structures.
	 * 
	 * @throws Exception
	 */
	public boolean parseAction(ProgramNode program) throws Exception {
		if (manage.MatchAndRemove(Token.Tokentype.BEGIN).isPresent()) {
			BlockNode beginBlock = parseBlock();
			program.addBegin(beginBlock);
			return true;
		} else if (manage.MatchAndRemove(Token.Tokentype.END).isPresent()) {
			program.addEnd(parseBlock());
			return true;
		} else {
			if (manage.peek(0).get().getTokenType().equals(Token.Tokentype.OPEN_BRACE)
					|| manage.peek(0).get().getTokenType().equals(Token.Tokentype.OPEN_PARA)) {
				BlockNode node = parseBlock();
				program.addOthers(node);
				return true;
			}
			Optional<Node> condition = parseOperation();
			BlockNode node = parseBlock();
			node.setCondition(condition);
			program.addOthers(node);
			return true;
		}
	}

// This method returns a structure(condition) of statements.
	public Optional<Node> parseOperation() throws Exception {
		return parseAssignment();
	}

// This method returns a block structure(BlockNode), which contains a set of braces
// and some statements. 
	public BlockNode parseBlock() throws Exception {
		BlockNode block = new BlockNode();
		if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.OPEN_BRACE).isPresent()) {
			do {
				AcceptSeparators();
				block.addStatement(parseStatement().get());
				AcceptSeparators();
				if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.CLOSED_BRACE).isPresent()) {
					return block;
				}
			} while (true);
		} else {
			block.addStatement(parseStatement().get());
			return block;
		}
		// return null;
	}

	// This method searches for and makes different types of statement
	public Optional<StatementNode> parseStatement() throws Exception {
		if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.IF)) {
			return Optional.of(parseIf());
		} else if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.FOR)) {
			return Optional.of(parseFor());
		} else if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.CONTINUE)) {
			return Optional.of(parseContinue());
		} else if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.BREAK)) {
			return Optional.of(parseBreak());
		} else if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.DELETE)) {
			return Optional.of(parseDelete());
		} else if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.WHILE)) {
			return Optional.of(parseWhile());
		} else if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.DO)) {
			return Optional.of(parseDoWhile());
		} else if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.RETURN)) {
			return Optional.of(parseReturn());
		} else {
			AcceptSeparators();
			Optional<Node> operation = parseAssignment();
			if (operation.get() instanceof OperationNode) {
				OperationNode operations = (OperationNode) operation.get();
				if (operations.getOperator().equals(OperationNode.Operations.PREINC)
						|| operations.getOperator().equals(OperationNode.Operations.POSTINC)
						|| operations.getOperator().equals(OperationNode.Operations.PREDEC)
						|| operations.getOperator().equals(OperationNode.Operations.POSTDEC)
						|| operations.getOperator().equals(OperationNode.Operations.IN)) {
					return Optional.of((StatementNode) operations);
				} else {
					throw new Exception();
				}
			}
			return Optional.of((StatementNode) operation.get());

		}

	}

	// This method makes a return statement
	public StatementNode parseReturn() throws Exception {
		if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.RETURN).isPresent()) {
			return new Return(parseOperation());
		}
		return null;
	}

	// This method returns a function call statement.
	public Optional<Node> parseFunctionCall() throws Exception {
		LinkedList<Optional<Node>> parameters = new LinkedList();
		Optional<Node> parameter = Optional.empty();
		if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.WORD)
				&& manage.MoreTokens() && manage.peek(1).get().getTokenType().equals(Token.Tokentype.OPEN_PARA)) {
			String name = manage.peek(0).get().getVal();
			manage.MatchAndRemove(Token.Tokentype.WORD);
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.OPEN_PARA).isPresent()) {
				while (manage.MoreTokens() && !manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isPresent()) {
					AcceptSeparators();
					parameter = Optional.of(parseOperation().get());
					manage.MatchAndRemove(Token.Tokentype.COMMA);
					AcceptSeparators();
					parameters.add(parameter);
					AcceptSeparators();
				}
				return Optional.of(new FunctionCallNode(name, parameters));
			} else {
				return Optional.empty();
			}

		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.PRINT).isPresent()) {
			while (manage.MoreTokens() && !manage.MatchAndRemove(Token.Tokentype.SEPERATOR).isPresent()) {
				parameter = Optional.of(parseOperation().get());
				manage.MatchAndRemove(Token.Tokentype.COMMA);
				parameters.add(parameter);
			}
			return Optional.of(new FunctionCallNode("print", parameters));

		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.PRINTF).isPresent()) {
			while (manage.MoreTokens() && !manage.MatchAndRemove(Token.Tokentype.SEPERATOR).isPresent()) {
				parameter = Optional.of(parseOperation().get());
				manage.MatchAndRemove(Token.Tokentype.COMMA);
				parameters.add(parameter);
			}
			return Optional.of(new FunctionCallNode("printf", parameters));

		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.EXIT).isPresent()) {
			return Optional.of(new FunctionCallNode("exit", parameters));

		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.GETLINE).isPresent()) {
			while (manage.MoreTokens() && !manage.MatchAndRemove(Token.Tokentype.SEPERATOR).isPresent()) {
				parameter = Optional.of(parseOperation().get());
				manage.MatchAndRemove(Token.Tokentype.COMMA);
				parameters.add(parameter);
			}
			return Optional.of(new FunctionCallNode("getline", parameters));

		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.NEXTFILE).isPresent()) {
			return Optional.of(new FunctionCallNode("nextfile", parameters));

		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.NEXT).isPresent()) {
			return Optional.of(new FunctionCallNode("next", parameters));
		} else
			return Optional.empty();
	}

	// This method makes a do while statement block.
	public StatementNode parseDoWhile() throws Exception {
		Optional<Node> condition = Optional.empty();
		if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.DO).isPresent()) {
			BlockNode block = parseBlock();

			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.WHILE).isPresent()) {
				if (manage.MatchAndRemove(Token.Tokentype.OPEN_PARA).isPresent()) {
					condition = parseOperation();
				} else {
					throw new Exception("no closing paranthesis");
				}
				if (condition.isEmpty()) {
					throw new Exception("no closing paranthesis");
				}
				if (!manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isPresent()) {
					throw new Exception("no closing paranthesis");
				}
				return new DoWhileNode(condition, block);
			} else {
				throw new Exception("No while statement");
			}
		}
		return null;
	}

	// This method makes a while statement block.
	public StatementNode parseWhile() throws Exception {
		Optional<Node> condition = Optional.empty();
		if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.WHILE).isPresent()) {
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.OPEN_PARA).isPresent()) {
				condition = parseOperation();
			} else {
				throw new Exception();
			}
			if (manage.MoreTokens() && !manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isPresent()) {
				throw new Exception("no closing paranthesis");
			}
			BlockNode block = parseBlock();
			return new WhileNode(condition, block);
		}
		return null;
	}

	// This method makes a delete statement.
	public StatementNode parseDelete() throws Exception {
		Optional<Node> variable;
		if (manage.MatchAndRemove(Token.Tokentype.DELETE).isPresent()) {
			variable = parseLvalue();
			return new Delete(variable);
		}
		return null;
	}

	// This method makes a break statment.
	public StatementNode parseBreak() {
		if (manage.MatchAndRemove(Token.Tokentype.BREAK).isPresent()) {
			return new Break();
		}
		return null;
	}

	// This method makes a continue statement.
	public StatementNode parseContinue() {
		if (manage.MatchAndRemove(Token.Tokentype.CONTINUE).isPresent()) {
			return new Continue();
		}
		return null;
	}

	// This method makes a for statement block.
	public StatementNode parseFor() throws Exception {
		Optional<Node> initialization = Optional.empty();
		Optional<Node> condition = Optional.empty();
		Optional<Node> updation = Optional.empty();
		Optional<Node> variable = Optional.empty();
		Optional<Node> array = Optional.empty();
		int i = 0;
		do {
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.FOR).isPresent()) {
				if (manage.MatchAndRemove(Token.Tokentype.OPEN_PARA).isPresent()) {
					while (manage.MoreTokens()
							&& !manage.peek(i).get().getTokenType().equals(Token.Tokentype.CLOSED_PARA)) {
						if (manage.MoreTokens() && manage.peek(i).get().getTokenType().equals(Token.Tokentype.IN)) {
							initialization = parseOperation();
							if (manage.MoreTokens()
									&& !manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isPresent()) {
								throw new Exception("no closing paranthesis");
							}
							BlockNode block = parseBlock();
							return new ForEachNode(block, initialization);
						}
						i++;
					}
					initialization = parseOperation();
					AcceptSeparators();

					AcceptSeparators();
					condition = parseOperation();
					AcceptSeparators();
					updation = parseOperation();

				} else {
					throw new Exception();
				}
				if (manage.MoreTokens() && !manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isPresent()) {
					throw new Exception("no closing paranthesis");
				}
				BlockNode block = parseBlock();
				return new ForNode(initialization, condition, updation, block);
			}
		} while (true);
		// return null;
	}

	// This method makes an if statement block.
	public StatementNode parseIf() throws Exception {
		Optional<Node> condition = Optional.empty();
		BlockNode ifblock = null;
		StatementNode first_Block = null;
		do {
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.IF).isPresent()) {
				if (manage.MatchAndRemove(Token.Tokentype.OPEN_PARA).isPresent()) {
					condition = parseOperation();
				} else {
					throw new Exception();
				}
				// System.out.print(condition.get().toString() + " ");
				if (manage.MoreTokens() && !manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isPresent()) {
					throw new Exception("no closing paranthesis");
				}
				ifblock = parseBlock();
				AcceptSeparators();
				// System.out.print(ifblock.toString() + " ");
				if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.ELSE).isPresent()) {
					AcceptSeparators();
					if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.IF)) {
						return new IfNode(condition, ifblock, parseBlock());
					}
					// System.out.print(ifblock.toString());
					BlockNode block = parseBlock();
					return new IfNode(condition, ifblock, block);
				}
				return new IfNode(condition, ifblock, null);
			}

		} while (true);

	}

// This method returns the operations and constants at the lowest level of precedence.
	public Optional<Node> parseBottomlevel() throws Exception {
		if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.LITERAL)) {
			return Optional.of(new ConstantNode(manage.MatchAndRemove(Token.Tokentype.LITERAL).get().getVal()));
		} else if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.NUMBER)) {
			// System.out.print(manage.peek(0).get().getVal());
			return Optional.of(new ConstantNode(manage.MatchAndRemove(Token.Tokentype.NUMBER).get().getVal()));
		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.OPEN_PARA).isPresent()) {
			Optional<Node> expression = parseOperation();
			if (!manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isPresent()) {
				throw new Exception();
			}
			return expression;
		} else if (manage.MoreTokens()
				&& manage.peek(0).get().getTokenType().equals(Token.Tokentype.REGULAR_EXPRESSIONS)) {
			return Optional
					.of(new PatternNode(manage.MatchAndRemove(Token.Tokentype.REGULAR_EXPRESSIONS).get().getVal()));
		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.NOT).isPresent()) {
			Optional<Node> expression = parseOperation();
			return Optional.of(new OperationNode(expression.get(), OperationNode.Operations.NOT));
		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.MINUS).isPresent()) {
			Optional<Node> expression = parseOperation();
			return Optional.of(new OperationNode(expression.get(), OperationNode.Operations.UNARYNEG));
		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.PLUS).isPresent()) {
			Optional<Node> expression = parseOperation();
			return Optional.of(new OperationNode(expression.get(), OperationNode.Operations.UNARYPOS));
		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.INCREMENT).isPresent()) {
			Optional<Node> expression = parseOperation();
			return Optional.of(new OperationNode(expression.get(), OperationNode.Operations.PREINC));
		} else if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.DECREMENT).isPresent()) {
			Optional<Node> expression = parseOperation();
			return Optional.of(new OperationNode(expression.get(), OperationNode.Operations.PREDEC));
		} else if (manage.MoreTokens() && (manage.peek(0).get().getTokenType().equals(Token.Tokentype.WORD)
				|| manage.peek(0).get().getTokenType().equals(Token.Tokentype.PRINT)
				|| manage.peek(0).get().getTokenType().equals(Token.Tokentype.PRINTF)
				|| manage.peek(0).get().getTokenType().equals(Token.Tokentype.GETLINE)
				|| manage.peek(0).get().getTokenType().equals(Token.Tokentype.EXIT)
				|| manage.peek(0).get().getTokenType().equals(Token.Tokentype.NEXTFILE)
				|| manage.peek(0).get().getTokenType().equals(Token.Tokentype.NEXT))) {
			Optional<Node> function = parseFunctionCall();
			if (function.isPresent()) {
				return function;
			}
			return parseLvalue();

		} else
			return parseLvalue();
	}

// This method checks for exponents.
	public Optional<Node> exponents() throws Exception {
		Node left = Factor().get();
		do {

			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens()) {
				operator = manage.MatchAndRemove(Token.Tokentype.EXPO);
			}
			if (operator.isEmpty()) {
				return Optional.of(left);

			}
			Optional<Node> right = And();
			if (operator.get().getTokenType().equals(Token.Tokentype.EXPO)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.EXPONENT));
			}
		} while (true);
	}

	// This method checks for an expression.
	public Optional<Node> Expression() throws Exception {
		Node left = Term().get();
		do {
			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens())
				operator = manage.MatchAndRemove(Token.Tokentype.PLUS);
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.MINUS);

			}

			if (operator.isEmpty()) {
				return Optional.of(left);
			}
			Optional<Node> right = Expression();
			if (operator.get().getTokenType().equals(Token.Tokentype.PLUS)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.ADD));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.MINUS)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.SUBTRACT));
			}
		} while (true);

	}

	// This method checks for the OR operation.
	public Optional<Node> Or() throws Exception {
		Node left = And().get();
		do {
			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens()) {
				operator = manage.MatchAndRemove(Token.Tokentype.OR);
			}
			if (operator.isEmpty()) {
				return Optional.of(left);

			}
			Optional<Node> right = And();
			if (operator.get().getTokenType().equals(Token.Tokentype.OR)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.OR));
			}
		} while (true);
	}

	// This method checks for the AND operation.
	public Optional<Node> And() throws Exception {
		Node left = Array().get();
		do {
			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens()) {
				operator = manage.MatchAndRemove(Token.Tokentype.AND);
			}
			if (operator.isEmpty()) {
				return Optional.of(left);

			}
			Optional<Node> right = Match();
			if (operator.get().getTokenType().equals(Token.Tokentype.AND)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.AND));

			}
		} while (true);
	}

	// This method checks for a match operation in awk.
	public Optional<Node> Match() throws Exception {
		Node left = Comparison().get();
		do {
			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens()) {
				operator = manage.MatchAndRemove(Token.Tokentype.MATCH);
			}

			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.NOT_MATCH);

			}
			if (operator.isEmpty()) {
				return Optional.of(left);

			}
			Optional<Node> right = Comparison();
			if (operator.get().getTokenType().equals(Token.Tokentype.MATCH)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.MATCH));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.NOT_MATCH)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.NOTMATCH));
			}
		} while (true);
	}

// This method checks for relational expressions
	public Optional<Node> Comparison() throws Exception {
		Node left = concatenation().get();
		do {
			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens())
				operator = manage.MatchAndRemove(Token.Tokentype.LESS_EQUAL);
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.GREATER_EQUAL);

			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.NOT_EQUAL);
			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.GREATER);
			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.LESS);
			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.EQUIVALENT);
			}
			if (operator.isEmpty()) {
				return Optional.of(left);

			}
			Optional<Node> right = parseOperation();
			if (operator.get().getTokenType().equals(Token.Tokentype.LESS_EQUAL)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.LE));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.GREATER_EQUAL)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.GE));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.NOT_EQUAL)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.NE));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.GREATER)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.GT));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.LESS)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.LT));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.EQUIVALENT)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.EQ));
			}

		} while (true);
	}

// This method checks for concatenation
	public Optional<Node> concatenation() throws Exception {
		Node left = Expression().get();
		do {
			Optional<Node> right = Optional.empty();
			// System.out.print(manage.peek(0).get().getVal() + " ");
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.LITERAL).isPresent()) {
				right = Expression();
			}
			if (right.isEmpty()) {
				return Optional.of(left);

			}
			return Optional.of(new OperationNode(left, right, OperationNode.Operations.CONCATENATION));
		} while (true);
	}

// This method checks for an array membership
	public Optional<Node> Array() throws Exception {
		Node expression = Match().get();
		do {
			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens())
				operator = manage.MatchAndRemove(Token.Tokentype.IN);
			if (operator.isEmpty()) {
				return Optional.of(expression);
			}
			Optional<Node> right = Match();
			return Optional.of(new OperationNode(expression, right, OperationNode.Operations.IN));

		} while (true);
	}

	// This method represents a term. It takes a factor and checks for the below
	// operations
	public Optional<Node> Term() throws Exception {
		Node left = exponents().get();
		// System.out.print(left.toString() + " ");
		do {
			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens())
				operator = manage.MatchAndRemove(Token.Tokentype.ASTERIK);
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.DIVIDE);

			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.MODULUS);
			}
			if (operator.isEmpty()) {
				return Optional.of(left);
			}
			Optional<Node> right = Term();
			if (operator.get().getTokenType().equals(Token.Tokentype.ASTERIK)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.MULTIPLY));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.DIVIDE)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.DIVIDE));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.MODULUS)) {
				return Optional.of(new OperationNode(left, right, OperationNode.Operations.MODULO));
			}

		} while (true);
	}

	// This method represents the factor, which can be a number, word or an
	// expression in ().
	public Optional<Node> Factor() throws Exception {
		Optional<Token> factor = Optional.empty();
		if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.OPEN_PARA).isPresent()) {
			Node expression = parseAssignment().get();
			if (expression == null) {
				throw new Exception();
			}
			// System.out.print(expression.toString() + " ");
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.CLOSED_PARA).isEmpty()) {
				throw new Exception();
			}
			return Optional.of(expression);
		}
		if (manage.MoreTokens())
			factor = manage.MatchAndRemove(Token.Tokentype.NUMBER);
		if (factor.isPresent()) {
			return Optional.of(new ConstantNode(factor.get().getVal()));
		}
		if (factor.isEmpty()) {
			AcceptSeparators();
			// System.out.print(manage.peek(0).get().getVal() + " ");
			return Optional.of(parseBottomlevel().get());
		}
		return null;
	}

// This method returns the potential left-hand value of an operation such as a dollar sign.
	public Optional<Node> parseLvalue() throws Exception {
		if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.DOLLAR).isPresent()) {
			Optional<Node> check = parseBottomlevel();
			if (check.isPresent()) {
				return Optional.of(new OperationNode(check.get(), OperationNode.Operations.DOLLAR));
			}
		}
		if (manage.MoreTokens() && manage.peek(0).get().getTokenType().equals(Token.Tokentype.WORD)) {
			Optional<Token> word = manage.MatchAndRemove(Token.Tokentype.WORD);
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.INCREMENT).isPresent()) {
				Node expression = new VariableReferenceNode(word.get().getVal());
				return Optional.of(new OperationNode(expression, OperationNode.Operations.POSTINC));
			}
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.DECREMENT).isPresent()) {
				Node expression = new VariableReferenceNode(word.get().getVal());
				return Optional.of(new OperationNode(expression, OperationNode.Operations.POSTDEC));
			}
			if (manage.MoreTokens() && manage.MoreTokens()
					&& manage.MatchAndRemove(Token.Tokentype.OPEN_BRACKET).isPresent()) {
				if (manage.MatchAndRemove(Token.Tokentype.CLOSED_BRACKET).isPresent()) {
					return Optional.of(new VariableReferenceNode(word.get().getVal(), Optional.empty()));
				}
				Optional<Node> index = Expression();
				if (!manage.MatchAndRemove(Token.Tokentype.CLOSED_BRACKET).isPresent()) {
					throw new Exception();
				}
				return Optional.of(new VariableReferenceNode(word.get().getVal(), index));
			} else {
				return Optional.of(new VariableReferenceNode(word.get().getVal()));
			}

		}
		return Optional.empty();
	}

	// This method collects expressions and makes an assignment node.
	public Optional<Node> parseAssignment() throws Exception {
		Node left = parseTernary().get();
		do {
			Optional<Token> operator = Optional.empty();
			if (manage.MoreTokens())
				operator = manage.MatchAndRemove(Token.Tokentype.PLUS_EQUAL);
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.MINUS_EQUAL);
			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.MODULUS_EQUAL);
			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.PRODUCT_EQUAL);
			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.ASSIGN);
			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.DIVISION_EQUAL);
			}
			if (operator.isEmpty()) {
				if (manage.MoreTokens())
					operator = manage.MatchAndRemove(Token.Tokentype.EXPO_EQUAL);
			}
			if (operator.isEmpty()) {
				return Optional.of(left);
			}
			Optional<Node> right = Or();
			if (operator.get().getTokenType().equals(Token.Tokentype.PLUS_EQUAL)) {
				Node rhs = new OperationNode(left, right, OperationNode.Operations.ADD);
				return Optional.of(new AssignmentNode(left, rhs));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.MINUS_EQUAL)) {
				Node rhs = new OperationNode(left, right, OperationNode.Operations.SUBTRACT);
				return Optional.of(new AssignmentNode(left, rhs));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.PRODUCT_EQUAL)) {
				Node rhs = new OperationNode(left, right, OperationNode.Operations.MULTIPLY);
				return Optional.of(new AssignmentNode(left, rhs));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.DIVISION_EQUAL)) {
				Node rhs = new OperationNode(left, right, OperationNode.Operations.DIVIDE);
				return Optional.of(new AssignmentNode(left, rhs));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.MODULUS_EQUAL)) {
				Node rhs = new OperationNode(left, right, OperationNode.Operations.MODULO);
				return Optional.of(new AssignmentNode(left, rhs));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.EXPO_EQUAL)) {
				Node rhs = new OperationNode(left, right, OperationNode.Operations.EXPONENT);
				return Optional.of(new AssignmentNode(left, rhs));
			}
			if (operator.get().getTokenType().equals(Token.Tokentype.ASSIGN)) {
				Node rhs = right.get();
				return Optional.of(new AssignmentNode(left, rhs));
			}

		} while (true);
	}

	// This method collects expressions and makes a ternary node.
	public Optional<Node> parseTernary() throws Exception {
		Optional<Node> expression = Or();
		// System.out.print(expression.get().toString() + " ");
		do {
			Optional<Node> true_ = Optional.empty();
			Optional<Node> false_ = Optional.empty();
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.QUESTION).isPresent()) {
				// System.out.print(manage.peek(0).get().getVal());
				true_ = parseAssignment();
			} else {
				return expression;
			}
			// System.out.print(true_.get().toString() + " ");
			if (manage.MoreTokens() && manage.MatchAndRemove(Token.Tokentype.COLON).isPresent()) {
				false_ = parseAssignment();
			}
			// System.out.println(false_.get().toString());
			else {
				throw new Exception();
			}
			return Optional.of(new TernaryNode(expression, true_, false_));
		} while (true);

	}

	// This is a temporary method that I made to print the operations and constants
	// for now.
	public String display() throws Exception {
		String result = "";
		while (manage.MoreTokens()) {
			// manage.MatchAndRemove(Token.Tokentype.OPEN_BRACE);
			result += parseOperation().get().toString();
			// manage.MatchAndRemove(Token.Tokentype.CLOSED_BRACE);
		}
		return result;
	}

	public String built() throws Exception {
		LinkedList<StatementNode> list = parseBlock().getList();
		String result = "";
		for (int i = 0; i < list.size(); i++) {
			result += list.get(i);
		}
		return result;
	}

// This method is for testing purposes and can be ignored.
	public static void main(String args[]) throws Exception {
		String test = "# awk_script.awk\r\n" + "\r\n" + "BEGIN {\r\n" + "    print \"Processing begins...\"\r\n"
				+ "    sum = 0\r\n" + "}\r\n" + "\r\n" + "{\r\n" + "    # Code for processing each record goes here\r\n"
				+ "    sum += $1\r\n" + "}\r\n" + "\r\n" + "END {\r\n" + "    print \"Processing ends...\"\r\n"
				+ "    print \"Sum of the first column:\", sum\r\n" + "}\r\n" + "";
		Lexer lexer = new Lexer(test);
		lexer.Lex();
		// lexer.display();
		Parser parser = new Parser(lexer.getList());
		// BlockNode block = parser.parseBlock();
		ProgramNode program = parser.Parse();
		// System.out.println(parser.display());
		// LinkedList<StatementNode> list = block.getList();
		System.out.println(program);
		// System.out.println(list.size());
		// for (int i = 0; i < list.size(); i++) {
		// System.out.println(list.get(i));
		// }

	}
}
