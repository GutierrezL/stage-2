import java.util.ArrayList;

public class Table implements Runnable{

	private int waitTime;   // time waiting orders
	private int tableID;     //id
	private ArrayList<Order> ordersMade;  //list of orders made
	//the kitchen, so tables can check whether finished and can receive an order
	private OrderGenerator orderGen;
	
	public Table(int t, OrderGenerator og) {
		//store id
		tableID= t;
		//each table has list of orders made, initially empty
		ordersMade = new ArrayList<Order>();

		//if speed is totally variable, we don't seem to get any processing clashes
		// i.e. (no order failures)
		waitTime = 3000 + 1000*(tableID%2);

		//store kitchen, so customer can submit a bid
		orderGen = og;
	}
	
	//add a bid to list of bids made
	public void addOrder(Order o) {
		ordersMade.add(o);
	}

	public int getTableId() {
		return this.tableID;
	}

	//returns report of all orders for this table
	public String getOrderList() {
		String report = "";
		if (ordersMade.size() == 0) {
			report += "There is no orders to show";
		}else {
			for (Order o : ordersMade) {
				report  += o.printInfo() + "\n";
			}
		}
		return report;		
	}

	//Tables receive orders repeatedly until kitchen is empty
	public void run() {
		//loop while kitchen not empty
		while (!orderGen.isFinished()) {
			try {
				if (tableID/2 == 0) {  //vary when they sleep to get variation
					//set a pause before the order
					Thread.sleep(waitTime);
				}

				//RECEIVE AN ORDER
				//get top order
				//Order firstOrder = kitchen.orderOnTop();
				//if(firstOrder.getTableID()==this.tableID){
					//add the order to the table
					//this.addOrder(firstOrder);
					//delete that order in kitchen
					//kitchen.removeFirst();
				//}

				if (tableID/2 != 0) {
					//set a pause after the order
					Thread.sleep(waitTime);
				}

			}
			catch (InterruptedException e) {
				System.out.println("Table " + tableID + " interrupted");
			}
			catch (Exception e) {
				System.out.println("Table exc" + e.getStackTrace());
				System.out.println(this.tableID);
			}

		}

	}

}
