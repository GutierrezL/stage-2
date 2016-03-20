import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

public class Log {
	
	private static final Log instance = new Log();
	private String logText;
	
	//private constructor
	private Log(){
		logText = "THE DAILY LOG \r\n";
	}
	
	/**
	 * Returns the current version of the Log class instance.
	 */
	public static Log getInstance(){
		return instance;
	}
	
	/**
	 * Adds a new entry to the Log class instance.
	 * @param log_entry the String containing the new entry.
	 */
	public void addEntry(String log_entry){
		logText += log_entry;
	}
	
	/**
	 * Returns all the log entries.
	 * @return String containing all the log entries.
	 */
	public String getLogEntries(){
		return logText;
	}
	
	/**
	 * Creates an output file of the Log.
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	public void outputLog() throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer = new PrintWriter("ActionLog.txt", "UTF-8");
		writer.print(logText);
		writer.close();
	}
}
