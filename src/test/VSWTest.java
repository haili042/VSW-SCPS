package test;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;

import com.haili.vsw.VSW;

public class VSWTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	public void test() {
		VSW vsw = new VSW(4, 3);
		vsw.run();
	}

}
