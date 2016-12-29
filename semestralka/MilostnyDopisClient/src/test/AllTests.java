package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        NetServiceStaticTests.class,
		MessageTest.class,
		PlayerTest.class
})

public class AllTests {

}
