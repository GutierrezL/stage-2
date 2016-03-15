import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class OrderGenerator extends Observable implements  Runnable{
	//threads for tables
	private ArrayList<LinkedList<Order>> tables;
	//List of orders
	private LinkedList<Order> orders;
	private LinkedList<Order> ordersInKitchen;
	
	private LinkedList<Order> hatch;
	private MenuItemMap menuItemMap;
	
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean startSimulation;
	private boolean finished;
	private boolean hatchFinished;
	//private MVCRestaurantView view;
	
	private List<Observer> registeredObservers = new LinkedList<Observer>();
	
	public OrderGenerator (LinkedList <Order> order_list, MenuItemMap menu){
		orders = order_list;
		menuItemMap = menu;
		ordersInKitchen = new LinkedList <Order>();
		hatch = new LinkedList <Order>();
		tables = new ArrayList<LinkedList<Order>>();
		for (int i = 1; i <=6; i++) {
			tables.add(new LinkedList<Order>());
		}
		finished = false;
		hatchFinished = false;
		startSimulation = false;
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
	 * Indicates the end of the working hours of the kitchen, i.e. no more orders accepted.	
	 */
	//indicates end of auction
	public void setFinished() {
			finished = true;
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
				report += "There is no orders to show";
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

	@Override
	/**
	 * The thread run method.
	 */
	public void run() {
		Thread kitchOrderThread = new Thread();
		kitchOrderThread.start();
		
		toHatch firstStep = new toHatch(this);
		Thread sendToHatch = new Thread(firstStep);
		sendToHatch.start();
		
		toTables secondStep = new toTables(this);
		Thread sendToTables = new Thread(secondStep);
		sendToTables.start();
		
		for (Order o: orders){			
			receiveOrder(o);
			if(!this.startSimulation)	this.startSimulation = true;
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * Returns the current version of the report, i.e. a list of orders 
	 * in the kitchen.
	 * @return a String containing the current version of the report.
	 */
	public String getOrderReport(){		
		String report = "LIST OF ORDERS IN THE KITCHEN \r\n" + String.format("%-9s", "ID")+
				String.format("%-5s", "TABLE")+ String.format("%-22s", "QUANTITY")
				+ "QUANT \r\n";
		for (Order ord: ordersInKitchen) {
			report += ord.printShortInfo() + "\r\n";
		}
		return report;	
	}
	
	public String getHatchReport(){		
		String report = "LIST OF ORDERS IN THE HATCH \r\n" + String.format("%-9s", "ID")+
				String.format("%-5s", "TABLE")+ String.format("%-22s", "QUANTITY")
				+ "QUANT \r\n";
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
		setChanged();
		notifyObservers();
    	clearChanged();
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
