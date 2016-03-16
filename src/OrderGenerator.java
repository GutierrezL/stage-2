import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;
import java.util.ArrayList;


public class OrderGenerator extends Observable{
	
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean finished;
	//OrderTable variable containing information of all the orders
	private OrderTable orderTable;
	//Instance variable which stores all the items in the menu
	private MenuItemMap menuItemMap;
	//LinkedList containing all the orders, in the order they were read.
	private LinkedList<Order> ordersInKitchen;
	//list of tables
	private String report;
	//Log instance using Singleton pattern
	Log log;
	//Order collection population method
	private String populateMethod;
	private ArrayList<LinkedList<Order>> tables;
	//List of orders
	private LinkedList<Order> hatch;
	private int numberOfTables;
	
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean startSimulation;
	private boolean hatchFinished;
	//The duration of the simulation in seconds
	private int kitchOpenTime;
	
	//Kitchen and hatch panel headers in the GUI
	private static final String orderTitles = String.format("%-9s", "ID")+
			String.format("%-7s", "TABLE")+ String.format("%-22s", "ITEM NAME") + 
			String.format("%-5s", "QUANT") +"\r\n";

	/**
	 * This collection maps pairs of integers corresponding to table numbers as keys
	 * and discounts provided by the waiter as values
	 */
	private HashMap<Integer,Integer> discounts;
	
	public OrderGenerator() {
		
		orderTable = new OrderTable();
		menuItemMap = new MenuItemMap();
		MenuScanner s = new MenuScanner();
		menuItemMap = s.getMenuEntries();
		discounts = new HashMap<Integer,Integer>();
		ordersInKitchen = new LinkedList<Order>();
		report = "";
		populateMethod = "";
		log = Log.getInstance();
		numberOfTables = 6;
		
		finished = false;
		hatchFinished = false;
		startSimulation = false;
		hatch = new LinkedList <Order>();
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

		public void setStartSimulation() {
			this.startSimulation = true;
		}
	
	/**
	 * Indicates the end of the working hours of the kitchen, i.e. no more orders accepted.	
	 */
 	//indicates end of auction
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
			//Gets line corresponding to argument value
			String lineValue = Files.readAllLines(Paths.get("OrderInput.txt")).get(line).trim();
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
		}catch (FileNotFoundException fnf){
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
	
	//returns customer list
		public ArrayList<LinkedList<Order>> getListOfTables() {
			return tables;
		}
	
		public LinkedList<Order> getHatch() {
			return hatch;
		}
		
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
		report = this.getKitchenReport();
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
	
	public synchronized void orderToHatch() {
		Order firstOrder = this.getFirstOrder();
		this.hatch.add(firstOrder);
		log.addEntry("Order " + firstOrder.getOrderID()+ " ('" + firstOrder.getItemName() + "', x" + firstOrder.getQuantity()
		+ ", table " + firstOrder.getTableID() + ") has been sent to the hatch.\r\n" );
		ordersInKitchen.removeFirst();
		if(ordersInKitchen.isEmpty())	this.setFinished();
		
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
}
