/*
 * Advanced Software Engineering coursework
 * @author Linda Viksne
 */

/**
 * A class for a custom exception: When there is an attempt to
 * add a MenuItem that is already in the MenuItemMap.
 */
public class DuplicateMenuItem extends Exception {
	
	public DuplicateMenuItem(String duplicate_item){
		super("Duplicate menu item: " + duplicate_item);
	}
}
