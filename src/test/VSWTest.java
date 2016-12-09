package test;

import org.junit.BeforeClass;
import org.junit.Test;

import com.haili.vsw.VSW;

public class VSWTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Test
	public void test() {
		VSW vsw = new VSW(4, 3);
		vsw.run();
	}

}
