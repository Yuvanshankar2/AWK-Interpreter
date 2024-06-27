
/*
 * This class represents a "list" of interpreter data types.
 */
import java.util.HashMap;

public class InterpreterArrayDatatype extends InterpreterDatatype {
	HashMap<String, InterpreterDatatype> variable_storage;

	public InterpreterArrayDatatype(HashMap<String, InterpreterDatatype> variable_storage) {
		super();
		this.variable_storage = variable_storage;
	}

	public HashMap<String, InterpreterDatatype> getMap() {
		return variable_storage;
	}

}
