import org.junit.Assert;
import org.junit.Test;

/*
 * This class tests different types of values and tokenizes them.
 */
public class UnitTest {

	@Test
	public void test() throws Exception {
		Lexer lexer = new Lexer("Hello");
		lexer.Lex();
		Assert.assertEquals(1, lexer.getList().size());
		Assert.assertEquals(new Token(Token.Tokentype.WORD, 0, 5, "Hello").toString(),
				lexer.getList().get(0).toString());
		Lexer lexer1 = new Lexer("1 2 3");
		lexer1.Lex();
		Assert.assertEquals(new Token(Token.Tokentype.NUMBER, 0, 1, "1").toString(),
				lexer1.getList().get(0).toString());
		Assert.assertEquals(new Token(Token.Tokentype.NUMBER, 0, 3, "2").toString(),
				lexer1.getList().get(1).toString());
		Assert.assertEquals(new Token(Token.Tokentype.NUMBER, 0, 5, "3").toString(),
				lexer1.getList().get(2).toString());
		Lexer lexer3 = new Lexer("Cristiano Ronaldo 7");
		lexer3.Lex();
		Assert.assertEquals((new Token(Token.Tokentype.WORD, 0, 9, "Cristiano").toString()),
				lexer3.getList().get(0).toString());
		Assert.assertEquals((new Token(Token.Tokentype.WORD, 0, 17, "Ronaldo").toString()),
				lexer3.getList().get(1).toString());
		Assert.assertEquals((new Token(Token.Tokentype.NUMBER, 0, 19, "7").toString()),
				lexer3.getList().get(2).toString());
		Lexer lexer4 = new Lexer("I am the best\nI love this");
		lexer4.Lex();
		Assert.assertEquals((new Token(Token.Tokentype.SEPERATOR, 0, 14).toString()),
				lexer4.getList().get(4).toString());
	}

}
