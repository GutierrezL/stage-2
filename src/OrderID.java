import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * <h1>OrderID</h1>
 * Following the Singleton pattern, this class is responsible for allocating correct
 * sequence numbers to each order. It stores the last identifier assigned and avoids
 * two different threads creating an order with identical identifier at the same time.
 */
public class OrderID {

	private static OrderID instance;

	private String lastID;

	private OrderID(){
		lastID = "O00000000";
	}

	public static synchronized OrderID getInstance(){
		if(instance==null){
			instance = new OrderID();
		}
		return instance;
	}

	public synchronized String getNext(){
		NumberFormat myFormat = new DecimalFormat("00000000");
		int i = Integer.parseInt(lastID.substring(1)) + 1;
		lastID = "O" + myFormat.format(i);
		return lastID;
	}

}