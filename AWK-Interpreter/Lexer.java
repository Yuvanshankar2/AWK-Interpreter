
/*
 * This is the lexer class which will take in a string value and divide it into tokens. It then
 * displays the tokens in the form of a linked list. 
 */
import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {
	private LinkedList<Token> symbol_table = new LinkedList<>(); // A linked list that will store tokens
	private int line = 0;
	private int pos = 0;
	private String val;
	private HashMap<String, Token.Tokentype> keyword;
	private HashMap<Character, Token.Tokentype> single_character;
	private HashMap<String, Token.Tokentype> double_character;
	Stringhandler handle;

	public Lexer(String content) {
		val = content;
		handle = new Stringhandler(content);
		keyword = new HashMap<>();
		single_character = new HashMap<>();
		double_character = new HashMap<>();
		collect();
		collect_SingleSymbol();
		collect_DoubleSymbol();
	}

	// This method breaks the string into characters and tokenizes the values by
	// analyzing each character.
	public void Lex() throws Exception {
		while (!handle.isDone()) {
			if (Character.isAlphabetic(handle.peek(0)) || handle.peek(0) == '_') {
				ProcessWord();

			} else if (Character.isDigit(handle.peek(0))) {
				ProcessDigits();

			} else if (handle.peek(0) == ' ' || handle.peek(0) == '\t') {
				handle.getChar();
				pos++;

			} else if (handle.peek(0) == '\n') {
				symbol_table.add(new Token(Token.Tokentype.SEPERATOR, pos, line));
				line++;
				pos = 0;
				handle.swallow(1);

				;
			} else if (handle.peek(0) == '\r') {
				handle.swallow(1);
				pos++;

			} else if (handle.peek(0) == '#') {
				while (!handle.isDone() && handle.peek(0) != '\n') {
					handle.swallow(1);
				}

			} else if (single_character.containsKey(handle.peek(0)) || handle.peek(0) == '&') {
				ProcessSymbol();
			} else if (handle.peek(0) == '`') {
				HandlePattern();
			} else if (handle.peek(0) == '"') {
				HandleLiteral();
			}
		}

	}

// This method helps us collect all the important keywords
	public void collect() {
		keyword.put("for", Token.Tokentype.FOR);
		keyword.put("while", Token.Tokentype.WHILE);
		keyword.put("do", Token.Tokentype.DO);
		keyword.put("if", Token.Tokentype.IF);
		keyword.put("break", Token.Tokentype.BREAK);
		keyword.put("continue", Token.Tokentype.CONTINUE);
		keyword.put("else", Token.Tokentype.ELSE);
		keyword.put("elseif", Token.Tokentype.ELSE_IF);
		keyword.put("return", Token.Tokentype.RETURN);
		keyword.put("BEGIN", Token.Tokentype.BEGIN);
		keyword.put("END", Token.Tokentype.END);
		keyword.put("print", Token.Tokentype.PRINT);
		keyword.put("printf", Token.Tokentype.PRINTF);
		keyword.put("next", Token.Tokentype.NEXT);
		keyword.put("in", Token.Tokentype.IN);
		keyword.put("delete", Token.Tokentype.DELETE);
		keyword.put("getline", Token.Tokentype.GETLINE);
		keyword.put("exit", Token.Tokentype.EXIT);
		keyword.put("nextfile", Token.Tokentype.NEXTFILE);
		keyword.put("function", Token.Tokentype.FUNCTION);

	}

// This method makes adds all the important symbols along with their token type in a hashmap.
	public void collect_SingleSymbol() {
		single_character.put('{', Token.Tokentype.OPEN_BRACE);
		single_character.put('}', Token.Tokentype.CLOSED_BRACE);
		single_character.put('[', Token.Tokentype.OPEN_BRACKET);
		single_character.put(']', Token.Tokentype.CLOSED_BRACKET);
		single_character.put('(', Token.Tokentype.OPEN_PARA);
		single_character.put(')', Token.Tokentype.CLOSED_PARA);
		single_character.put('+', Token.Tokentype.PLUS);
		single_character.put('=', Token.Tokentype.ASSIGN);
		single_character.put('-', Token.Tokentype.MINUS);
		single_character.put('*', Token.Tokentype.ASTERIK);
		single_character.put(':', Token.Tokentype.COLON);
		single_character.put(';', Token.Tokentype.SEPERATOR);
		single_character.put('%', Token.Tokentype.MODULUS);
		single_character.put('^', Token.Tokentype.EXPO);
		single_character.put('?', Token.Tokentype.QUESTION);
		single_character.put('/', Token.Tokentype.DIVIDE);
		single_character.put('|', Token.Tokentype.PIPE);
		single_character.put(',', Token.Tokentype.COMMA);
		single_character.put('!', Token.Tokentype.NOT);
		single_character.put('\n', Token.Tokentype.SEPERATOR);
		single_character.put('>', Token.Tokentype.GREATER);
		single_character.put('<', Token.Tokentype.LESS);
		single_character.put('$', Token.Tokentype.DOLLAR);
		single_character.put('~', Token.Tokentype.MATCH);
		single_character.put(' ', Token.Tokentype.SEPERATOR);

	}

// This method makes adds all the important symbols along with their token type in a hashmap.
	public void collect_DoubleSymbol() {
		double_character.put("++", Token.Tokentype.INCREMENT);
		double_character.put("==", Token.Tokentype.EQUIVALENT);
		double_character.put("--", Token.Tokentype.DECREMENT);
		double_character.put("+=", Token.Tokentype.PLUS_EQUAL);
		double_character.put(">=", Token.Tokentype.GREATER_EQUAL);
		double_character.put("<=", Token.Tokentype.LESS_EQUAL);
		double_character.put("!=", Token.Tokentype.NOT_EQUAL);
		double_character.put("^=", Token.Tokentype.EXPO_EQUAL);
		double_character.put("%=", Token.Tokentype.MODULUS_EQUAL);
		double_character.put("*=", Token.Tokentype.PRODUCT_EQUAL);
		double_character.put("/=", Token.Tokentype.DIVISION_EQUAL);
		double_character.put("-=", Token.Tokentype.MINUS_EQUAL);
		double_character.put(">>", Token.Tokentype.APPEND);
		double_character.put("&&", Token.Tokentype.AND);
		double_character.put("||", Token.Tokentype.OR);
		double_character.put("!~", Token.Tokentype.NOT_MATCH);

	}

	/*
	 * This method is made especially for values starting with a letter. It checks
	 * if the following characters before the next separator can be joined together
	 * to form a word. Values with special characters are not words and will not be
	 * considered as a word token.
	 * 
	 */
	public void ProcessWord() throws Exception {
		String word = "";
		while (!handle.isDone() && handle.peek(0) != ' ' && handle.peek(0) != '\n' && handle.peek(0) != '\r') {
			if (handle.peek(0) != '_' && !Character.isLetterOrDigit(handle.peek(0))) {
				if (keyword.containsKey(word)) {
					symbol_table.add(new Token(keyword.get(word), line, pos));
				} else
					symbol_table.add(new Token(Token.Tokentype.WORD, line, pos, word));
				return;
			}
			word += handle.peek(0) + "";
			handle.swallow(1);
			pos++;
		}
		if (keyword.containsKey(word)) {
			symbol_table.add(new Token(keyword.get(word), line, pos));
		} else
			symbol_table.add(new Token(Token.Tokentype.WORD, line, pos, word));
	}

// This method helps us make a token out of the literals in the input
	public void HandleLiteral() throws Exception {
		String literal = "";
		handle.swallow(1);
		while (!handle.isDone() && handle.peek(0) != '"') {
			if (handle.PeekString(2).equals("\\\"")) {
				literal += handle.PeekString(2);
				handle.swallow(2);
			} else {
				literal += handle.peek(0) + "";
				handle.swallow(1);
			}
		}

		handle.swallow(1);
		symbol_table.add(new Token(Token.Tokentype.LITERAL, line, pos, literal));

	}

	/*
	 * The method below is made for values starting with a digit. It checks if the
	 * following characters before the next separator can be joined together to form
	 * a number. Values with special characters or with letters in between are not
	 * numbers and the method below will throw an exception.
	 */
	public void ProcessDigits() {
		try {
			String number = "";
			while (!handle.isDone() && handle.peek(0) != ' ' && handle.peek(0) != '\n' && handle.peek(0) != '\r') {
				if (handle.peek(0) != '_' && !Character.isLetterOrDigit(handle.peek(0)) && handle.peek(0) != '.') {
					double num = Double.parseDouble(number);
					symbol_table.add(new Token(Token.Tokentype.NUMBER, line, pos, number));
					return;
				}
				number += handle.peek(0) + "";
				handle.swallow(1);
				pos++;
			}
			double num = Double.parseDouble(number);
			symbol_table.add(new Token(Token.Tokentype.NUMBER, line, pos, number));
		} catch (NumberFormatException nfe) {
			System.out.println("This is an invalid number!");
		}
	}

	// This method helps us view the tokens in the form of a linked list.
	public void display() {
		for (int i = 0; i < symbol_table.size(); i++) {
			System.out.println(symbol_table.get(i) + " ");
		}
	}

// This method helps us make tokens out of the single and double symbols
	public void ProcessSymbol() throws Exception {
		if (double_character.containsKey(handle.PeekString(2))) {
			symbol_table.add(new Token(double_character.get(handle.PeekString(2)), line, pos, handle.PeekString(2)));
			pos += 2;
			handle.swallow(2);
		} else if (single_character.containsKey(handle.peek(0))) {
			symbol_table.add(new Token(single_character.get(handle.peek(0)), line, pos, handle.PeekString(1)));
			pos++;
			handle.swallow(1);
		} else {
			throw new Exception("Invalid symbol!");
		}
	}

// This method helps us make tokens out of regular expressions
	public void HandlePattern() {
		String expression = "";
		handle.swallow(1);
		while (!handle.isDone() && handle.peek(0) != '`') {
			if (handle.PeekString(2).equals("\\`")) {
				expression += handle.PeekString(2);
				handle.swallow(2);
				pos += 2;
			} else {
				expression = expression + handle.peek(0);
				handle.swallow(1);
				pos++;
			}
		}
		symbol_table.add(new Token(Token.Tokentype.REGULAR_EXPRESSIONS, line, pos, expression));
		handle.swallow(1);
	}

// This helps us get the final linked list of tokens.
	public LinkedList<Token> getList() {
		return symbol_table;
	}

// The method below is used for testing purposes.
	public static void main(String args[]) throws Exception {
		// System.out.println("\"Hello\"");
		Lexer lexer = new Lexer("{sum =sum+NF} END {print sum}");
		lexer.Lex();
		lexer.display();
	}
}
