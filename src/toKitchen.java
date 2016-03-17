import java.io.IOException;

public class toKitchen implements Runnable {
	
	private OrderGenerator kitchen;
	//Duration for which the kitchen is open (msec)
	private int kitchOpenTime;
	//The current line in the order input file
	private int line;
	
	public toKitchen(OrderGenerator k){
		kitchen = k;
		line = 0;
		kitchOpenTime = 0;
	}
	
	
	@Override
	public void run() {
		//Checks, for how long the kitchen will be open.
		kitchOpenTime = kitchen.getKitchOpenTime();
		//Kitchen opening time
		long start = System.currentTimeMillis();
		//Kitchen closing time
		long end = start + kitchOpenTime*1000; // converting time from msec to sec
		//While the kitchen is not closed
		while (System.currentTimeMillis() < end){
			if (kitchen.getPopulateMethod().equals("from a textfile")){
				try {
					kitchen.populateWithFile(this.line);
				} catch (IOException e) {
					e.printStackTrace();
				}
				line++;
			}else {
				try {
					kitchen.populateWithGenerator();
				} catch (InvalidPositiveInteger e1) {
					e1.printStackTrace();
				}
			} 
			if(!kitchen.isSimulationActive())	kitchen.setStartSimulation();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("The kitchen is closing.");
    	kitchen.setFinished();
	}
}
