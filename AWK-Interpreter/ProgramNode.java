import java.util.LinkedList;

/**
 * This class represents an entire awk program. It contains BEGIN blocks, END
 * blocks, other blocks, and functions. Every time we make a new structure, we
 * add it to the list of those structures present in this class.
 * 
 * @author yuvan
 *
 */
public class ProgramNode extends Node {
	LinkedList<BlockNode> Begin;
	LinkedList<BlockNode> End;
	LinkedList<FunctionDefinitionNode> functions;
	LinkedList<BlockNode> others;

	public ProgramNode() {
		functions = new LinkedList<>();
		Begin = new LinkedList<>();
		End = new LinkedList<>();
		others = new LinkedList<>();
	}

	public void addBegin(BlockNode begin) {
		Begin.add(begin);
	}

	public void addEnd(BlockNode end) {
		End.add(end);
	}

	public void addFunction(FunctionDefinitionNode function) {
		functions.add(function);
	}

	public void addOthers(BlockNode node) {
		others.add(node);
	}

	public String toString() {
		String begin = "";
		String middle = "";
		String function = "";
		String end = "";
		String program = "";
		for (int i = 0; i < Begin.size(); i++) {
			begin += Begin.get(i);
		}
		for (int i = 0; i < others.size(); i++) {
			middle += others.get(i);
		}
		for (int i = 0; i < functions.size(); i++) {
			middle += functions.get(i);
		}

		for (int i = 0; i < End.size(); i++) {
			end += End.get(i);
		}
		if (Begin.size() != 0) {
			program += "BEGIN " + begin;
		}
		if (others.size() != 0) {
			program += middle;
		}
		if (functions.size() != 0) {
			program += " {" + functions + "}";
		}
		if (End.size() != 0) {
			program += "END " + end;
		}
		return program;
	}
}
