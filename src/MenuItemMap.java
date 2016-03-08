import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/*
 * Advanced Software Engineering coursework
 * @author Linda Viksne
 */

/**
 * A class for storing the items on the menu (MenuItem-s).
 */
public class MenuItemMap {
	
	private TreeMap<String, MenuItem> menuItemMap;

	public MenuItemMap(){
		menuItemMap = new TreeMap<String, MenuItem>();    
	}
	/**
	 * Returns the value (the MenuItem/null) corresponding to the provided key.
	 * @param item_name the String key provided.
	 * @return the value (MenuItem, if present in MenuItemMap, or null, if not) 
	 * corresponding to the key provided.
	 */
	public MenuItem findByName(String item_name){
		MenuItem item = menuItemMap.get(item_name);
		return item;
	}
	
	/**
	 * Returns the MenuItemMap containing all the MenuItem-s as values and their names
	 * as keys.
	 * @return the TreeMap <String, MenuItem> containing all the MenuItem-s.
	 */
	public TreeMap<String, MenuItem> getMenuItemMap(){
		return menuItemMap;
	}
	
	/**
	 * Adds a new MenuItem to the MenuItemMap.
	 * @param new_item the new MenuItem to be added.
	 * @throws DuplicateMenuItem 
	 */
	public void addItem(MenuItem new_item) throws DuplicateMenuItem {
		String name = new_item.getName();
		
		if (findByName(name)!=null){
			
			throw new DuplicateMenuItem(name);
			//String error = "Could not add '" + name + "'. Duplicate value.";
			//System.out.println(error);
		} 
		else {
			menuItemMap.put(name, new_item);
		}
	}
	

	/**
	 * Removes the MenuItem, identified by its name, from the MenuItemMap.
	 * @param item_name the name of the MenuItem.
	 */
	public void removeItem(String item_name){
		menuItemMap.remove(item_name);
	}
	
	/**
	 * Returns the total number of different MenuItem-s.
	 * @return an integer representing the total number of MenuItem-s in the MenuItemMap.
	 */
	public int getNumberOfDishes(){
		int num_of_dishes = menuItemMap.size();
		return num_of_dishes;
	}
	
	/**
	 * Returns a list of the MenuItem-s in alphabetical order.
	 * @return a string of the MenuItem-s in alphabetical order.
	 */
	public String listByName(){
		String menu = "";
		  // Get a set of the entries
	      Set set = menuItemMap.entrySet();
	      // Get an iterator
	      Iterator i = set.iterator();
	      // Display elements
	      while(i.hasNext()) {
	         Map.Entry me = (Map.Entry)i.next();
	         MenuItem m = (MenuItem) me.getValue();
	         menu += "\r\n" + m.printItemSummary();
	      }
		return menu;
	}
	
	
	/**
	 * Returns a string showing all the MenuItem-s in the MenuItemMap grouped by category 
	 * in alphabetical order for each category.
	 * @return a String with all the MenuItem-s grouped by category.
	 */
	public String listByCategory(){
		String menu = "\r\nMENU \r\n==== \r\n";
		Set set = menuItemMap.entrySet();
	    // Get an iterator
	    Iterator iterator = set.iterator();
	    // Create a string for each menu item category
	    String starters = "STARERS\r\n";
	    String mains = "MAINS\r\n";
	    String sides = "SIDES\r\n";
	    String desserts = "DESSERTS\r\n";
	    String drinks = "DRINKS\r\n";   
	    /*
	     * to avoid having to iterate through the TreeMap several times, the category
	     * of each item (iterated by name in alphabetical order) is determined
	     * and from this, the details of the item are added to the corresponding
	     * category's string. All the strings are joined together at the end to 
	     * form the menu.
	     */
	    while(iterator.hasNext()) {
		       Map.Entry me = (Map.Entry)iterator.next();
		       MenuItem m = (MenuItem) me.getValue();
		       String categ = m.getCategory().toLowerCase();
		       if (categ.equals("starter")){
		    	   starters += "    " + m.printItemSummary() + "\r\n";
		       } else if (categ.equals("main")){
		    	   mains += "    " + m.printItemSummary() + "\r\n";
		       } else if (categ.equals("side")){
		    	   sides += "    " + m.printItemSummary() + "\r\n";
		       } else if (categ.equals("dessert")){
				     desserts += "    " + m.printItemSummary() + "\r\n";
		       } else if (categ.equals("drink")){
				     drinks += "    " + m.printItemSummary() + "\r\n";
		       } else {
		    	   System.out.println("Ooops, somethign went wrong.");}
		       }
	    //the strings of all menu item categories being joined together
	    menu += starters + mains + sides + desserts + drinks;
		return menu;
	}
	
	/**
	 * Checks, if the key set of the MenuItemMap contains the 
	 * string given as an argument.
	 * @param item_name String containing the name of the item.
	 * @return true, if the key set contains the string in the argument
	 * and false, if it does not.
	 */
	public boolean containsItem(String item_name){
	       return menuItemMap.containsKey(item_name);
	}
}

