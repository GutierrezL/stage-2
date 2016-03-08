/**
* <h1>InvalidPositiveInteger</h1>
* This class extends an exception. It will be thrown when a negative integer or
* zero is assigned to a field requiring a positive integer
*
* @author Luis Alberto Gutierrez Iglesias
* @version 1.0
* @since 2016-02-17
*/
public class InvalidPositiveInteger extends Exception{

	private static final long serialVersionUID = 1L;

	public InvalidPositiveInteger(int number) {
		super("'" + number + "' is not a valid positive integer.");
	}

}
