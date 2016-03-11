import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Otonye Manuel
 * Class that handles interaction with users and 
 * calls view and model as needed
 */
public class MVCRestaurantController {
	
	private KitchenOrders model;
	private MVCRestaurantView view;
	
	public MVCRestaurantController (KitchenOrders m, MVCRestaurantView v){
		model  = m;
		view = v;
		view.kitchenTableOrderListener(new restaurantController());
		
		//Starts automatically, if we want it to start with the push of a button
		//needs to be moved under "actionPerformed"
		Thread thread = new Thread (model);
		thread.start();
	}
	
	class restaurantController  implements ActionListener
{
    public void actionPerformed(ActionEvent e) 
    { 
    	//If we want to start simulation with the push of a button
    	//Thread thread = new Thread (model);
		//thread.start();
    }
}




}