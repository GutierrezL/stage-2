/*
 * Advanced Software Engineering coursework
 * @author Otonye Manuel
 */

/**
 * Exception Class for No Matching Order ID
 * Throws and exception when a wrong Order ID is entered
 */
public class NoMatchingOrderID extends Exception {
	public NoMatchingOrderID(String NoId){
		super("No ID Found = " + NoId);
	}
}
