import java.util.Observable;

public class Kitchen extends Observable implements  Runnable {
	
	//List of orders
	//private ArrayList custs = new CustomerList();
	//Set to true when the kitchen closes, i.e. no more orders accepted
	private boolean finished = false;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	

	/**
	 * Returns, if the kitchen is still open, i.e. still taking new orders.
	 * @return true, if the closed, and false, if the kitchen is still open.
	 */
		public boolean isFinished() {
			return finished;
		}
	
	/**
	 * Indicates the end of the working hours of the kitchen, i.e. no more orders accepted.	
	 */
	//indicates end of auction
	public void setFinished() {
			finished = true;
		}
	
	
}
