import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Otonye Manuel
 * Class that handles interaction with users and 
 * calls view and model as needed
 */
public class GUIController {
	
	private GUIModel model;
	private RestaurantGUIView view;
	
	public GUIController (GUIModel m, RestaurantGUIView v){
		model  = m;
		view = v;
		view.kitchenTableOrderListener(new restaurantController());
		
	}
	
	class restaurantController  implements ActionListener
{
    public void actionPerformed(ActionEvent e) 
    { 
    	//Threads are implemented here..

		
    }
}




}