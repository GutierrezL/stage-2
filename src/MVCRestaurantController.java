import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;

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
		view.orderBillListener(new billController());
		view.closerListener(new closerController());
	}
	
	class restaurantController  implements ActionListener
	{	
	    public void actionPerformed(ActionEvent ae) 
	    { 
	    	//Gets order population method from the GUI.
	    	String popValue = view.getPopulateMethod();
	    	model.setPopulateMethod(popValue);
	    	//Gets the length of time the kitchen will be open from the GUI.
	    	String durValue = view.getKitchOpenTime();
	    	model.setKitchOpenTime(durValue);
			model.start();
			view.disableStartButton();
	    }
	 }
	
	class billController  implements ActionListener
	{	
	    public void actionPerformed(ActionEvent ae) 
	    { 
	    	javax.swing.UIManager.put("OptionPane.messageFont", new Font(Font.MONOSPACED, Font.PLAIN, 12));
	    	try{
				String numberText = view.tables.getSelectedItem().toString().substring(1);
				String discountText = view.discountField.getText().trim();
				if(!discountText.equals("")){
					if(Integer.parseInt(discountText) < 0 || Integer.parseInt(discountText) > 100){
						String error = "Provided discount is not a correct percentage";
						JOptionPane.showMessageDialog(view, error, "Bill could not be generated", JOptionPane.ERROR_MESSAGE);
					}else
						JOptionPane.showMessageDialog(view, model.generateBill(numberText, discountText), "Bill for TABLE " + numberText, JOptionPane.PLAIN_MESSAGE);
				}else
					JOptionPane.showMessageDialog(view, model.generateBill(numberText, discountText), "Bill for TABLE " + numberText, JOptionPane.PLAIN_MESSAGE);
	    	}
			catch (NumberFormatException nfe) {
				String error = "Number conversion error.\nPlease, make sure you've used numbers as discount";
				JOptionPane.showMessageDialog(view, error, "Bill could not be generated", JOptionPane.ERROR_MESSAGE);
			}
	    }
	 }
	
	class closerController  implements ActionListener
	{	
	    public void actionPerformed(ActionEvent ae) 
	    { 
	    	model.writer("Report.txt", model.reporter());
			System.exit(0);
	    }
	 }
}
