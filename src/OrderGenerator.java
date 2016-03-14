import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.Scanner;


public class OrderGenerator extends Observable implements Subject, Runnable {
	
	private List<Observer> registeredObservers = new LinkedList<Observer>();
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean finished = false;
	private MVCRestaurantView view;

	//OrderTable variable containing information of all the orders
	private OrderTable orderTable;
	//Instance variable which stores all the items in the menu
	private MenuItemMap menuItemMap;
	//LinkedList containing all the orders, in the order they were read.
	private LinkedList<Order> ordersInKitchen;
	//list of tables
	private TableList tables = new TableList();
	private String report;
	Log log = Log.getInstance();
	private String populateMethod;
	

	/**
	 * This collection maps pairs of integers corresponding to table numbers as keys
	 * and discounts provided by the waiter as values
	 */
	private HashMap<Integer,Integer> discounts;
	
	
	public OrderGenerator() {
		orderTable = new OrderTable();
		menuItemMap = new MenuItemMap();
		discounts = new HashMap<Integer,Integer>();
		ordersInKitchen = new LinkedList<Order>();
		report = "";
		populateMethod = "";
	}
	
	/**
	 * Returns, if the kitchen is still open, i.e. still taking new orders.
	 * @return true, if the closed, and false, if the kitchen is still open.
	 */
		public boolean isFinished() {
			return finished;
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
	public void populateWithGenerator() throws InvalidPositiveInteger {
		while (!this.isFinished()){
			Order o = this.generateRandomOrder();
			if(orderTable.addOrder(o))	{
				this.receiveOrder(o);
			}
		}
	}
	
	/**
	 * Reads the items on the menu from an input text file and adds them to
	 * a collection (menuItemMap).
	 */
	public void populateMenuItems(){
		// Menu population
		MenuScanner s = new MenuScanner();
		menuItemMap = s.getMenuEntries();
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
		report = this.getOrderReport();
		
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

	
	
		/**
		 * Returns the current version of the report, i.e. a list of orders 
		 * in the kitchen.
		 * @return a String containing the current version of the report.
		 */
		public String getOrderReport(){		
			String report = "LIST OF ORDERS \r\n" + String.format("%-10s", "ID")+
					String.format("%-6s", "TABLE")+ String.format("%-20s", "ITEM NAME")
					+ "QUANT \r\n";
			
			for (Order ord: ordersInKitchen) {
				report += ord.printShortInfo() + "\r\n";
			}
			return report;	
		}
		
		/**
		 * returns customer list
		 * @return
		 */
		public TableList getListOfTables() {
			return tables;
		}
		
		/**
		 * Returns the report containing a list of orders in the kitchen.
		 * @return String report of orders in the kitchen.
		 */
		public String getReport(){
			return report;
		}

		///////////////////////////////////////////////////////////////////////////////////////////
		/**
		 * Required method for Runnable implementation.
		 */
		@Override
		public void run() {
			//Reads the menu input file.
			this.populateMenuItems();

			System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%%%" + populateMethod + "%%%%%%%%%%%%%%%%%%%%%%");
			if (populateMethod.equals("from a textfile")){
				//Reads the order input file.
				this.populateWithFile();
			} else {
				try {
					this.populateWithGenerator();
				} catch (InvalidPositiveInteger e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		
		///////////////////////////////////////////////////////////////////////////////////////////
		//Required methods for the Observer pattern
		/**
		 * Registers a new Observer.
		 */
		@Override
		public void registerObserver(Observer obs) {
			registeredObservers.add( obs);	
		}

		/**
		 * Removes an existing Observer.
		 */
		@Override
		public void removeObserver(Observer obs) {
			registeredObservers.remove( obs);
		}
		
		/**
		 * Notifies the Observers of a change.
		 */
		public void notifyObservers(){ 
			for( Observer obs : registeredObservers) obs.update(null, obs); 
		}
		
}
