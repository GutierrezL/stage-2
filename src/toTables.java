
public class toTables implements Runnable{

	private OrderGenerator kitchen;  
	
	public toTables(OrderGenerator k) {
		kitchen = k;
	}

	//Tables receive orders repeatedly until kitchen is empty
	public void run() {
		try { Thread.sleep(1000); }
	    catch (InterruptedException e) {}
		//loop while kitchen not empty
		while (!kitchen.hatchIsFinished() || !kitchen.isFinished()) {
			try { Thread.sleep(1000); }
		    catch (InterruptedException e) {}
			kitchen.orderToTable();
		}
	}
}
