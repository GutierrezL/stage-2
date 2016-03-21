import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/*
 * This JUnit Suite joins all the test cases created
 */

@RunWith(Suite.class)
@SuiteClasses({ OrderGeneratorTest.class, MenuItemTest.class, OrderTableTest.class, OrderTest.class })
public class AllTests {

}
