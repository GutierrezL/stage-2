import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Random;
import java.util.Scanner;
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
	Log log = Log.getInstance();
	private String populateMethod;
	
	private ArrayList<LinkedList<Order>> tables;
	//List of orders
	private LinkedList<Order> hatch;
	
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean startSimulation;
	private boolean hatchFinished;
	
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
	 * This method populates the collections of orders and items using input text files
	 */
	public void populateWithFile() {
		try {
			File f = new File("OrderInput.txt");
			Scanner scanner = new Scanner(f);
			int line = 0;
			//Checks, if kitchen has not closed and if there is a next line.
			while (!this.isFinished() && scanner.hasNextLine()) {
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
						//The restaurant has 6 table and 
						if(menuItemMap.containsItem(item) && (7>table && table>0) && (quantity>0)){
							Order o = new Order(table, item, quantity);
							//Checks, if order has been successfully added to orderTable
							if(orderTable.addOrder(o))	{this.receiveOrder(o);}
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


		
		public String getPopulateMethod(){
			return populateMethod;
		}
	
	public Order getFirstOrder(){
		return this.ordersInKitchen.getFirst();
	}
	
	public synchronized void orderToHatch() {
		Order firstOrder = this.getFirstOrder();
		this.hatch.add(firstOrder);
		ordersInKitchen.removeFirst();
		if(ordersInKitchen.isEmpty())	this.setFinished();
		setChanged();
		notifyObservers();
    	clearChanged();
	}
	
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
			this.hatch.removeFirst();
			if(ordersInKitchen.isEmpty() && hatch.isEmpty())	this.setHatchFinished();
			setChanged();
			notifyObservers();
	    	clearChanged();
		}
	}

}
