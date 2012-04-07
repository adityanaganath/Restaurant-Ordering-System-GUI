import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
/**
 * This class creates the main frame and serves as the primary interface
 * We employ inheritance to absorb the features of JFrame while adding our own
 * We split the frame into one horizontal panel on the right for displaying items ordered
 * A main/center panel for displaying all the menu items
 * A lower panel for placing orders/clearing orders
 * MainFrame amongst others has a menureader as an IV, menu items, and the receipt panel
 * @author adityanaganath
 *
 */
public class MainFrame extends JFrame {

	private BigDecimal totalCost;
	private MenuReader menuRead;
	private JPanel receipt;
	private JPanel centerPanel;
	private JTextField orderPrice;
	private ArrayList <MenuItem> itemsOrdered;
	private JTextPane orderItems;
	private String itemInformation;
	
	public MainFrame(File givenMenu) throws FileNotFoundException {
		/**
		 * Initializing IVs 
		 */
		totalCost = new BigDecimal(0);
		itemInformation = "";
		
		itemsOrdered = new ArrayList<MenuItem>();
		menuRead = new MenuReader(givenMenu);
		menuRead.readInputFile();
		create();
		
		setSize(1500,1500);
		setTitle("Restaurant Menu System");
		setBackground(Color.WHITE);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

	}
	
	/**
	 * We use a border layout here
	 * We split the pane horizontally with the ordered items on the right and item buttons to the left
	 * We get the panels for the frame and put it in the divided pane
	 */
	public void create() {
		JPanel mainPanel = (JPanel) getContentPane();
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, getItemButtons(), getReceipt());
		
		splitPane.setDividerLocation(780);
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(splitPane, BorderLayout.CENTER);
		
		
	}
	/**
	 * An important method that allows for scrolling. This is crucial for long menus that wont fit on the same screen
	 * The button panel is created in a standard grid layout and eventually returns a scroll pane with all buttons
	 * @return
	 */
	private JScrollPane getItemButtons() {
		JPanel pan = new JPanel();
		pan.setLayout(new GridLayout(0,2));
		
		ArrayList<MenuItem> itemButtons = menuRead.getMenuItems();
		/**
		 * making a button for each item
		 * Adding action listeners so that they can respond to clicks
		 * Refresh panel is a private method that updates the right panel to reflect the current status of the order
		 */
		for (final MenuItem itemButton: itemButtons) {

			final JButton createButton = new JButton(itemButton.getName());
			createButton.setToolTipText(itemButton.getName());
			
			createButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					refreshPanel(itemButton);
				}
			});
			pan.add(createButton);
			createButton.setPreferredSize(new Dimension(30,60));
			
		}
		/**
		 * Specifying the border specification and add a scroll pane to the main button panel
		 * Specifying the nature of the border
		 * Returning scroller since we have implemented a scrollable panel
		 */
		JScrollPane scroller = new JScrollPane(pan);
		Border etchedBorder = BorderFactory.createEtchedBorder();
		Border border = BorderFactory.createTitledBorder(etchedBorder, "Items",TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Lucida", Font.BOLD, 20) , Color.BLACK);
		pan.setBorder(border);
		return scroller;

		}
	/**
	 * Receipt panel deals with the current order
	 * Specify all the dimensions and colors
	 * We add a scroll pane here too incase the order is very long
	 * The textfield is constantly updated with the current price
	 * We set the textfield.setEditable to false so that it cannot be altered by the user
	 * Place order and Clear Order buttons are added here with their respective ActionListeners
	 * @return
	 */
	private JPanel getReceipt() {
		
		receipt = new JPanel();
		JLabel label = new JLabel("Customer Order:");
		receipt.setLayout(new BorderLayout());
		
		JPanel lowerPanel = new JPanel();
		lowerPanel.setLayout(new BorderLayout());
		
		receipt.add(lowerPanel,BorderLayout.SOUTH);
		receipt.add(label, BorderLayout.NORTH);
		
		centerPanel = new JPanel();
		centerPanel.setLayout(new GridLayout(0,1));
		
		orderItems = new JTextPane();
		centerPanel.add(orderItems);
		
		orderItems.setEditable(false);
		
		JScrollPane centerPanelScroller = new JScrollPane(centerPanel);
		receipt.add(centerPanelScroller, BorderLayout.CENTER);
		
		orderPrice = new JTextField(20);
		orderPrice.setText("Total Cost = $0.00");
		orderPrice.setEditable(false);
		
		JButton placeOrder = new JButton("Place Order");
		JButton clearOrder = new JButton("Clear Order");
		
		placeOrder.setPreferredSize(new Dimension(30,50));
		clearOrder.setPreferredSize(new Dimension(30,50));
		
		centerPanel.setBackground(Color.LIGHT_GRAY);
		placeOrder.setForeground(Color.BLUE);
		clearOrder.setForeground(Color.RED);
		
		placeOrder.setFont(new Font ("Times New Roman", Font.BOLD,40));
		clearOrder.setFont(new Font ("Times New Roman", Font.BOLD,40));
		
		clearOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				/**
				 * private method that clears all content
				 */
				delete();
				
			}
			
		});
		
		placeOrder.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				/**
				 * We call menu read and log our order. Also display an option pane to notify.
				 * If there is no order, then an option pane will notify a user that there is not one
				 * 
				 */
				try {
					if (!orderPrice.getText().equals("Total Cost = $0.00")) {
						menuRead.logOrder(itemsOrdered, totalCost);
						JOptionPane.showMessageDialog(getContentPane(), "Order has been sent to kitchen", "Order has been logged", JOptionPane.INFORMATION_MESSAGE);
						delete();
					}
					else {
						JOptionPane.showMessageDialog(null,"No items ordered", "Place order", JOptionPane.ERROR_MESSAGE);
					}
					
				}
				 catch (IOException g) {
					
					 JOptionPane.showMessageDialog(null, "Error! Program terminated"
							 , " Error", JOptionPane.ERROR_MESSAGE);
				}
				
			}
			
		});
		/**
		 * Adding to the panel
		 */
		lowerPanel.add(orderPrice, BorderLayout.NORTH);
		lowerPanel.add(placeOrder, BorderLayout.CENTER);
		lowerPanel.add(clearOrder, BorderLayout.SOUTH);
		lowerPanel.setBackground(Color.LIGHT_GRAY);
		receipt.setBackground(Color.WHITE);
		return receipt;
		
	}
	
	private void delete() {
		
		orderPrice.setText("Total Cost = $0.00");
		totalCost = new BigDecimal(0);
		itemsOrdered.clear();
		itemInformation = "";
		orderItems.setText(null);
		
	}
	/**
	 * Constantly updates the order panel based on commands
	 * @param itemButton
	 */
	private void refreshPanel(final MenuItem itemButton) {
		String item = itemButton.getName();
		BigDecimal itemPrice = itemButton.getCost();
		 itemInformation += "\n" + item + "\n" + itemPrice + "\n";
		 orderItems.setText(itemInformation);
		 itemsOrdered.add(itemButton);
		
		totalCost = totalCost.add(itemPrice) ;
		orderPrice.setText("Total cost = $" + totalCost);
	}


}
