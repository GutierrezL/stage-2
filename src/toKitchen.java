
public class toKitchen implements Runnable {
	
	private OrderGenerator kitchen;
	private int kitchOpenTime;
	public toKitchen(OrderGenerator k){
		kitchen = k;
	}
	
	
	@Override
	public void run() {
		kitchOpenTime = kitchen.getKitchOpenTime();
		long start = System.currentTimeMillis();
		long end = start + kitchOpenTime*1000; // multiplied because the value is in msec
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
