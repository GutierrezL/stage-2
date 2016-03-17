import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Otonye Manuel
 * Class that handles interaction with users and 
 * calls view and model as needed
 */
public class MVCRestaurantController{
	
	private OrderGenerator model;
	private MVCRestaurantView view;
	
	public MVCRestaurantController (OrderGenerator m, MVCRestaurantView v){
		model = m;
		view = v;
		view.kitchenOrderListener(new restaurantController());
	}
	
	class restaurantController  implements ActionListener
{	
    public void actionPerformed(ActionEvent ae) { 
    	if (ae.equals("Start")){ 
    		String popValue = view.getPopulateMethod();
    		model.setPopulateMethod(popValue);
    		String durValue = view.getKitchOpenTime();
    		model.setKitchOpenTime(durValue);
    		System.out.println(ae.getActionCommand());
    		model.start();
    		view.disableStartButton();
    		}
    	}
	}
}
