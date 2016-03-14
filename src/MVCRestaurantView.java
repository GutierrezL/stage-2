import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * @author Otonye Manuel 
 * Class that displays various GUI components 
 *
 */

public class MVCRestaurantView extends JFrame implements Observer {

	/**
	 * Instance variables for GUI
	 */
	private JTextArea kitchenOrders;
	private JTextArea [] tableDisplay;
	private JScrollPane scrollDown;
	private JButton getBill,startSimulation; 
	
	private OrderGenerator model;
	private LinkedList orders;
	private String report;
		  
    /**
     * Create the frame with its panels.
     */
    public MVCRestaurantView(OrderGenerator model)
    {              
        //set up window title
        setTitle("Kitchen Orders Simulation");
        //To ensure that when window is closed program ends
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//set window size
		setPreferredSize(new Dimension(1100, 620));
		this.model = model;
        model.registerObserver(this);
        orders = model.getOrdersInKitchen();
        report = "";


        /**
         * Add centre panel containing text field and scroll pane 
         * that displays kitchen orders
         */
        JPanel centrePanel = new JPanel();
      	//centrePanel.add(new JLabel("Kitchen Orders")); 
      	kitchenOrders = new JTextArea(30, 30);
      	kitchenOrders.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
      	kitchenOrders.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
      	kitchenOrders.setEditable(false);
        centrePanel.add(kitchenOrders);
               
         //create container and add centre panel to content pane     
         Container contentPane = getContentPane();
         contentPane.add(centrePanel, BorderLayout.WEST);
            
         //add scroll pane to content pane     
         scrollDown = new JScrollPane(kitchenOrders);
         scrollDown.setPreferredSize( new Dimension( 350, 500 ) );
         centrePanel.add(scrollDown,BorderLayout.CENTER);
          
            
        //set up south panel containing a buttons and a combo boxes
         JPanel southPanel = new JPanel();
         startSimulation = new JButton ("Start");
         southPanel.add(startSimulation);
         southPanel.add(new JLabel("Table Number"));
                        
        //create first combo box to allow for selection of table ID in order to get bill 
        JComboBox<String> tables = new JComboBox<String>();
        // add items to the combo box
        tables.addItem("Select Table No.");
        tables.addItem("Table 1");
        tables.addItem("Table 2");
        tables.addItem("Table 3");
        tables.addItem("Table 4");
        tables.addItem("Table 5");
        tables.addItem("Table 6");
            
        //add first combo box to south panel
        southPanel.add(tables, BorderLayout.BEFORE_FIRST_LINE);
        centrePanel.add(customTabDisplay(), BorderLayout.EAST);
        //button to get bill for table selected    
        getBill = new JButton("Get Bill");   
        southPanel.add(getBill); 
        getBill.setEnabled(false);
        contentPane.add(southPanel, BorderLayout.SOUTH);
            
        //add second combo box to select orders from either text file or randomly and label
        southPanel.add(new JLabel("Dishes"));  
        //create combo box    
        JComboBox<String> dishes = new JComboBox<String>();
        // add items to the combo box;
        dishes.addItem("Textfile");
        dishes.addItem("Random");
        southPanel.add(dishes, BorderLayout.EAST);
                 
        //pack and set visible
        pack();
        setVisible(true);
    }
    	//create custom panel to display text area in a matrix
        private JPanel customTabDisplay() {
    	//creating 6 text areas for 6 tables
    	JPanel customTablePanel = new JPanel(new GridLayout (3,2));
		tableDisplay  = new JTextArea [6];
		for (int i = 0; i < 6; i++){ 
			
			tableDisplay [i]= new JTextArea(10,50);
			tableDisplay[i].setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			tableDisplay [i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
			tableDisplay[i].setLineWrap(true); 
			customTablePanel.add(tableDisplay[i]);
			
		}
		return customTablePanel;
		
    }
   
    //MVC pattern - allows listeners to be added
    public void kitchenOrderListener(ActionListener a) {
    	startSimulation.addActionListener(a);
    }
    

    public void disableGetBillButton() {
    	getBill.setEnabled(false);
    }
    
    public void enableGetBillButton(){
    	getBill.setEnabled(true);
    }
    
    //Required methods for the Observer pattern
    
    /**
     * Updates the GUI.
     */
    public synchronized void update(Observable o,  Object arg) {
    	report = model.getReport();
    	kitchenOrders.setText(report);
    	
    	try {
			wait(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	/**
    	for (int i = 0; i < model.getListOfTables().getSize(); i++) {
    		String report = model.getListOfTables().get(i).getOrderList();
			this.tableDisplay[i].setText(report);	
    	}
    	*/
    }

}