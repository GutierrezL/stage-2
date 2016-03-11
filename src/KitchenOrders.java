import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class KitchenOrders extends Observable implements  Runnable, Subject {
	
	
	//List of orders
	private LinkedList<Order> orders;
	private LinkedList<Order> ordersInKitchen;
	
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean finished = false;
	private MVCRestaurantView view;
	
	private List<Observer> registeredObservers = new LinkedList<Observer>();
	
	String report = "LIST OF ORDERS \r\n" + String.format("%-9s", "ID")+
			String.format("%-5s", "ITEM")+ String.format("%-22s", "QUANTITY")
			+ "TABLE \r\n";
	
	
	public KitchenOrders (LinkedList <Order> order_list){
		orders = order_list;
		ordersInKitchen = new LinkedList <Order>();
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
	
	@Override
	/**
	 * The thread run method.
	 */
	public void run() {
		
		Thread kitchOrderThread = new Thread();
		kitchOrderThread.start();
		
		for (Order o: orders){			
			receiveOrder(o);
		} try {
    		Thread.sleep(10000);
    	} catch (Exception e) {
			System.out.println("KitchenOrder thread exception" + e.getStackTrace());
		}
    	System.out.println("The kitchen is closing.");
    	finished = true;
	}
	
	/**
	 * Returns the current version of the report, i.e. a list of orders 
	 * in the kitchen.
	 * @return a String containing the current version of the report.
	 */
	public String getOrderReport(){
		return report;
	}
	
	/**
	 * Gets the next order.
	 * Once this method is started, it should be allowed to finish.
	 * @param o the order to be processed
	 */
	public synchronized void receiveOrder(Order o) {
		
		ordersInKitchen.add(o);

		report += o.printShortInfo() + "\r\n";
		
		setChanged();
		notifyObservers();
    	clearChanged();
	}
	
	/**
	 * Returns all the orders in the kitchen in the order they arrived to the kitchen.
	 * @return a LinkedList of all the orders in the kitchen.
	 */
	public LinkedList <Order> getOrdersInKitchen(){
		return ordersInKitchen;
	}

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
	 * Notifies the observers of a change.
	 */
	public void notifyObservers(){ 
		for( Observer obs : registeredObservers) obs.update(null, obs); 
		}
}
