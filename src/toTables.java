import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class toTables implements Runnable{

	private OrderGenerator kitchen;  
	
	public toTables(OrderGenerator k) {
		kitchen = k;
	}

	// This class sends orders from the hatch to each table
	public void run() {
		// It loops while kitchen is not closed and hatch is not empty
		while (!kitchen.hatchIsFinished() || !kitchen.isFinished()) {
			// This thread runs each second, simulating the time a waiter could take to pick an
			// order from the hatch and serve it in its corresponding table
			try { Thread.sleep(1000); }
		    catch (InterruptedException e) {}
			kitchen.orderToTable();
		} 
		try {
			Log.getInstance().outputLog();
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		// Simulation ends when kitchen closes
		kitchen.setSimFinished();
	}
}
