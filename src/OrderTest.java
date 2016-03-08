import static org.junit.Assert.*;

import org.junit.Test;
/**
* <h1>OrderTest</h1>
* This class consists of different JUnit cases to test several methods of Order class
*
* @author Luis Alberto Gutierrez Iglesias
* @version 1.0
* @since 2016-02-18
*/
public class OrderTest {

	@Test
	public void testOrder() throws InvalidPositiveInteger {
		Order o1 = new Order(1, "Chicken", 2);
		assertEquals(9,o1.getOrderID().length());
		assertTrue(o1.getOrderID().substring(1).matches("\\d+"));
		assertEquals('O',o1.getOrderID().charAt(0));
		assertEquals("Chicken",o1.getItemName());
		Order o2 = new Order(1, "Lasagna", 1);
		assertEquals(9,o2.getOrderID().length());
		assertTrue(o2.getOrderID().substring(1).matches("\\d+"));
		assertNotEquals(o1.getOrderID(),o2.getOrderID());
		assertEquals('O',o2.getOrderID().charAt(0));
		assertNotEquals("Chicken",o2.getItemName());
	}

	@Test
	public void testCompareTo() throws InvalidPositiveInteger {
		Order o1 = new Order(1, "Chicken", 2);
		Order o2 = new Order(1, "Lasagna", 1);
		Order o3 = new Order(3, "Tuna", 4);
		Order o4 = new Order(2, "Tuna", 2);
		assertEquals(1,o4.compareTo(o1));
		assertEquals(1,o4.compareTo(o2));
		assertEquals(-1,o4.compareTo(o3));
		assertEquals(0,o1.compareTo(o2));
	}

	@Test
	public void testEqualsObject() throws InvalidPositiveInteger {
		Order o1 = new Order(1, "Chicken", 2);
		Order o2 = new Order(2, "Tuna", 1);
		Order o3 = new Order(3, "Tuna", 4);
		Order o4 = new Order(2, "Tuna", 2);
		Order o5 = null;
		String o6 = "1, Chicken, 2";
		assertTrue(o1.equals(o1));
		assertFalse(o1.equals(o2));
		assertTrue(o2.equals(o4));
		assertFalse(o1.equals(o6));
		assertTrue(o4.equals(o2));
		assertFalse(o3.equals(o4));
		assertFalse(o4.equals(o5));
	}

}
