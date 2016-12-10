package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		l.add("b");
		l.add("d");
		
		Map<String, Integer> IList = new HashMap<>();
		IList.put("a", 3);
		IList.put("b", 4);
		IList.put("c", 3);
		IList.put("d", 4);
		
		
		System.out.println(isSorted(l, IList));
	}

	/**
	 * 判断事务是否有序
	 * @param transaction
	 * @param IList
	 * @return
	 */
	public static boolean isSorted(List<String> transaction, Map<String, Integer> IList) {
		for (int i = 0; i < transaction.size() - 1; i++) {
			String cur = transaction.get(i);
			String next = transaction.get(i + 1);
			
			if (IList.get(cur) < IList.get(next)) {
				return false;
			} else if (IList.get(cur) == IList.get(next) && cur.compareTo(next) > 0) {
				return false;
			}
		}
		return true;
	}
}
