import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
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
							if(orderTable.addOrder(o))	{
								this.receiveOrder(o);
								}
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
			this.populate();
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
