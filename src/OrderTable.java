import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

/**
* <h1>OrderTable</h1>
* OrderTable class is a collection of all the orders managed by the application. It
* contains two different types of collections: a TreeMap storing numbers of tables
* as keys, having their corresponding orders as values, and a HashMap where the
* number of times each item of menu has been ordered will be registered.
*
* @author Luis Alberto Gutierrez Iglesias
* @version 1.1
* @since 2016-02-14
*/
public class OrderTable {
	
	/**
	 * Collection of all the orders managed by the application. It contains numbers
	 * of tables as values and their corresponding set of Orders as values in a
	 * HashSet, thus avoiding repeated values.
	 */
	private TreeMap<Integer,HashSet<Order>> orderTable;
	
	/**
	 * Collection mapping items names with the number of times each one has been
	 * ordered.
	 */
	private HashMap<String,Integer> frequency;
	
	/**
	 * Class constructor
	 */
	public OrderTable() {
		orderTable = new TreeMap<Integer,HashSet<Order>>();
		frequency = new HashMap<String, Integer>();
	}
	

	/**
	 * Returns the whole TreeMap containing all the orders
	 * @return 	the TreeMap with table numbers as keys and HashSet of orders as
	 * 			values
	 */
	public TreeMap<Integer, HashSet<Order>> getOrderTable() {
		return orderTable;
	}

	/**
	 * Returns a collection reflecting how often each item has been ordered
	 * @return 	a HashMap with item name as key and number of times it has been
	 * 			requested
	 */
	public HashMap<String, Integer> getFrequency() {
		return frequency;
	}

	
	/**
	 * Returns a Order object with the identifier specified
	 * @param id	The Order identifier
	 * @return	the Order object if identifier number has been found
	 * @throws NoMatchingOrderID if the specified ID is not found
	 */
	public Order findByID(String id) throws NoMatchingOrderID{
		if(validateOrderID(id)){
			Collection<HashSet<Order>> c = orderTable.values();
			Iterator<HashSet<Order>> i = c.iterator();
			boolean success = false;
			while(i.hasNext() && !success) {
				Iterator<Order> j = i.next().iterator();
				while(j.hasNext() && !success){
					Order o = j.next();
					if(o.getOrderID().equals(id))
						return o; 
				}
			}
			if(!success)
				throw new NoMatchingOrderID(id);
		}
		return null;
	}

	/**
	 * Returns the collection of orders requested by the table specified
	 * @param table	The table number
	 * @return	a HashSet of orders if table number exists. If not, returns 
	 * 			null and shows an error message
	 */
	public HashSet<Order> findByTable(int table){
		if(orderTable.containsKey(table))
			return orderTable.get(table);
		else{
			System.out.println("Table number " + table + " cannot be found");
			return null;
		}
	}
	
	/**
	 * Returns a collection of orders which contain the item specified.
	 * @param item	Name of the item
	 * @return	A ArrayList of orders where the item is found. If the item is
	 * 			not found in any order, return null and an error message
	 */
	public ArrayList<Order> findByMenuItem(String item){
		ArrayList<Order> orders = new ArrayList<Order>();
		Collection<HashSet<Order>> c = orderTable.values();
		Iterator<HashSet<Order>> i = c.iterator();
		boolean success = false;
		while(i.hasNext()) {
			Iterator<Order> j = i.next().iterator();
			while(j.hasNext()){
				Order o = j.next();
				if(o.getItemName().equals(item)){
					success = true;
					orders.add(o); 
				}
			}
		}
		if(success)
			return orders;
		else{
			System.out.println("There is no orders containing '" + item + "'");
			return null;
		}
	}
	
	/**
	 * Adds a new order to the collection. If there is already an order equals to the
	 * specified one, the order is not added and an error message is shown. Also creates
	 * an entry for that item in the frequencies collection, or updates its existing one.
	 * @param o	The new order to be added
	 */
	public boolean addOrder(Order o){
		Integer i = new Integer(o.getTableID());
		String itemName = o.getItemName();
		HashSet<Order> set = orderTable.get(i);
		if(set==null) set = new HashSet<Order>();
		if(set.add(o)){
			orderTable.put(i, set);
			Integer value = frequency.putIfAbsent(itemName, o.getQuantity());
			if(value!=null)
				frequency.replace(itemName, value+o.getQuantity());
			return true;
		}else{
			// It would not process that line, but print the error message
			String error = "Duplicate value: could not add '" + itemName + "' in table " + i;
			System.out.println(error);
			return false;
		}
	}
	
	/**
	 * Removes an order from the collection using a specified id. If the id does not
	 * exist or is not valid, shows an error message. It also updates the frequencies
	 * collection for that item.
	 * @param id	Order identifier
	 * @throws NoMatchingOrderID 
	 */
	public void removeOrder(String id) throws NoMatchingOrderID{
		if(validateOrderID(id)){
			Collection<HashSet<Order>> c = orderTable.values();
			Iterator<HashSet<Order>> i = c.iterator();
			boolean success = false;
			while(i.hasNext() && !success) {
				HashSet<Order> set = i.next();
				Iterator<Order> j = set.iterator();
				while(j.hasNext() && !success){
					Order o = j.next();
					if(o.getOrderID().equals(id)){
						success = set.remove(o);
						Integer value = frequency.get(o.getItemName());
						value -= o.getQuantity();
						if(value!=0)
							frequency.replace(o.getItemName(), value);
						else
							frequency.remove(o.getItemName());
					} 
				}
			}
			if(!success)
				throw new NoMatchingOrderID(id);
		}else{
			// It would not delete that Order, but print the error message
			String error = "Could not delete the order. '" + id + "' is not a valid order ID";
			System.out.println(error);
		}
	}
	
	/**
	 * Returns the total number of orders
	 * @return	the number of objects in the TreeMap collection
	 */
	public int getNumberOfOrders(){
		int total = 0;
		Collection<HashSet<Order>> c = orderTable.values();
		Iterator<HashSet<Order>> i = c.iterator();
		while(i.hasNext()) {
			total += i.next().size();
		}
		return total;
	}
	
	/**
	 * Prints a list of all the orders, including their identifiers, item name,
	 * quantities and table number.
	 * @return	a String containing all the information of each order in the collection
	 */
	public String listAllOrders(){
		String summary = "";
		if(!orderTable.isEmpty()){
			Collection<HashSet<Order>> set = orderTable.values();
			Iterator<HashSet<Order>> i = set.iterator();
			while(i.hasNext()){
				Iterator<Order> io = i.next().iterator();
				while(io.hasNext())
					summary += io.next().printInfo() + "\n";
			}
		}else
			summary += "There is no orders to show";
		return summary;
	}
	
	/**
	 * Returns a list of all the items ordered
	 * @return	a String containing the names of all the items requested
	 */
	public String orderedItems(){
		String list = "";
		Iterator<Entry<String, Integer>> it = frequency.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,Integer> pair = (Entry<String, Integer>)it.next();
			list += String.format("%-22s", pair.getKey()) + pair.getValue() + "\n";
		}
		if(list.equals(""))
			list += "There is no items ordered yet";
		return list;
	}
	
	/*
	 * Private method to check if an order identifier is valid or not
	 * Returns true if the first character is 'O' followed by 8 numbers
	 */
	private boolean validateOrderID(String id){
		if(id.length()!=9)
			return false;
		if(id.charAt(0)!='O')
			return false;
		if(!id.substring(1).matches("\\d+"))
			return false;
		return true;
	}
}
