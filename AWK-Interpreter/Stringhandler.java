/*
 * This class gives us the operations we need to use to build a lexer.
 */
public class Stringhandler {
	public String file;
	public int index = -1; // This is the pointer that goes through every character in the string.

	public Stringhandler(String file) {
		this.file = file;
	}

// The peek method moves ahead by a certain number of positions and returns the character at
	// that point/
	public char peek(int i) {
		if (index + i + 1 <= file.length() - 1)
			return file.charAt(index + i + 1);
		else {
			return '0';
		}
	}

	// This method returns the string of values up until a certain position
	public String PeekString(int i) {
		String representation = "";
		if (!isDone() && index + i <= file.length() - 1) {
			for (int j = index + 1; j <= index + i; j++) {
				representation += file.charAt(j) + "";
			}
			return representation;
		}
		return "End of the Line";
	}

// This method returns the next character and moves the index.
	public char getChar() {
		if (!isDone()) {
			char ch = file.charAt(index + 1);
			index++;
			return ch;
		}
		return '0';
	}

// This method moves the index by a certain number of positions.
	public void swallow(int i) {
		if (!isDone() && index + i + 1 <= file.length())
			index += i;
		else
			System.out.print("End of the file");
	}

// This method checks if we have reached the end of the file.
	public boolean isDone() {
		if (index == file.length() - 1)
			return true;

		return false;
	}

// This method returns the rest of the document in the form of a string starting from the current index.
	public String remainder() {
		if (!isDone())
			return file.substring(index + 1);
		else
			return "End of the file";
	}

// The method below is made only for testing purposes.
	public static void main(String args[]) {
		Stringhandler s = new Stringhandler("Heaven");
		s.swallow(3);
		System.out.println(s.peek(0));
		// System.out.println(s.remainder());

	}

}
