
/**
* <h1>Manager</h1>
* Manager class is responsible for creating each component of the project's
* MVC architecture: GUI as view, the different collections as model and a
* controller class which joins view and model.
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
		OrderGenerator model = new OrderGenerator();
		MVCRestaurantView view = new MVCRestaurantView(model);
		MVCRestaurantController controller = new MVCRestaurantController(model, view);   
		view.setVisible(true);

	}	
}
