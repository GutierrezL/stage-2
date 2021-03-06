import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
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
import javax.swing.JTextField;


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
	private JTextArea hatchOrders;
	private JTextArea [] tableDisplay;
	protected JTextField discountField;
	private JScrollPane scrollDown;
	private JButton getBill, startSimulation, close; 
	private JComboBox <String> dishes;
	private JComboBox <String> kitchOpen;
	protected JComboBox<String> tables;
	
	private OrderGenerator model;
	private int numOfTables;
		  
    /**
     * Create the frame with its panels.
     */
    public MVCRestaurantView(OrderGenerator model){            
        //set up window title
        setTitle("Kitchen Orders Simulation");
        //To ensure that when window is closed program ends
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		//set window size
		//setPreferredSize(new Dimension(1100, 620));
		
        this.model = model;
        model.addObserver(this);
        numOfTables = 6;
        
        setSize(100,500);
        setLocation(10,20);

        /**
         * Add centre panel containing text field and scroll pane 
         * that displays kitchen orders
         */
        JPanel centrePanel = new JPanel();
      	//centrePanel.add(new JLabel("Kitchen Orders")); 
      	kitchenOrders = new JTextArea(20, 50);
      	kitchenOrders.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
      	kitchenOrders.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
  		hatchOrders = new JTextArea(20, 50);
  		hatchOrders.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
  		hatchOrders.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
      	kitchenOrders.setEditable(false);
      	kitchenOrders.setText("LIST OF ORDERS IN THE KITCHEN");
      	hatchOrders.setEditable(false);
      	hatchOrders.setText("LIST OF ORDERS IN THE HATCH");
      	centrePanel.add(kitchenOrders);
        centrePanel.add(hatchOrders);
               
        //create container and add centre panel to content pane     
        Container contentPane = getContentPane();
        contentPane.add(centrePanel, BorderLayout.WEST);
       
        //add scroll pane to content pane     
        scrollDown = new JScrollPane();
        centrePanel.add(scrollDown,BorderLayout.CENTER);            
            
        //set up south panel containing a buttons and a combo boxes
         JPanel southPanel = new JPanel();
         
         startSimulation = new JButton ("Start");
         southPanel.add(startSimulation);
         
         //Add combo box to select orders from either text file or randomly and label
         southPanel.add(new JLabel("Kitchen open (sec):"));  
         //create combo box    
         kitchOpen = new JComboBox<String>();
         // add items to the combo box;
         kitchOpen.addItem("5");
         kitchOpen.addItem("10");
         kitchOpen.addItem("15");
         southPanel.add(kitchOpen, BorderLayout.EAST);
         
         
         //Add combo box to select orders from either text file or randomly and label
         southPanel.add(new JLabel("Dishes generated:"));  
         //create combo box    
         dishes = new JComboBox<String>();
         // add items to the combo box;
         dishes.addItem("from a textfile");
         dishes.addItem("at random");
         southPanel.add(dishes, BorderLayout.EAST);
         
         
         southPanel.add(new JLabel("Table:"));
                        
        //create first combo box to allow for selection of table ID in order to get bill 
        tables = new JComboBox<String>();
        // add items to the combo box
        for (int i = 1; i < (numOfTables+1); i++){
        	tables.addItem("#" + i);
        }
        
        //add combo box to south panel
        southPanel.add(tables, BorderLayout.BEFORE_FIRST_LINE);
        centrePanel.add(customTabDisplay(), BorderLayout.EAST);
        //button to get bill for table selected    
        southPanel.add(new JLabel("Discount (%)"));   
        discountField = new JTextField(3);
        southPanel.add(discountField);
        getBill = new JButton("Get Bill");
        getBill.setEnabled(false);
        southPanel.add(getBill);
        close = new JButton("Close application");   
        southPanel.add(close);
        //getBill.setEnabled(false);
        contentPane.add(southPanel, BorderLayout.SOUTH);

                 
        //pack and set visible
        pack();
        setVisible(true);
    }
    
       private JPanel customTabDisplay() {
    	//cheating - know there are 6 customers
    	JPanel customTablePanel = new JPanel(new GridLayout (3,2));
		tableDisplay  = new JTextArea [6];
		for (int i = 0; i < 6; i++){ 
			tableDisplay [i]= new JTextArea(10,30);
			//monospaced allows nice tabular layout
			tableDisplay[i].setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			tableDisplay [i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
			tableDisplay[i].setLineWrap(true); 
			customTablePanel.add(tableDisplay[i]);
		}
		return customTablePanel;
		
    }
   
    ///////////////// MVC pattern - allows listeners to be added
       
    public void kitchenOrderListener(ActionListener a) {
    	startSimulation.addActionListener(a);
    }
    
    public void orderBillListener(ActionListener a) {
    	getBill.addActionListener(a);
    }
    
    public void closerListener(ActionListener a) {
    	close.addActionListener(a);
    }
    
    /////////////////
    
    /**
     * Returns the order population method (from text file or random generation).
     * @return String value of order population method.
     */
    public String getPopulateMethod(){
    	String value = dishes.getSelectedItem().toString();
    	return value;
    }
    
    /**
     * Returns the duration for which the kitchen is open.
     * @return String containing the length in seconds for which the kitchen is open.
     */
    public String getKitchOpenTime(){
    	String value = kitchOpen.getSelectedItem().toString();
    	return value;
    }
    
    /**
     * Enables the Get Bill button in the GUI.
     */
    public void enableGetBillButton(){
    	getBill.setEnabled(true);
    }
    
    /**
     * Disables the Start button in the GUI.
     */
    public void disableStartButton(){
    	startSimulation.setEnabled(false);
    }
    
    //Required methods for the Observer pattern
    /**
     * Updates the GUI.
     */
    public synchronized void update(Observable o,  Object args) {
       	this.kitchenOrders.setText(model.getKitchenReport());
    	this.hatchOrders.setText(model.getHatchReport());
    	for (int i = 0; i < model.getListOfTables().size(); i++) {
    		String report = model.getOrderList(i);
    		this.tableDisplay[i].setText(report);
    	}
    	//Enables the "Get Bill" button, if the simulation has finished.
    	if (model.getSimFinished()){getBill.setEnabled(true);}
    }
}
    
   