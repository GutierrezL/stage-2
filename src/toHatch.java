
public class toHatch implements Runnable{

	private OrderGenerator kitchen;
	
	public toHatch(OrderGenerator k) {
		kitchen = k;
	}

	//This class sends orders from the kitchen to the hatch once they are ready
	public void run() {
		// At the beginning, this thread tries to run each 100 miliseconds till the kitchen receives the first order
		int waitingTime = 100;
		boolean orderAvailable = false;
		// If the kitchen is not closes and has ready orders, the thread keep sending orders to the hatch
		while ((!kitchen.isFinished()) || (!kitchen.noOrdersInKitchen())) {
			if(kitchen.isSimulationActive()&&(!kitchen.noOrdersInKitchen())){
				// The thread waits as much time as the order takes to be prepared, simulation the real functioning of a kitchen
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
