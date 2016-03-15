import java.text.DecimalFormat;

/*
 * Advanced Software Engineering coursework
 * @author Linda Viksne
 */

/**
 *  A simple class to manage the details (name, price, category, whether is vegetarian)
 *  of the items on the menu.
 */
public class MenuItem {
	
	private String itemName;       
	private double itemPrice;
	private String category;
	private boolean isVegetarian;
	private int preparationTime;
	DecimalFormat df = new DecimalFormat("#.00"); //For formatting the price value
	
	/**
	 * 
	 * @param name            Name of the menu item
	 * @param price           Price of the menu item
	 * @param category        Category of the menu item
	 * @param is_vegetarian   Whether the menu item is vegetarian (true) or not (false)
	 */
	public MenuItem(String name, double price, String category, boolean is_vegetarian, int time){
		itemName = name;
		itemPrice = price;
		this.category = category;
		isVegetarian = is_vegetarian;
		preparationTime = time;
	}
	
	/**
	 * Returns the name of the item on the menu.
	 * @return a string with the name of the MenuItem.
	 */
	public String getName(){
		return itemName;
	}
	
	/**
	 * Returns the price of the item on the menu.
	 * @return a double with the price of the MenuItem.
	 */
	public double getPrice(){
		return itemPrice;
	}
	
	/**
	 * Returns the category of the item on the menu.
	 * @return a string with the category of the MenuItem.
	 */
	public String getCategory(){
		return category;
	}
	
	/**
	 * @return the preparationTime
	 */
	public int getPreparationTime() {
		return preparationTime;
	}

	/**
	 * @param preparationTime the preparationTime to set
	 */
	public void setPreparationTime(int preparationTime) {
		this.preparationTime = preparationTime;
	}

	/**
	 * Sets a new name for the item on the menu.
	 * @param new_name   the new name of the item on the menu
	 */
	public void setName(String new_name){
		itemName = new_name;
	}
	
	/**
	 * Sets a new price for the item on the menu.
	 * @param new_price   the price of the item on the menu
	 */
	public void setPrice(String new_price){
		itemName = new_price;
	}
	
	/**
	 * Sets a new category for the item on the menu.
	 * @param new_category   the category of the item on the menu
	 */
	public void setCategory(String new_category){
		category = new_category;
	}
	
	 /**
     * Test for content equality between two objects.
     * @param other The object to compare to the one in question.
     * @return true, if the argument object has the same itemName, false, if it does not.
     */
    @Override
	public boolean equals(Object other)
    {
        if(other instanceof MenuItem) {
            MenuItem otherMenuItem = (MenuItem) other;
            return itemName.equals(otherMenuItem.getName());
        }
        else {
            return false;
        }
    }

    /**
     * Compare this MenuItem object with another one for sorting reasons.
     * The objects are compared by their itemName attribute.
     * @param otherMenuItem The object that the MenuItem in question will be compared against.
     * @return a negative integer if the itemName comes before the parameter's itemName,
     *         zero, if they are equal, and a positive integer, if it comes after the other.
     */
    public int compareByNameTo(MenuItem otherMenuItem)
    {
        return itemName.compareTo(otherMenuItem.getName());
    }    

    /**
     * Compare this MenuItem object with another one for sorting reasons.
     * The objects are compared by their category attribute.
     * @param otherMenuItem The object that the MenuItem in question will be compared against.
     * @return a negative integer if the category comes before the parameter's category,
     *         zero, if they are equal, and a positive integer, if it comes after the other.
     *         If two are equal, they will be ordered alphabetically/
     */
    public int compareByCategoryTo(MenuItem otherMenuItem)
    {
        return itemName.compareTo(otherMenuItem.getCategory());
    }    
    
    /**
     * Returns a description of the menu item (name, if it is vegetarian, price).
     * @return a string containing the name, if it is vegetarian and price of the menu item.
     */
    public String printItemSummary()
    {
        String padded = String.format("%-20s", this.itemName);
    	return padded + " " + this.isVegetarianPrint() + " " + df.format(itemPrice);
    }
    /**
     * Checks, if the item on the menu is vegetarian or not, i.e. if MenuItem attribute isVegetarian
     * is true (in which case it is vegetarian) or false in which case it is not.
     * @return the string (V), if the item is vegetarian, and a blank string, if the item is not vegetarian.
     */
    public String isVegetarianPrint(){
    	String is_veg = "   ";
    	if (this.isVegetarian){
    		is_veg = "(V)";
    	}
    	return is_veg;
    }
}	
	
