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
	public static void main(String[] args) {
		List<String> l = new ArrayList<>();
		l.add(0, "3");
		l.add(0, "2");
		l.add(0, "1");
		System.out.println(l.toString());
	}

}
