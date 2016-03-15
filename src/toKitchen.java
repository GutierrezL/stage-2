
public class toKitchen implements Runnable {
	
	private OrderGenerator kitchen;
	public toKitchen(OrderGenerator k){
		kitchen = k;
	}
	
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		long end = start + 5*1000;
		while (System.currentTimeMillis() < end){
			try {
				kitchen.populateWithGenerator();
			} catch (InvalidPositiveInteger e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if(!kitchen.isSimulationActive())	kitchen.setStartSimulation();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("The kitchen is closing.");
    	kitchen.setFinished();
	}
}
