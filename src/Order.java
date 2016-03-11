import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
* <h1>Order</h1>
* Order class is the basic class managed by the application. It contains information
* about each item ordered by a certain table, including its ID number, table, name
* and quantity requested.
*
* @author Luis Alberto Gutierrez Iglesias
* @version 1.1
* @since 2016-02-14
*/
public class Order implements Comparable<Order> {

	// INSTANCE VARIABLES
	/** 
	 * Order identifier
	 */
	private String orderID;
	
	/**
	 * Table number
	 */
	private int tableID;
	
	/**
	 * Item identifier
	 */
	private String itemName;
	
	/**
	 * Quantity of items ordered
	 */
	private int quantity;
	
	// CLASS VARIABLES
	/**
	 * Last order identifier used
	 */
	private static String lastID = "O00000000";
	
	// CONSTRUCTORS
	/**
	 * Main constructor
	 * 
	 * @param t	 Table which makes the order
	 * @param i	 Name of the item ordered
	 * @param q	 Quantity of items
	 * @throws InvalidPositiveInteger
	 */
	public Order(int t, String i, int q) throws InvalidPositiveInteger {
		orderID = lastID;
		autoincrementID();
		if(t>0)	tableID = t;
		else	throw new InvalidPositiveInteger(t);
		itemName = i;
		if(q>0)	quantity = q;
		else	throw new InvalidPositiveInteger(q);
	}
	
	/*
	 * Private method used to increment the last order identifier assigned.
	 * It will be called in each order creation. 
	 */
	private void autoincrementID() {
		NumberFormat myFormat = new DecimalFormat("00000000");
		int i = Integer.parseInt(lastID.substring(1)) + 1;
		lastID = "O" + myFormat.format(i);
	}

	/**
	 * Returns the whole Order object
	 * @return the Order object
	 */
	public Order getOrder() {
		return this;
	}
	
	/**
	 * Returns a String containing the Order identifier
	 * @return the orderID
	 */
	public String getOrderID() {
		return orderID;
	}

	/**
	 * Returns the number of the table
	 * @return the tableID
	 */
	public int getTableID() {
		return tableID;
	}

	/**
	 * Sets a new number for the table in the order
	 * @param tableID	the new table number
	 */
	public void setTableID(int tableID) {
		this.tableID = tableID;
	}

	/**
	 * Returns the name of the item ordered
	 * @return a String with the item name
	 */
	public String getItemName() {
		return itemName;
	}

	/**
	 * Sets a new name for the item in the order
	 * @param itemName	a String with the new item name
	 */
	public void setItemName(String itemName) {
		this.itemName = itemName;
	}

	/**
	 * Returns the quantity of items ordered
	 * @return a integer with the quantity
	 */
	public int getQuantity() {
		return quantity;
	}

	/**
	 * Sets a new quantity of items in the order
	 * @param quantity	the new quantity
	 */
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	/**
	 * Returns a String containing information of the order
	 * @return a String consists of all the variables in the Order
	 */
	public String printInfo(){
		return this.getOrderID() + "\t" + String.format("%-5s", this.getTableID())
			+ "\t" + String.format("%-20s", this.getItemName()) + "\t"
			+ this.getQuantity();
	}
	
	/**
	 * Returns a short version of the String containing information of the order
	 * @return a String consists of all the variables in the Order
	 */
	public String printShortInfo(){
		return this.getOrderID() + "  " + String.format("%-1s", this.getTableID())
			+ "  " + String.format("%-20s", this.getItemName()) + "  "
			+ this.getQuantity();
	}
	
	/*
	 * Method overridden to make our Order class Comparable.
     * Compares this Order object with the specified one in the method call.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
    public int compareTo(Order comp) {
        int compareTable = comp.getTableID();
        return this.getTableID() - compareTable;
    }

	/* 
	 * Returns a hash code value for the Order object
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
		result = prime * result + tableID;
		return result;
	}

	/* 
	 * Indicates whether some other object is "equal to" this Order one
	 * Returns true if both orders contain the same table and item name
	 * Also returns true if both have the same order ID
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Order)) {
			return false;
		}
		Order other = (Order) obj;
		if (itemName == null) {
			if (other.itemName != null) {
				return false;
			}
		} else if (itemName.equals(other.getItemName()) && tableID == other.getTableID()) {
			return true;
		}
		if (orderID == null) {
			if (other.orderID != null) {
				return false;
			}
		} else if (!orderID.equals(other.orderID)) {
			return false;
		}
		return true;
	}
}
