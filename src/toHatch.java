import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

public class toHatch implements Runnable{

	private OrderGenerator kitchen;
	
	public toHatch(OrderGenerator k) {
		kitchen = k;
	}

	//Tables receive orders repeatedly until kitchen is empty
	public void run() {
		int waitingTime = 100;
		boolean orderAvailable = false;
		//try { Thread.sleep(1000); }
	   // catch (InterruptedException e) {}
		//loop while kitchen not empty
		while ((!kitchen.isFinished()) || (!kitchen.noOrdersInKitchen())) {
			if(kitchen.isSimulationActive()&&(!kitchen.noOrdersInKitchen())){
				waitingTime = kitchen.getMenuItemMap().findByName(kitchen.getFirstOrder().getItemName()).getPreparationTime() * 200;
				if(!orderAvailable)	orderAvailable = true;
			}else if((!kitchen.isFinished()) && (kitchen.noOrdersInKitchen()))
				orderAvailable = false;
			try { Thread.sleep(waitingTime); }
			catch (InterruptedException e) {}
			if(orderAvailable)	kitchen.orderToHatch();
		}
	}
}
