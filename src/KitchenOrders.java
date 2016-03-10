import java.util.LinkedList;
import java.util.Observable;

public class KitchenOrders extends Observable implements  Runnable {
	
	
	//List of orders
	private LinkedList<Order> orders;
	
	private LinkedList<Order> ordersInKitchen;
	
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean finished = false;
	
	private MVCRestaurantView view;
	
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
	 * The thread run method
	 */
	public void run() {
		
		for (Order o: orders){
			ordersInKitchen.add(o);
			System.out.println(o.getItemName());
		}
		
		Thread kitchOrderThread = new Thread();
		kitchOrderThread.start();
		
		try {
    		Thread.sleep(10000);
    	}
		catch (Exception e) {
			System.out.println("KitchenOrder thread exception" + e.getStackTrace());
		}
    	
    	System.out.println("The kitchen is closing.");
    	finished = true;
	}
	
	public String getOrderReport(){
		String report = "LIST OF ORDERS \r\n "
  				+ "ID     ITEM          QUANTITY  TABLE    ";
    	for (Order ord: ordersInKitchen) {
    		String order_details = ord.getOrderID() + "   " + ord.getItemName() + "   " + 
    				ord.getQuantity() + "  " + ord.getTableID() + "\r\n";
    		report += order_details;
    	}
			return report;	
	}
	
	/**
	 * Gets the next order.
	 * Once this method is started, it should be allowed to finish.
	 * @param o the order to be processed
	 */
	public synchronized void receiveOrder() {
		
		setChanged();
		notifyObservers();
    	clearChanged();
	}
	
	public LinkedList <Order> getOrders(){
		return orders;
	}
	
}
