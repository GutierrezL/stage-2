import static org.junit.Assert.*;

import javax.swing.JFrame;

import org.junit.Test;

/**
 * Test case for checking the error message, for the case when the user inputs 
 * an invalid number format into the GUI in the text field where the table 
 * number should be input.
 * @author Otonye Manuel
 */

public class ManagerGUITest {

	@Test
	public void testGenerateBill() {
		OrderGenerator a = new OrderGenerator();
		//Creates a an instance of ManagerGUI.
		ManagerGUI GUI = new ManagerGUI("Restaurant application", a);
		GUI.setSize(600, 500);
		GUI.setVisible(true);
		
		//Set the text field to an invalid value format, i.e., int required,
		//but String provided
		GUI.setIDFieldString("dog");
		try {
			GUI.generateBill();
		} catch (NumberFormatException nfe){
			//Checks the content of the error message being caught
			assertTrue(nfe.getMessage().contains("Number conversion"));
		}
	}

}
