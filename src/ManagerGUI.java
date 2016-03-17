import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/*
 * Advanced Software Engineering coursework
 * @author Otonye Manuel
 */

/**
* ManagerGUI class is a class that controls the GUI
* Gets the discount, prints table order to text area and
* summary to an output file
** */

public class ManagerGUI extends JFrame implements ActionListener{ 
		
	private static final long serialVersionUID = 1L;
	
	/**
	 * Instance variables for GUI
	 */

	private JTextArea displaySearch;
	private JScrollPane scrollSearch;
	private JButton close, printBtn, getBill;
	private JTextField tableIdField, discountField;
	private OrderGenerator collections;
	
	/**
     * Creates the frame with its panels.
     * @param title The GUI title
     * @param Aggregator The class that joins all the different collections
     **/
	public ManagerGUI(String title, OrderGenerator a){
		setTitle(title);		
		// Disable standard close button, since the reports will be created when
        // the personalized Close button is pressed
		setDefaultCloseOperation(ManagerGUI.DO_NOTHING_ON_CLOSE);
		
		collections = a;
		//add north panel containing a button and text fields
		JPanel northPanel = new JPanel();
        northPanel.add(new JLabel("Table ID"));   
        tableIdField = new JTextField(5);
        northPanel.add(tableIdField);   
        northPanel.add(new JLabel("Discount (%)"));   
        discountField = new JTextField(3);
        northPanel.add(discountField);   
        getBill = new JButton("Generate bill"); 
        
        //specify action when button is pressed
        getBill.addActionListener(this);
        northPanel.add(getBill);   
        
      //add north panel to the content pane
        Container contentPane = getContentPane();
        contentPane.add(northPanel,BorderLayout.NORTH);
        
        //The area where the results will be displayed.
        displaySearch = new JTextArea(15,20);
        displaySearch.setFont(new Font (Font.MONOSPACED, Font.PLAIN,13));
        displaySearch.setEditable(false);
        scrollSearch = new JScrollPane(displaySearch);
        contentPane.add(scrollSearch,BorderLayout.CENTER);
        
                
        //set up south panel containing 2 buttons
        JPanel southPanel = new JPanel();
        printBtn = new JButton("Print Summary");
        
        //specify action when button is pressed
        printBtn.addActionListener(this);
        close = new JButton("Close application");
        
        //specify action when button is pressed
        close.addActionListener(this);
        southPanel.add(printBtn);
        southPanel.add (close);
        contentPane.add(southPanel, BorderLayout.SOUTH);
                                       
        //pack and set visible
        pack();
        setVisible(true);
    }
	
	/**
	 * The class which processes the ActionEvent 
	 * which implements this interface
	 */
	
	public void actionPerformed(ActionEvent e){
		
		if (e.getSource() == printBtn){
			displaySearch.setText(collections.printBills());
		}else if(e.getSource() == getBill){
			generateBill();
		}else if(e.getSource() == close){
			collections.writer("Report.txt", collections.reporter());
			System.exit(0);
		}
	}
	
	/**
	 * If a discount is provided, it creates a new entry for the specified table in the discount collection If not, 
	 * removes a possible entry for that table. This will allow the automatic discounts work
	 */
		public void generateBill(){
		try{
			String numberText = tableIdField.getText().trim();
			String discountText = discountField.getText().trim();
			if(!numberText.equals("")){
				int number = Integer.parseInt(numberText);
				if(!discountText.equals("")){
					int discount = Integer.parseInt(discountText);
					collections.updateDiscounts(number, discount);
				}else{
					collections.deleteDiscount(number);
				}
				displaySearch.setText(collections.getTableBill(number));
			}
		}
		catch (NumberFormatException nfe) {
			// It would not process that line, but print the error message
			String error = "Number conversion error.\nPlease, make sure you've used numbers as input";
			displaySearch.setText(error);
		}
	}
		
	/**
	 * Sets the text of the tableIdField (mainly for testing purposes)
	 * @param input String value to be set as the tableIdField value
	 */
	public void setIDFieldString(String input){
		tableIdField.setText(input);
	}
		
}
