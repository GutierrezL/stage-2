package adSoftware_Part2;

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
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * @author Otonye Manuel 
 * Class that displays various GUI components 
 *
 */

public class RestaurantGUIView extends JFrame implements Observer{
	//private int tabNo;
	/**
	 * Instance variables for GUI
	 */
	private JTextArea kitchenOrders;
	private JTextArea [] tableDisplay;
	private JScrollPane scrollDown;
	private JButton getBill;
		  
    /**
     * Create the frame with its panels.
     */
    public RestaurantGUIView()
    {              
        //set up window title
        setTitle("Simulating Kitchen Orders");
        //To ensure that when window is closed program ends
		setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(100,500);
        setLocation(10,20);
 
      //add centre panel containing text fields and scroll pane 
      		JPanel centrePanel = new JPanel();
      		//centrePanel.add(new JLabel("Kitchen Orders")); 
      		kitchenOrders = new JTextArea(30, 30);
      		kitchenOrders.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
      		kitchenOrders.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
            centrePanel.add(kitchenOrders);
            
       //create container and add centre panel to content pane     
            Container contentPane = getContentPane();
            contentPane.add(centrePanel, BorderLayout.WEST);
       
       //add scroll pane to content pane     
            scrollDown = new JScrollPane();
            centrePanel.add(scrollDown,BorderLayout.CENTER);
            
        //set up south panel containing a button and a combo box
            JPanel southPanel = new JPanel();
            getBill = new JButton("Get Bill");   
            southPanel.add(getBill);   
            contentPane.add(southPanel, BorderLayout.SOUTH);
            
         //create combo box    
            JComboBox<String> tables = new JComboBox<String>();
        // add items to the combo box
            tables.addItem("Select Table No.");
            tables.addItem("Table 1");
            tables.addItem("Table 2");
            tables.addItem("Table 3");
            tables.addItem("Table 4");
            tables.addItem("Table 5");
            tables.addItem("Table 6");
            
        //add combo box to south panel
            southPanel.add(tables, BorderLayout.BEFORE_FIRST_LINE);
            centrePanel.add(customTabDisplay(), BorderLayout.EAST);
                 
        //pack and set visible
        pack();
        setVisible(true);
    }
    
       private JPanel customTabDisplay() {
    	//cheating - know there are 6 customers
    	JPanel customTablePanel = new JPanel(new GridLayout (2,3));
		tableDisplay  = new JTextArea [6];
		for (int i = 0; i < 6; i++){ 
			tableDisplay [i]= new JTextArea(15,30);
			//monospaced allows nice tabular layout
			tableDisplay[i].setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
			tableDisplay [i].setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.LIGHT_GRAY));
			customTablePanel.add(tableDisplay[i]);
		}
		return customTablePanel;
    }
   
      //MVC pattern - allows listeners to be added
    public void kitchenTableOrderListener(ActionListener a) {
        getBill.addActionListener(a);
    }
    
   
        //OBSERVER pattern - must provide update methods
    
    public synchronized void update(Observable o, Object args) {
    	
    	}
    

}
