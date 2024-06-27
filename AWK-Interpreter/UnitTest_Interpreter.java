
/*
 * This class is used to test our interpreter.
 */
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import org.junit.Assert;
import org.junit.Test;

public class UnitTest_Interpreter {
	@Test
	public void splitTest() throws IOException {
		ProgramNode program = new ProgramNode();
		Interpreter interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		HashMap<String, InterpreterDatatype> arguments = new HashMap<>();
		//
		arguments.put("arg1", new InterpreterDatatype("Hello"));
		BuiltInFunctionDefinitionNode length = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("length");
		Assert.assertEquals("5", length.execute.apply(arguments));
		//
		arguments.put("arg1", new InterpreterDatatype("Hello"));
		BuiltInFunctionDefinitionNode toupper = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("toupper");
		Assert.assertEquals("HELLO", toupper.execute.apply(arguments));
		//
		arguments.put("arg1", new InterpreterDatatype("Hello"));
		BuiltInFunctionDefinitionNode tolower = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("tolower");
		Assert.assertEquals("hello", tolower.execute.apply(arguments));
		//
		arguments.put("arg1", new InterpreterDatatype("Hello this"));
		arguments.put("substring", new InterpreterDatatype("this"));
		BuiltInFunctionDefinitionNode index = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("index");
		Assert.assertEquals("6", index.execute.apply(arguments));
		//
		arguments.put("regular expression", new InterpreterDatatype("\\bis\\b"));
		arguments.put("arg1", new InterpreterDatatype("Hello this is an example file"));
		BuiltInFunctionDefinitionNode match = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("match");
		Assert.assertEquals("11", match.execute.apply(arguments));
		//
		arguments.put("regular expression", new InterpreterDatatype("\\bis\\b"));
		arguments.put("arg1", new InterpreterDatatype("Hello this is an example file"));
		BuiltInFunctionDefinitionNode sub = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("sub");
		Assert.assertEquals("Hello this Replaced an example file", sub.execute.apply(arguments));
		//
		arguments.put("regular expression", new InterpreterDatatype("\\bis\\b"));
		arguments.put("arg1", new InterpreterDatatype("Hello this is an example of an is file"));
		BuiltInFunctionDefinitionNode gsub = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("gsub");
		Assert.assertEquals("Hello this Replaced an example of an Replaced file", gsub.execute.apply(arguments));
		//
		HashMap<String, InterpreterDatatype> arrayMap = new HashMap<>();
		arrayMap.put("0", new InterpreterDatatype("a"));
		arrayMap.put("1", new InterpreterDatatype("b"));
		arrayMap.put("2", new InterpreterDatatype("c"));
		arguments.put("arg1", new InterpreterArrayDatatype(arrayMap));
		BuiltInFunctionDefinitionNode print = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("print");
		Assert.assertEquals("", print.execute.apply(arguments));
		//
		HashMap<String, InterpreterDatatype> arrayMap1 = new HashMap<>();
		arrayMap1.put("0", new InterpreterDatatype("Hello"));
		arrayMap1.put("1", new InterpreterDatatype("b"));
		arrayMap1.put("2", new InterpreterDatatype("c"));
		arguments.put("arg1", new InterpreterArrayDatatype(arrayMap));
		BuiltInFunctionDefinitionNode printf = (BuiltInFunctionDefinitionNode) interpreter.function_map.get("printf");
		Assert.assertEquals("", print.execute.apply(arguments));
		//
	}

	/**
	 * This method tests the implementations that we aimed to do with the parsed
	 * node tree that we have.
	 * 
	 * @throws Exception
	 */
	@Test
	public void Interpretation() throws Exception {
		ProgramNode program = new ProgramNode();
		Interpreter interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		String test = "{a=4}";
		Lexer lexer = new Lexer(test);
		lexer.Lex();
		Parser parser = new Parser(lexer.getList());
		BlockNode block = parser.parseBlock();
		LinkedList<StatementNode> list = block.getList();
		HashMap<String, InterpreterDatatype> localmap = new HashMap<>();
		InterpreterDatatype type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("4", type.getValue());
		Assert.assertEquals("4", interpreter.globalvariable_map.get("a").getValue());
		interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		test = "{a=4 ++a}";
		lexer = new Lexer(test);
		lexer.Lex();
		parser = new Parser(lexer.getList());
		block = parser.parseBlock();
		list = block.getList();
		localmap = new HashMap<>();
		type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("5.0", type.getValue());
		Assert.assertEquals("5.0", interpreter.globalvariable_map.get("a").getValue());
		interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		test = "{a=4 a/=2}";
		lexer = new Lexer(test);
		lexer.Lex();
		parser = new Parser(lexer.getList());
		block = parser.parseBlock();
		list = block.getList();
		localmap = new HashMap<>();
		type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("2.0", type.getValue());
		interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		test = "{a=4+11}";
		lexer = new Lexer(test);
		lexer.Lex();
		parser = new Parser(lexer.getList());
		block = parser.parseBlock();
		list = block.getList();
		localmap = new HashMap<>();
		type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("15.0", type.getValue());
		Assert.assertEquals("15.0", interpreter.globalvariable_map.get("a").getValue());
		interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		test = "{a=4 b=a+7}";
		lexer = new Lexer(test);
		lexer.Lex();
		parser = new Parser(lexer.getList());
		block = parser.parseBlock();
		list = block.getList();
		localmap = new HashMap<>();
		type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("11.0", type.getValue());
		Assert.assertEquals("11.0", interpreter.globalvariable_map.get("b").getValue());
		interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		test = "{a=10 (a>5)?a+=2:--a}";
		lexer = new Lexer(test);
		lexer.Lex();
		parser = new Parser(lexer.getList());
		block = parser.parseBlock();
		list = block.getList();
		localmap = new HashMap<>();
		type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("12.0", type.getValue());
		interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		test = "{a=2 (a>5)?a+=2:--a}";
		lexer = new Lexer(test);
		lexer.Lex();
		parser = new Parser(lexer.getList());
		block = parser.parseBlock();
		list = block.getList();
		localmap = new HashMap<>();
		type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("1.0", type.getValue());
		Assert.assertEquals("1.0", interpreter.globalvariable_map.get("a").getValue());
		interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		test = "{a=\"true\" b=0 (a||b)?b+=2:++b}";
		lexer = new Lexer(test);
		lexer.Lex();
		parser = new Parser(lexer.getList());
		block = parser.parseBlock();
		list = block.getList();
		localmap = new HashMap<>();
		type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("1.0", type.getValue());
		Assert.assertEquals("1.0", interpreter.globalvariable_map.get("b").getValue());
		interpreter = new Interpreter(program, "C:\\Users\\yuvan\\input.txt");
		test = "{a=1 b=1 (a&&b)?b+=2:++b}";
		lexer = new Lexer(test);
		lexer.Lex();
		parser = new Parser(lexer.getList());
		block = parser.parseBlock();
		list = block.getList();
		localmap = new HashMap<>();
		type = null;
		for (int i = 0; i < list.size(); i++) {
			type = interpreter.GetIDT(list.get(i), localmap);
		}
		Assert.assertEquals("3.0", type.getValue());
		Assert.assertEquals("3.0", interpreter.globalvariable_map.get("b").getValue());

	}
}
