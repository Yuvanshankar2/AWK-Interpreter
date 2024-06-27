import org.junit.Assert;
import org.junit.Test;

/**
 * This class is used for testing the parser.
 * 
 * @author yuvan
 *
 */
public class UnitTest_Parser {

	@Test
	public void TokenManagertest() throws Exception {
		Lexer lexer = new Lexer("This is ICSI computer science");
		lexer.Lex();
		TokenManager manager = new TokenManager(lexer.getList());
		Assert.assertTrue(manager.MoreTokens());
		Assert.assertEquals("ICSI", manager.peek(2).get().getVal());
		manager.MatchAndRemove(Token.Tokentype.WORD);
		Assert.assertEquals("is", manager.peek(0).get().getVal());
	}

	@Test
	public void parse() throws Exception {
		Lexer lexer = new Lexer("a+b=3");
		lexer.Lex();
		Parser parser = new Parser(lexer.getList());
		Assert.assertEquals(
				"AssignmentNode(Operation Node(VariableReference Node(a),ADD,VariableReference Node(b)),Constant node(3))",
				parser.display());
		Lexer lexer1 = new Lexer("a*=7");
		lexer1.Lex();
		Parser parser1 = new Parser(lexer1.getList());
		Assert.assertEquals(
				"AssignmentNode(VariableReference Node(a),Operation Node(VariableReference Node(a),MULTIPLY,Constant node(7)))",
				parser1.display());
		Lexer lexer2 = new Lexer("(a&&b)||(c&&d)");
		lexer2.Lex();
		Parser parser2 = new Parser(lexer2.getList());
		Assert.assertEquals(
				"Operation Node(Operation Node(VariableReference Node(a),AND,VariableReference Node(b)),OR,Operation Node(VariableReference Node(c),AND,VariableReference Node(d)))",
				parser2.display());
		Lexer lexer3 = new Lexer("i in arr[]");
		lexer3.Lex();
		Parser parser3 = new Parser(lexer3.getList());
		Assert.assertEquals("Operation Node(VariableReference Node(i),IN,VariableReference Node(arr))",
				parser3.display());
		Lexer lexer4 = new Lexer("(a==5)?(a++):(a--)");
		lexer4.Lex();
		Parser parser4 = new Parser(lexer4.getList());
		Assert.assertEquals(
				"TernaryNode( Operation Node(VariableReference Node(a),EQ,Constant node(5)),?,Operation Node(VariableReference Node(a),POSTINC):,Operation Node(VariableReference Node(a),POSTDEC))",
				parser4.display());
		Lexer lexer5 = new Lexer("a^3=5");
		lexer5.Lex();
		Parser parser5 = new Parser(lexer5.getList());
		Assert.assertEquals(
				"AssignmentNode(Operation Node(VariableReference Node(a),EXPONENT,Constant node(3)),Constant node(5))",
				parser5.display());
	}

	@Test
	public void parseFull() throws Exception {
		Lexer lexer = new Lexer("{return a;}");
		lexer.Lex();
		Parser parser = new Parser(lexer.getList());
		Assert.assertEquals("return VariableReference Node(a);", parser.parseBlock().getList().get(0).toString());

		Lexer lexer1 = new Lexer("{continue;}");
		lexer1.Lex();
		Parser parser1 = new Parser(lexer1.getList());
		Assert.assertEquals("continue", parser1.parseBlock().getList().get(0).toString());
		Lexer lexer2 = new Lexer("{break;}");
		lexer2.Lex();
		Parser parser2 = new Parser(lexer2.getList());
		Assert.assertEquals("break", parser2.parseBlock().getList().get(0).toString());
		Lexer lexer3 = new Lexer("delete arr[];");
		lexer3.Lex();
		Parser parser3 = new Parser(lexer3.getList());
		Assert.assertEquals("delete VariableReference Node(arr)", parser3.parseBlock().getList().get(0).toString());
		Lexer lexer4 = new Lexer("{if(a<2){return a;}}");
		lexer4.Lex();
		Parser parser4 = new Parser(lexer4.getList());
		Assert.assertEquals(
				"if(Operation Node(VariableReference Node(a),LT,Constant node(2))){return VariableReference Node(a); }",
				parser4.parseBlock().getList().get(0).toString());

	}

	@Test
	public void parseBuiltIn() throws Exception {
		Lexer lexer = new Lexer("{exit}");
		lexer.Lex();
		Parser parser = new Parser(lexer.getList());
		Assert.assertEquals("FunctionNode(exit)", parser.built());
		Lexer lexer1 = new Lexer("{next}");
		lexer1.Lex();
		Parser parser1 = new Parser(lexer1.getList());
		Assert.assertEquals("FunctionNode(next)", parser1.built());
		Lexer lexer2 = new Lexer("{getline a;}");
		lexer2.Lex();
		Parser parser2 = new Parser(lexer2.getList());
		Assert.assertEquals("FunctionNode(getline  VariableReference Node(a))", parser2.built());
		Lexer lexer3 = new Lexer("{print \"Hello\";}");
		lexer3.Lex();
		Parser parser3 = new Parser(lexer3.getList());
		Assert.assertEquals("FunctionNode(print  Constant node(Hello))", parser3.built());

	}

}
