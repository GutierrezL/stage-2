
public class toKitchen implements Runnable {
	
	private OrderGenerator kitchen;
	public toKitchen(OrderGenerator k){
		kitchen = k;
	}
	
	
	@Override
	public void run() {
		for(int i=0;i<15;i++){
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
	}
}
