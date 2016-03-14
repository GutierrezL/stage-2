import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

/**
 * @author Otonye Manuel
 * Class that handles interaction with users and 
 * calls view and model as needed
 */
public class MVCRestaurantController {
	
	private OrderGenerator model;
	private MVCRestaurantView view;
	
	public MVCRestaurantController (OrderGenerator m, MVCRestaurantView v){
		model = m;
		view = v;
		view.kitchenOrderListener(new restaurantController());
	}
	
	class restaurantController  implements ActionListener
{
    public void actionPerformed(ActionEvent ae) 
    { 

    	String value = view.getPopulateMethod();
    	model.setPopulateMethod(value);
    	
    	Thread kitchOrderThread = new Thread(model);
		kitchOrderThread.start();
	
		try {
    		Thread.sleep(10000);
    	} catch (Exception e) {
			System.out.println("KitchenOrder thread exception" + e.getStackTrace());
		}
    	
		System.out.println("The kitchen is closing.");
    	model.setFinished();
    	view.enableGetBillButton();
    	try {
			Log.getInstance().outputLog();
		} catch (FileNotFoundException | UnsupportedEncodingException e1) {
			e1.printStackTrace();
		} 
    }
 }
}

