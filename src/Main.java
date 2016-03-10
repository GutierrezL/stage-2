/**
* <h1>Restaurant Application</h1>
* This application simulates the functionalities of a restaurant software, containing 
* a list of orders and items included in the menu. The program reads data from text 
* files and writes its results in a different one.
*
* @author Linda Viksne
* @author Otonye Manuel
* @author Luis Alberto Gutierrez Iglesias
* @version 1.1
* @since 2016-02-17
* F21AS - Advanced Software Engineering Coursework
* MSc in Information Technology & MSc in Software Engineering
* School of Mathematical and Computer Sciences
* Heriot-Watt University
*/
public class Main {

	public static void main(String[] args) {
		
		Manager manager = new Manager();
		manager.run();
		
	}
}
