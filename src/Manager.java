import javax.swing.JFrame;

/**
* <h1>Manager</h1>
* Manager class contains an instance variable with all the orders requested and all 
* the items found in the menu, and connects them with the GUI
*
* @author Luis Alberto Gutierrez Iglesias
* @version 1.0
* @since 2016-02-17
*/
public class Manager {
	
	/**
	 * Class constructor
	 */
	public Manager() {
	}

	/**
	 * Calls the population method for the different collections of orders and items.
	 * In addition, creates the user interface
	 */
	public void run(){
		//ManagerGUI GUI = new ManagerGUI("Restaurant application", collections);
		//GUI.setSize(600, 500);
		//KitchenOrders model = new KitchenOrders(collections.getOrderList());
		OrderGenerator model = new OrderGenerator();
		MVCRestaurantView view = new MVCRestaurantView(model);
		MVCRestaurantController controller = new MVCRestaurantController(model, view);   
		view.setVisible(true);

	}
	
}
