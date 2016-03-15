import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Random;

/**
* <h1>Aggregator</h1>
* Aggregator class contains information of all the orders requested and all the items
* found in the menu. It also populates their collections, generating summaries and
* tables of frequency.
*
* @author Luis Alberto Gutierrez Iglesias
* @version 1.0
* @since 2016-02-16
*/
public class Aggregator {

	/**
	 * OrderTable variable containing information of all the orders
	 */
	private OrderTable orderTable;
	
	/**
	 * Instance variable which stores all the items in the menu
	 */
	private MenuItemMap menuItemMap;
	
	/**
	 * This collection maps pairs of integers corresponding to table numbers as keys
	 * and discounts provided by the waiter as values
	 */
	private HashMap<Integer,Integer> discounts;
	
	/**
	 * LinkedList containing all the orders, in the order they were read.
	 */
	private LinkedList <Order> orders;
	
	/**
	 * Class constructor
	 */
	public Aggregator() {
		orderTable = new OrderTable();
		menuItemMap = new MenuItemMap();
		discounts = new HashMap<Integer,Integer>();
		orders = new LinkedList<Order>();
	}

	/**
	 * Method used to generate a String of summaries and tables of orders and items in 
	 * the menu in an output file
	 * @return	a String with all the summaries and statistics from the class collections
	 */
	public String reporter(){
		String report = "";
		report += menuItemMap.listByCategory();
		report += "\nTABLE SUMMARY\n==============";
		report += printBills();
		report += this.frequencyReport();
		report += this.dishesNotOrdered();
		int highestBill = this.mostExpensiveTableBill();
		int lowestBill = this.cheapestTableBill();
		report += "\nBILLS PRICE STATISTICS (without discounts)\n==========================================\n";
		report += "Most expensive table: Table " + highestBill + ", whose bill is £" + String.format("%.2f", getTableTotal(highestBill)) + "\n";
		report += "Cheapest table: Table " + lowestBill + ", whose bill is £" + String.format("%.2f", getTableTotal(lowestBill)) + "\n";
		report += "The average bill paid is: £" + String.format("%.2f", this.averageBill()) + "\n";
		report += "\nVEGETARIAN DISHES STATISTICS\n============================\n";
		report += "Menu contains " + vegetarianDishes() + " veggie dishes out of " + menuItemMap.getNumberOfDishes() + "\n";
		report += String.format("%.1f", vegetarianOrdersPercentage()) + "% of orders are vegetarian dishes";
		return report;
	}
	
	/**
	 * This method writes supplied text to file. If the filename is not found,
	 * it will throw an FileNotFoundException. And if filename exists, but any
	 * I/O operation fails or is interrupted, it will throw IOException
	 * 
	 * @param filename  name of the file to be written to
	 * @param report  String containing text to be written to the file
	 */
	public void writer(String filename, String report) {
		 FileWriter fW;
		 try {
			 fW = new FileWriter(filename);
			 fW.write("-----------------------------------------------\n"
		    		+ "------ COMPLETE REPORT OF THE RESTAURANT ------\n"
		    		+ "-----------------------------------------------\n");
			 fW.write(report);
			 fW.close();
		 }
		 catch (FileNotFoundException fnf){
			 System.out.println("Sorry, but " + filename + " could not be found\n");
			 // If this error occurs, the application would end
			 System.exit(0);
		 }
		 catch (IOException ioe){
		    ioe.printStackTrace();
		    // If this error occurs, the application would end
		    System.exit(1);
		 }
	}
	
	/**
	 * This method populates the collections of orders and items using input text files
	 */
	public void populate() {
		// Menu population
		MenuScanner s = new MenuScanner();
		menuItemMap = s.getMenuEntries();
		try {
			File f = new File("OrderInput.txt");
			Scanner scanner = new Scanner(f);
			int line = 0;
			while (scanner.hasNextLine()) {
				line++;
				// Firstly, read the line and process it
				String inputLine = scanner.nextLine();
				// If it is a blank line, ignore it
				if (inputLine.length() != 0){
					try {
						String parts [] = inputLine.split(";");
						// Remove spaces and get an integer from those Strings
						int table = Integer.parseInt(parts[0].trim());
						int quantity = Integer.parseInt(parts[2].trim());
						String item = parts[1].trim();
						if(menuItemMap.containsItem(item)){
							Order o = new Order(table, item, quantity);
							if(orderTable.addOrder(o))	orders.add(o);
						}else{
							String error = "Error in line " + line + " - There is no item called " + item + " in the menu";
							System.out.println(error);
						}
					}					
					// This catches trying to convert a String to an integer
					catch (NumberFormatException nfe) {
						// It would not process that line, but print the error message
						String error = "Number conversion error was found in line " + line + " - " + nfe.getMessage();
						System.out.println(error);
					}
					// This catches missing items (if items < 3)
					catch (ArrayIndexOutOfBoundsException air) {
						// It would not process that line, but print the error message
						String error = "Not enough items in line " + line + " (index position : " + air.getMessage() + ")";
						System.out.println(error);
					}
					catch(InvalidPositiveInteger ipi){
						// It would not process that line, but print the error message
						System.out.println(ipi.getMessage());
					}
				}
			}
			scanner.close();
		}
		// This catches if the file is not found
		catch (FileNotFoundException fnf){
			System.out.println("File OrderInput.txt could not be found\n");
			System.exit(0);
		}
	}

	/**
	 * Method used to update the discounts collection. It will be used in the GUI 
	 * each time a waiter generates a bill for a specified table providing a discount 
	 * percentage.
	 * @param table	Table to be added to the collection
	 * @param discount	percentage of discount for that table
	 */
	protected void updateDiscounts(int table, int discount){
		discounts.put(table, discount);
	}
	
	/**
	 * Method used to update the discounts collection removing an entry where the table
	 * specified may be present as key
	 * @param table	Table to be added to the collection
	 */
	protected void deleteDiscount(int table){
		discounts.remove(table);
	}
	
	/**
	 * Returns a table's bill price without considering discounts
	 * @param table	Table number
	 * @return	a double containing the total price for a table specified
	 */
	public double getTableTotal(int table){
		double total = 0;
		HashSet<Order> set = orderTable.findByTable(table);
		if(set!=null){
			Iterator<Order> i = set.iterator();
			Order temp = null;
			while(i.hasNext()){
				temp = i.next();
				total += menuItemMap.findByName(temp.getItemName()).getPrice() * temp.getQuantity();
			}
		}
		return total;
	}
	
	public LinkedList<Order> getOrderList(){
		return orders;
	}
	
	/**
	 * @return the menuItemMap
	 */
	public MenuItemMap getMenuItemMap() {
		return menuItemMap;
	}

	/**
	 * Returns a table's bill price considering discounts
	 * @param table	Table number
	 * @return	a double containing the total price for a table specified
	 */
	public double getTableDiscountedTotal(int table){
		double total = getTableTotal(table);
		// If a discount is found, it will be applied to the total price
		if(discounts.containsKey(table)){
			double discount = total * (discounts.get(table)/100.0);
			total -= discount;
		// If not, an automatic discount will be applied. This automatic discount are two pounds for
		// each ten pounds spent.
		}else{
			int autoDiscount = (int) (total/10);
			autoDiscount *= 2;
			total -= autoDiscount;
		}
		return total;
	}
	
	/**
	 * Returns the table number with the most expensive bill. If there is no orders
	 * yet, returns 0 and shows an error message
	 * @return	an integer corresponding to the table number with the highest bill
	 */
	public int mostExpensiveTableBill(){
		int table = 0;
		if(!(orderTable.getOrderTable().isEmpty())){
			double max = 0;
			for(Integer t : orderTable.getOrderTable().keySet()){
				double price = getTableTotal(t);
				if (price > max) {
					max = price;
					table = t;
				}
			}
		}else
			System.out.println("There is no orders yet");
		return table;
	}
	
	/**
	 * Returns the table number with the cheapest bill. If there is no orders yet, 
	 * returns 0 and shows an error message
	 * @return	an integer corresponding to the table number with the lowest bill
	 */
	public int cheapestTableBill(){
		int table = 0;
		if(!(orderTable.getOrderTable().isEmpty())){
			double min = getTableTotal(orderTable.getOrderTable().firstKey());
			for(Integer t : orderTable.getOrderTable().keySet()){
				double price = getTableTotal(t);
				if (price <= min) {
					min = price;
					table = t;
				}
			}
		}else
			System.out.println("There is no orders yet");
		return table;
	}
	
	/**
	 * Returns the average price of all the bills, or 0 if there is no orders yet
	 * @return	an double with the average bill price
	 */
	public double averageBill(){
		double average = 0;
		int numberOfTables = orderTable.getOrderTable().size();
		if(!(orderTable.getOrderTable().isEmpty())){
			for(Integer t : orderTable.getOrderTable().keySet()){
				average += getTableTotal(t);
			}
		}else
			System.out.println("There is no orders yet");
		return Math.round((average/numberOfTables) * 100.0) / 100.0;
	}
	
	
	/**
	 * Returns a frequency report showing name of items ordered and how many times
	 * they have been requested
	 * @return	a String containing the frequency report for all the items ordered 
	 */
	public String frequencyReport(){
		String report = "\nFREQUENCY REPORT\n================\n";
		report += orderTable.orderedItems();
		return report;
	}
	
	/**
	 * Returns a report showing name of items not ordered
	 * @return	a String containing names of all the items not ordered in the menu
	 */
	public String dishesNotOrdered(){
		String report = "\nDISHES NOT ORDERED\n==================\n";
		if(menuItemMap.getNumberOfDishes()>0){
			Iterator<String> it = menuItemMap.getMenuItemMap().keySet().iterator();
			while(it.hasNext()){
				String temp = it.next();
				if(!(orderTable.getFrequency().containsKey(temp))){
					report += temp + "\n";
				}
			}
			if(report.equals("DISHES NOT ORDERED\n==================\n"))
				report += "All of dishes have been already ordered";
		}else
			report += "There is no dishes in the menu yet\n";
		return report;
	}
	
	/**
	 * Returns a bill for a specified table. It also checks if the specified table
	 * contains an entry in the discounts collection. If so, it will apply that
	 * discount in the bill. If not, the automatic discount (£2 for each £10 spent)
	 * will be reflected in the final price.
	 * @param table	Table number
	 * @return	a String containing the bill for the table, including its menu items
	 * 			and quantity, prices and automatic discounts
	 */
	public String getTableBill(int table){
		String bill = "Sorry, there is no table number " + table + "\n";
		if(orderTable.getOrderTable().containsKey(table)){
			bill = "\nTABLE " + table + "\n";
			HashSet<Order> set = orderTable.getOrderTable().get(table);
			Iterator<Order> i = set.iterator();
			while(i.hasNext()){
				Order o = i.next();
				bill += String.format("%-20s", o.getItemName().toUpperCase()) + String.format("%-2s", o.getQuantity()) + " * ";
				double price = menuItemMap.findByName(o.getItemName()).getPrice();
				bill += String.format("%5.2f", price) + " = " + String.format("%6.2f", price*o.getQuantity()) + "\n";
			}
			bill += String.format("%-33s", "") + "======\n";
			double total = getTableTotal(table);
			bill += String.format("%-33s", "Total for this table :") + String.format("%6.2f", total) + "\n";
			double discount = total - getTableDiscountedTotal(table);
			bill += String.format("%-33s", "Discount :") + String.format("%6.2f", discount) + "\n";
			bill += String.format("%-33s", "Discounted total :") +  String.format("%6.2f", getTableDiscountedTotal(table)) + "\n";
		}
		return bill;
	}
	
	/**
	 * Returns the bills for all the tables
	 * @return	a String containing the bills, including its menu items and quantity, prices and discounts
	 */
	public String printBills(){
		String report = "";
			for(Integer i : orderTable.getOrderTable().keySet()) {
				report += getTableBill(i);
			}
		return report;
	}
	
	/**
	 * Returns the number of vegetarian dishes ordered
	 * @return	an Integer with the number of vegetarian items ordered
	 */
	public int vegetarianDishes(){
		int amount = 0;
		Iterator<MenuItem> it = menuItemMap.getMenuItemMap().values().iterator();
		while(it.hasNext()){
			if(it.next().isVegetarianPrint().equals("(V)"))
				amount++;
		}
		return amount;
	}
	
	
	/**
	 * Returns the percentage of items ordered which are suitable for vegetarians
	 * @return a double with the percentage of veggie dishes ordered
	 */
	public double vegetarianOrdersPercentage(){
		int total = 0;
		int veggies = 0;
		Iterator<Entry<String, Integer>> it = orderTable.getFrequency().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,Integer> pair = (Entry<String, Integer>)it.next();
			total += pair.getValue();
			if(menuItemMap.findByName(pair.getKey()).isVegetarianPrint().equals("(V)"))
				veggies += pair.getValue();
		}
		if(total>0){
			double percentage = veggies * 100 / total;
			return percentage;
		}else
			return 0;
	}
	
	/**
	 * Returns the MenuItemMap with all the MenuItem-s.
	 * @return the MenuItemMap containing all the MenuItem-s.
	 */
	public MenuItemMap getMenuItemMap(){
		return menuItemMap;
	}
	
	
	/**
	 * Creates a random order.
	 * @return a random order.
	 * @throws InvalidPositiveInteger
	 */
	public Order getRandomOrder() throws InvalidPositiveInteger{
		Random r1 = new Random();
		int t = r1.nextInt(6) + 1;
		Random r2 = new Random();
		int q = r2.nextInt(4)+1;
		Order o = new Order (t, menuItemMap.getRandomItemName(),q);
		return o;
	}
}
