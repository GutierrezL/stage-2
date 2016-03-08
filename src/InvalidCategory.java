/*
 * Advanced Software Engineering coursework
 * @author Linda Viksne
 */

/**
 * A class for a custom exception: When there is an attempt to
 * create a MenuItem that does not belong to a valid category.
 */
public class InvalidCategory extends Exception{

	public InvalidCategory(String category){
		super("'" + category + "' is not a valid menu item category.");
	}
}