/*
 * This class classifies a token as a word,number, or a separator. We will tokenize the input content
 * using this format (the toString method).
 */
public class Token {
	public enum Tokentype {
		WORD, NUMBER, SEPERATOR, FOR, WHILE, IF, DO, BREAK, CONTINUE, ELSE, ELSE_IF, RETURN, BEGIN, END, PRINT, PRINTF,
		NEXT, IN, DELETE, GETLINE, EXIT, NEXTFILE, FUNCTION, LITERAL, SINGLE_CHARACTER_SYMBOL, DOUBLE_CHARACTER_SYMBOL,
		REGULAR_EXPRESSIONS, INCREMENT, DECREMENT, PLUS_EQUAL, MINUS_EQUAL, PRODUCT_EQUAL, DIVISION_EQUAL, EQUIVALENT,
		LESS_EQUAL, GREATER_EQUAL, NOT_EQUAL, MODULUS_EQUAL, EXPO_EQUAL, NOT_MATCH, AND, OR, APPEND, MATCH, OPEN_PARA,
		CLOSED_PARA, OPEN_BRACE, CLOSED_BRACE, OPEN_BRACKET, CLOSED_BRACKET, DOLLAR, CONGRUENCE, ASSIGN, LESS, GREATER,
		NOT, PLUS, EXPO, MINUS, QUESTION, COLON, ASTERIK, DIVIDE, MODULUS, PIPE, COMMA
	};

	private String val;
	private int line; // Tells us which line the index is currently at.
	private int pos; // Tells us a character's position within the line.
	private Tokentype type; // Tells us the type of token.

	public Token(Tokentype type, int line, int pos, String val) {
		this.type = type;
		this.line = line;
		this.pos = pos;
		this.val = val;
	}

	public Token(Tokentype type, int line, int pos) {
		this.type = type;
		this.line = line;
		this.pos = pos;
	}

	public Token.Tokentype getTokenType() {
		return type;
	}

	public String getVal() {
		return val;
	}

// Returns a token entry, which consists of a value and its token type. 
	public String toString() {
		return type + "(" + val + ")" + " ";
	}

// The method below is used only for testing purposes.
	public static void main(String args[]) {
		Token t = new Token(Tokentype.WORD, 2, 3, "hello");
		System.out.print(t.getTokenType());
	}
}
