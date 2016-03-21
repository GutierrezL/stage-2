import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Observable;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Map.Entry;
import java.util.ArrayList;

public class OrderGenerator extends Observable{

	//OrderTable variable containing information of all the orders
	private OrderTable orderTable;
	//Instance variable which stores all the items in the menu
	private MenuItemMap menuItemMap;
	//LinkedList containing all the orders...
	//...in the kitchen,...
	private LinkedList<Order> ordersInKitchen;
	//...in the hatch and...
	private LinkedList<Order> hatch;
	//...at each table
	private ArrayList<LinkedList<Order>> tables;
	//Log instance using Singleton pattern
	Log log;
	//Order collection population method
	private String populateMethod;
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean finished;
	//Set to true when the hatch doesn't contain orders
	private boolean hatchFinished;
	//Number of tables in the restaurant
	private int numberOfTables;
	// This collection maps pairs of integers corresponding to table numbers as keys
	// and discounts provided by the waiter as values
	private HashMap<Integer,Integer> discounts;
	//Set to true when the kitchen opens and orders start to be made
	private boolean startSimulation;
	//The duration of the simulation in seconds
	private int kitchOpenTime;
	//Shows, if the simulation is active, i.e. it becomes true when the last order reaches its table
	private boolean simulationFinished;

	//Kitchen and hatch panel headers in the GUI
	private static final String orderTitles = String.format("%-9s", "ID")+
			String.format("%-7s", "TABLE")+ String.format("%-22s", "ITEM NAME") + 
			String.format("%-5s", "QUANT") +"\r\n";

	public OrderGenerator() {
		orderTable = new OrderTable();
		menuItemMap = new MenuItemMap();
		MenuScanner s = new MenuScanner();
		menuItemMap = s.getMenuEntries();
		discounts = new HashMap<Integer,Integer>();
		ordersInKitchen = new LinkedList<Order>();
		populateMethod = "";
		log = Log.getInstance();
		numberOfTables = 6;
		finished = false;
		hatchFinished = false;
		startSimulation = false;
		hatch = new LinkedList <Order>();
		simulationFinished = false;
		tables = new ArrayList<LinkedList<Order>>();
		for (int i = 1; i <=6; i++) {
			tables.add(new LinkedList<Order>());
		}
	}

	/**
	 * Returns, if the kitchen is still open, i.e. still taking new orders.
	 * @return true, if the closed, and false, if the kitchen is still open.
	 */
	public boolean isFinished() {
		return finished;
	}

	public boolean hatchIsFinished() {
		return hatchFinished;
	}

	public boolean isSimulationActive() {
		return startSimulation;
	}

	/**
	 * Sets the simulation as finished; becomes true when last order reaches its table.
	 * Notifies the observers that the simulation has ended.
	 */
	public void setSimFinished(){
		simulationFinished = true;

		//Notifies observers.
		setChanged();
		notifyObservers();
		clearChanged();
	}

	/**
	 * Returns the status of the simulation, i.e. false, if the start button has been 
	 * pressed and the simulation is running. It becomes true when the last order reaches its table.
	 * @return boolean with simulation status, false, if running, true, if finished.
	 */
	public boolean getSimFinished(){
		return simulationFinished;
	}

	public void setStartSimulation() {
		this.startSimulation = true;
	}

	/**
	 * Indicates the end of the working hours of the kitchen, i.e. no more orders accepted.	
	 */
	public void setFinished() {
		finished = true;
	}

	/**
	 * Returns all the orders in the kitchen in the order they arrived to the kitchen.
	 * @return a LinkedList of all the orders in the kitchen.
	 */
	public LinkedList <Order> getOrdersInKitchen(){
		return ordersInKitchen;
	}

	/**
	 * Sets the String describing how the orders should be generated.
	 * @param value String value describing how the orders should be generated.
	 */
	public void setPopulateMethod(String value){
		populateMethod = value;
	}

	/**
	 * Sets the duration of simulation in seconds.
	 * @param value String value describing how the orders should be generated.
	 */
	public void setKitchOpenTime(String value){
		int time = Integer.parseInt(value.trim());
		kitchOpenTime = time;
	}

	/**
	 * Returns the duration in seconds for which the kitchen is open in the simulation.
	 * @return integer containing duration of kitchen opening time in seconds.
	 */
	public int getKitchOpenTime(){
		return kitchOpenTime;
	}

	/**
	 * This method populates the collections of orders and items using input text files
	 */
	public void populateWithFile(int line) throws IOException{
		//Checks, if kitchen is still open
		if (!this.isFinished()){
			try{
				InputStream is = getClass().getResourceAsStream("resources/OrderInput.txt");
		        //Gets line corresponding to argument value
				String lineValue = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.toList()).get(line).trim();
				//Checks, if line exists and is not empty
				if(lineValue!= null && !lineValue.isEmpty()){
					String parts [] = lineValue.split(";");
					// Remove spaces and get an integer from those Strings
					int table = Integer.parseInt(parts[0].trim());
					int quantity = Integer.parseInt(parts[2].trim());
					String item = parts[1].trim();
					//The restaurant is assumed to have 6 tables and quantity must be at least 1
					if(menuItemMap.containsItem(item) && ((numberOfTables+1)>table && table>0) && (quantity>0)){
						Order o;
						try {
							o = new Order(table, item, quantity);
							//Checks, if order has been successfully added to orderTable
							if(orderTable.addOrder(o))	{this.receiveOrder(o);
							} else{
								String error = "Error in line " + line + " - There is no item called " 
										+ item + " in the menu";
								System.out.println(error);
							}
						} catch (InvalidPositiveInteger e) {
							e.printStackTrace();
						}						
					}
				}
			}catch (NullPointerException npe){
				System.out.println("File OrderInput.txt could not be found\n");
				System.exit(0);
			} 
		}
	}

	/**
	 * Populates the collections of orders and items by generating random orders.
	 * @throws InvalidPositiveInteger 
	 */
	public synchronized void populateWithGenerator() throws InvalidPositiveInteger {
		Order o = this.generateRandomOrder();
		if(orderTable.addOrder(o))	{
			this.receiveOrder(o);
		}
		setChanged();
		notifyObservers();
		clearChanged();
	}

	public void setHatchFinished() {
		hatchFinished = true;
	}

	public ArrayList<LinkedList<Order>> getListOfTables() {
		return tables;
	}

	public LinkedList<Order> getHatch() {
		return hatch;
	}
	
	/**
	 * This method returns a summary for all the orders in the given table
	 * @param i	Table number
	 * @return	a String containing data about the orders in the table
	 */
	public String getOrderList(int i) {
		String report = "TABLE " + (i+1) + "\n";
		if (tables.get(i).size() == 0) {
			report += "There are no orders to show";
		}else {
			int num = 1;
			for (Order o : tables.get(i)) {
				report  += num++ + " " + o.getItemName() + " * " + o.getQuantity() + "\n";
			}
		}
		return report;		
	}

	/**
	 * @return the menuItemMap
	 */
	public MenuItemMap getMenuItemMap() {
		return menuItemMap;
	}

	/**
	 * Starts all the threads.
	 */
	public void start() {
		Thread kitchOrderThread = new Thread();
		kitchOrderThread.start();

		toKitchen firstStep = new toKitchen(this);
		Thread sendToKitchen = new Thread(firstStep);
		sendToKitchen.start();	

		toHatch secondStep = new toHatch(this);
		Thread sendToHatch = new Thread(secondStep);
		sendToHatch.start();

		toTables thirdStep = new toTables(this);
		Thread sendToTables = new Thread(thirdStep);
		sendToTables.start();
	}

	/**
	 * Returns the current version of the report, i.e. a list of orders 
	 * in the kitchen.
	 * @return a String containing the current version of the report.
	 */
	public String getKitchenReport(){		
		String report = "LIST OF ORDERS IN THE KITCHEN \r\n" + orderTitles;
		for (Order ord: ordersInKitchen) {
			report += ord.printShortInfo() + "\r\n";
		}
		return report;	
	}

	/**
	 * Returns the current version of the report, i.e. a list of orders 
	 * in the hatch.
	 * @return a String containing the current version of the report.
	 */
	public String getHatchReport(){		
		String report = "LIST OF ORDERS IN THE HATCH \r\n" + orderTitles;
		for (Order ord: hatch) {
			report += ord.printShortInfo() + "\r\n";
		}
		return report;	
	}

	/**
	 * Gets the next order.
	 * Once this method is started, it should be allowed to finish.
	 * @param o the order to be processed
	 */
	public synchronized void receiveOrder(Order o) {
		ordersInKitchen.add(o);
		log.addEntry("Order " + o.getOrderID()+ " ('" + o.getItemName() + "', x" + o.getQuantity()
		+ ", table " + o.getTableID() + ") has been sent to the kitchen.\r\n" );

		setChanged();
		notifyObservers();
		clearChanged();

		try {
			//Slows thread
			Thread.sleep(500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a random order.
	 * @return a random order.
	 * @throws InvalidPositiveInteger
	 */
	public Order generateRandomOrder() throws InvalidPositiveInteger{
		//Generates a random table number from 1 to 6.
		Random r1 = new Random();
		int t = r1.nextInt(6) + 1;
		//Generates a random quantity from 1 to 10.
		Random r2 = new Random();
		int q = r2.nextInt(10)+1;
		Order o = new Order (t, menuItemMap.getRandomItemName(),q);
		return o;
	}

	/**
	 * Returns the method, how the order collections will be populated.	
	 * @return String with order collection population method.
	 */
	public String getPopulateMethod(){
		return populateMethod;
	}

	public Order getFirstOrder(){
		return this.ordersInKitchen.getFirst();
	}

	/**
	 * Method which sends the first order in the kitchen to the hatch. Then, it
	 * notifies the observers and adds an entry in the Log file.
	 */
	public synchronized void orderToHatch() {
		Order firstOrder = this.getFirstOrder();
		this.hatch.add(firstOrder);
		log.addEntry("Order " + firstOrder.getOrderID()+ " ('" + firstOrder.getItemName() + "', x" + firstOrder.getQuantity()
		+ ", table " + firstOrder.getTableID() + ") has been sent to the hatch.\r\n" );
		ordersInKitchen.removeFirst();
		setChanged();
		notifyObservers();
		clearChanged();
	}

	/**
	 * Checks, if there are any orders in the kitchen.
	 * @return true, if ordersInKitchen collection is empty, and false, if otherwise
	 */
	public boolean noOrdersInKitchen(){
		if (ordersInKitchen.isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Method which sends the first order in the hatch to the corresponding table. Then, it
	 * notifies the observers and adds an entry in the Log file.
	 */
	public synchronized void orderToTable() {
		if(!this.hatch.isEmpty()){
			Order firstOrder = this.hatch.getFirst();
			tables.get(firstOrder.getTableID()-1).add(firstOrder);
			log.addEntry("Order " + firstOrder.getOrderID()+ " ('" + firstOrder.getItemName() + "', x" + firstOrder.getQuantity()
			+ ", table " + firstOrder.getTableID() + ") has been sent to the table.\r\n" );
			this.hatch.removeFirst();
			if(ordersInKitchen.isEmpty() && hatch.isEmpty())	this.setHatchFinished();
			setChanged();
			notifyObservers();
			clearChanged();
		}
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
		String bill = "Sorry, there is no orders for table " + table + " yet.\n";
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
	 * If a discount is provided, it creates a new entry for the specified table in the discount collection If not, 
	 * removes a possible entry for that table. This will allow the automatic discounts work
	 * @param numberText	Table number whose bill will be generated
	 * @param discountText	Percentage of discount applied to that table
	 * @return
	 */
	public String generateBill(String numberText, String discountText){
		int number = Integer.parseInt(numberText);
		if(!discountText.equals("")){
			int discount = Integer.parseInt(discountText);
			this.updateDiscounts(number, discount);
		}else{
			this.deleteDiscount(number);
		}
		return this.getTableBill(number);
	}
}
