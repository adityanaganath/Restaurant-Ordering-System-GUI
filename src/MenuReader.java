import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.math.BigDecimal;

public class MenuReader {
	/**
	 * This class serves as an agent class that reads an inputted menu
	 * We use the singleton design pattern to create only one File instance
	 * This is so that all orders are logged on one file
	 * We use FileWriter and BufferedWriter to write to file
	 * A counter is put in place to log which order one is
	 * We import the Gregorian Calendar and Timestamp so that we can put a time and date for each order
	 * 
	 * 
	 */
	private File orders;
	private Scanner readMenu;
	private BufferedWriter orderLogger;
	private FileWriter orderLoggerStream;
	private int count;
	private String inputFile;
	private Calendar calendar;
	private ArrayList<MenuItem> menuItems;
	
	public MenuReader(File inputMenu) throws FileNotFoundException {
		
		menuItems = new ArrayList<MenuItem>();
		readMenu = new Scanner(inputMenu);
		calendar = new GregorianCalendar();
		count = 1;
		inputFile = inputMenu.getName();
	}
	
	public void readInputFile() {
		/**
		 * Iterative pattern to read information from the file
		 * Add items to an arraylist of items based - characterized by name and price
		 */
		while (readMenu.hasNextLine()) {
			
			String item = readMenu.nextLine();
			String itemCost = readMenu.nextLine();
			BigDecimal itemPrice = new BigDecimal(itemCost);
			
			MenuItem createItem = new MenuItem(item, itemPrice);
			menuItems.add(createItem);
		}
		readMenu.close();
	}
	
	/**
	 * Returning the created arraylist.
	 * @return
	 */
	public ArrayList<MenuItem> getMenuItems() {
		return menuItems;
	}
	
	/**
	 * This method handles the operation for logging an order when the waiter presses place order
	 * We format the order by including the dates and time(note: month+1 is because of the inconsistency of month in the calendar class)
	 * We also list the total price and menu items. We throw an IO Exception as a checked exception
	 * @param itemsOrdered
	 * @param orderPrice
	 * @throws IOException
	 */
	public void logOrder(ArrayList<MenuItem> itemsOrdered, BigDecimal orderPrice) throws IOException {
		/**
		 * File Writer and BufferedWriter work in tandem to write the order onto the file instance
		 * We record the appropriate date and time and use appropriate syntax from the date class
		 * counter is incremented to reflect the order #
		 * TimeStamp records the present time as well
		 * We must use close() on orderLogger so that it can write onto the file
		 */
		orderLoggerStream = new FileWriter(getFileInstance(),true);
		orderLogger = new BufferedWriter(orderLoggerStream);
		
		java.util.Date orderDate = new java.util.Date();
		
		if(count == 1) {
		
		orderLogger.write( "Date: " + calendar.get(Calendar.DAY_OF_MONTH) 
		+ " " + calendar.get((Calendar.MONTH +1)) + " " + calendar.get(Calendar.YEAR) + " " + "\n \n");

		}
		
		orderLogger.write("Order number" + count + ", ");
		count++;
		orderLogger.write(" " + new Timestamp(orderDate.getTime()) + "\n");
		
		for (MenuItem item: itemsOrdered) {
			orderLogger.write(item.itemToString() + "\n");
			
		}
		
		orderLogger.write("Total Price is: $" + orderPrice + "\n \n");
		orderLogger.close();
	}

	/**
	 * Singleton pattern - for returning one file instance.
	 * We make this method private and only return a file if the instance is null
	 * Thus we create just one file that we can write to instead of multiple files
	 * @return
	 */
	private File getFileInstance() {
		
		if (orders == null) {
			String fileName = inputFile + " " + "ItemOrders.txt";
			orders = new File(fileName);
		}
		return orders;
	}
}

