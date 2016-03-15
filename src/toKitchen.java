
public class toKitchen implements Runnable {
	
	private OrderGenerator kitchen;
	public toKitchen(OrderGenerator k){
		kitchen = k;
	}
	
	
	@Override
	public void run() {

		int waitingTime = 100;
		
		kitchen.populateMenuItems();
		if (kitchen.getPopulateMethod().equals("from a textfile")){
			//Reads the order input file.
			kitchen.populateWithFile();
		} else {
			try {
				kitchen.populateWithGenerator();
			} catch (InvalidPositiveInteger e) {
				e.printStackTrace();
			}
		}
	}
}
