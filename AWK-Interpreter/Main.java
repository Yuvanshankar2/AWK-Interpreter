import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/*
 * This class takes in a file and feeds it to the lexer which tokenizes it. It then displays
 * all the tokens.
 */
public class Main {
	public static void main(String args[]) throws Exception {
		Path path = Paths.get(args[0]); // The args[0] stores a file path.
		String data = new String(Files.readAllBytes(path));
		Lexer lexer = new Lexer(data);
		lexer.Lex();
		lexer.display();
	}
}
