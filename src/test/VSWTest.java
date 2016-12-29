package test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.BeforeClass;

import com.haili.scps.Ilist;
import com.haili.scps.IlistItem;
import com.haili.sw.SW;

public class VSWTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	public void test() {
		SW vsw = new SW(4, 3);
		vsw.run();
	}

	public static void main(String[] args) {
		Set result = new HashSet();
		Set set1 = new HashSet() {
			{
				add(1);
				add(3);
				add(5);
			}
		};

		Set set2 = new HashSet() {
			{
				add(1);
				add(2);
				add(3);
			}
		};

		result.clear();
		result.addAll(set1);
		result.retainAll(set2);
		System.out.println("交集：" + result);

		result.clear();
		result.addAll(set1);
		result.removeAll(set2);
		System.out.println("差集：" + result);
		
		result.clear();
		result.addAll(set2);
		result.removeAll(set1);
		System.out.println("差集：" + result);

		result.clear();
		result.addAll(set1);
		result.addAll(set2);
		System.out.println("并集：" + result);
		
	}
	
//	public static void main(String[] args) {
//		Map<String, IlistItem> ilist = new LinkedHashMap<>(); // i-list
//		ilist.put("c", new IlistItem("c", 4));
//		ilist.put("b", new IlistItem("b", 1));
//		ilist.put("d", new IlistItem("d", 3));
//		ilist.put("a", new IlistItem("a", 4));
//		Ilist l = new Ilist(ilist);
//		
//		l.sort();
//		List<String> tr = new ArrayList<>();
//		tr.add("d");
//		tr.add("c");
//		tr.add("a");
//		tr.add("b");
//		System.out.println(l.isSorted(tr));
//		l.sortTransaction(tr);
//		System.out.println(l.isSorted(tr));
//		
//		System.out.println(ilist.toString());
//		System.out.println(l.getIlist().toString());
//		System.out.println(tr.toString());
//		
//	}

}
