package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.haili.cofi.ItemSet;

public class ItemSetTest {

	public ItemSetTest() {}
	
	public static void main(String[] args) {
		Map mmm = new HashMap<>();
		ItemSet s1 = new ItemSet();
		s1.addItem("a");
		s1.addItem("c");
		s1.addItem("d");

		ItemSet s2 = new ItemSet();
		s2.addItem("a");
		s2.addItem("c");
		s2.addItem("d");

		mmm.put(s1, 1);
		mmm.put(s2, 1);
		System.out.println(mmm.toString());
		
		
		Map mmm2 = new HashMap<>();
		Set<String> s11 = new HashSet<>();
		s11.add("a");
		s11.add("c");
		s11.add("d");

		Set<String> s22 = new HashSet<>();
		s22.add("c");
		s22.add("d");

		Set<String> s33 = new HashSet<>();
		mmm2.put(s11, 1);
		mmm2.put(s22, 1);
		System.out.println(mmm2.toString());

		ItemSetTest ist = new ItemSetTest();
		
		System.out.println("=" + ist.getUniversalSet(s11));
	}
	
	public List<Set<String>> getUniversalSet(Set<String> itemset) {
		List<Set<String>> results = new ArrayList<>();
		
		// 利用二进制递增生成全排列
		int len = itemset.size();
		int n = 1 << len;
		for (int i = 1; i < n; i++) { // 从 1 循环到 2^len -1
			Set<String> s = new HashSet<>();
			
			int j = 0;
			for (String item : itemset) {
				int temp = i;
				if ((temp & (1 << j++)) != 0) { // 对应位上为1，则输出对应的字符
					s.add(item);
				}
			}
			results.add(s);
		}
	
		return results;
	}

}
