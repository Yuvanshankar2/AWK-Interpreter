import java.util.LinkedList;
import java.util.Optional;

/**
 * This class takes in a linked list of tokens from the lexer and allows us to
 * perform some operations on them. These operations will eventually help us
 * make structures in awk programs.
 * 
 * @author yuvan
 *
 */
public class TokenManager {
	private LinkedList<Token> list = new LinkedList<>();
	private int index = 0;

	public TokenManager(LinkedList<Token> list) {
		this.list = list;
	}

	public Optional<Token> peek(int j) {
		if (j > list.size()) {
			return null;
		}
		return Optional.of(list.get(j));
	}

	public boolean MoreTokens() {
		if (list.size() != 0) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unused")
	public Optional<Token> MatchAndRemove(Token.Tokentype t) {
		if (list.get(0).getTokenType().equals(t)) {
			return Optional.of(list.remove());
		} else {
			return Optional.empty();
		}
	}

	public static void main(String args[]) throws Exception {
		Lexer lexer = new Lexer("This is ICSI 311");
		lexer.Lex();
		lexer.display();
		// TokenManager token = new TokenManager(lexer.getList());
		// System.out.print(token.MoreTokens());
	}
}
