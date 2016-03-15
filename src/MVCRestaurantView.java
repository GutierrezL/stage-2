import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
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
import javax.swing.SwingUtilities;


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
	private JScrollPane scrollDown;
	private JButton getBill,startSimulation; 
	private JComboBox <String> dishes;
	private JComboBox <String> kitchOpen;
	
	private OrderGenerator model;
	private String report;
	
		  
    /**
     * Create the frame with its panels.
     */

    public MVCRestaurantView(OrderGenerator model){            
        //set up window title
        setTitle("Kitchen Orders Simulation");
        //To ensure that when window is closed program ends
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//set window size
		//setPreferredSize(new Dimension(1100, 620));
		this.model = model;
        model.addObserver(this);
        report = "";
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
        JComboBox<String> tables = new JComboBox<String>();
        // add items to the combo box
        tables.addItem("All");
        for (int i = 1; i < 7; i++){
        	tables.addItem("#" + i);
        }

        
        //add first combo box to south panel
        southPanel.add(tables, BorderLayout.BEFORE_FIRST_LINE);
        centrePanel.add(customTabDisplay(), BorderLayout.EAST);
        //button to get bill for table selected    
        getBill = new JButton("Get Bill");   
        southPanel.add(getBill); 
        getBill.setEnabled(false);
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
   
    //MVC pattern - allows listeners to be added
    public void kitchenOrderListener(ActionListener a) {
    	startSimulation.addActionListener(a);
    }
    

    public String getPopulateMethod(){
    	String value = dishes.getSelectedItem().toString();
    	return value;
    }
    
    public String getKitchOpenTime(){
    	String value = kitchOpen.getSelectedItem().toString();
    	return value;
    }
    
    public void updateKitchenPanel(){
    	kitchenOrders.setText(report);
    }
    
    
    /**
     * Enables the Get Bill button in the GUI.
     */
    public void enableGetBillButton(){
    	getBill.setEnabled(true);
    }
    
    //Required methods for the Observer pattern
    
    /**
     * Updates the GUI.
     */
    public synchronized void update(Observable o,  Object args) {
    	
    	//report = model.getReport();
    	this.kitchenOrders.setText(model.getKitchenReport());
    	this.hatchOrders.setText(model.getHatchReport());
    	for (int i = 0; i < model.getListOfTables().size(); i++) {
    		String report = model.getOrderList(i);
    		this.tableDisplay[i].setText(report);
    	}
    }
}
    
   