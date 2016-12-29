package com.haili.cofi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.haili.mine.MiningFP;
import com.haili.scps.IlistItem;
import com.haili.scps.SCPSNode;
import com.haili.scps.SCPSTree;
import com.haili.sw.SW;

public class COFI implements MiningFP {

	private SCPSTree scpsTree; // 类fp树
	private Map<String, IlistItem> IlistMap; // ilist
	private int treeSize; // 数据库大小
	private double minSup; // 最小支持度
	private int minSN; // 最小支持数
	private Map<Set<String>, Integer> FP = new LinkedHashMap<>(); // 频繁项集， 键是项集， 值是计数
	

	/**
	 * 构造函数
	 * 
	 * @param scpsTree
	 * @param minSup
	 */
	public COFI(SCPSTree scpsTree, double minSup) {
		this.scpsTree = scpsTree;
		this.IlistMap = scpsTree.getIlist().getIlistMap();
		this.minSup = minSup;
		treeSize = scpsTree.getCurrentWindowSize();
		this.minSN = (int) Math.ceil(this.minSup * this.treeSize);
	}


	/**
	 * 将集合的全排列添加到cs集合中
	 * @param set 要求全排列的集合
	 * @param item 该条件模式的项名
	 * @param count 项集的支持数
	 * @param cs 要添加进去的集合
	 * @return
	 */
	public void addUniversalSet2(Set<String> set, String item, int count, Map<Set<String>, Integer> cs) {
		
		// 先转成字符串
		String setStr = "";
		for (String s : set) {
			setStr += s;
		}
		
		// 利用二进制递增生成全排列
		int len = setStr.length();
		int n = 1 << len;
		for (int i = 0; i < n; i++) { // 从 1 循环到 2^len -1
			Set<String> s = new HashSet<>();
			
			for (int j = 0; j < len; j++) {
				int temp = i + 1;
				if ((temp & (1 << j)) != 0) { // 对应位上为1，则输出对应的字符
					s.add(setStr.charAt(j) + "");
				}
			}
			if (s.size() == 0) {
				continue;
			}
			s.add(item); // 把条件模式也加到项集里
//			System.out.print(s.toString() + " ");
			
			if (cs.get(s) != null) {
				cs.put(s, cs.get(s) + count);
			} else {
				cs.put(s, count);
			}
		}
//		System.out.println();
	}
	
	public void addUniversalSet(Set<String> set, String item, int count, Map<Set<String>, Integer> cs) {
		List<Set<String>> usl = getUniversalSet(set);
		for (Set<String> s : usl) {
			s.add(item);
			
			if (cs.get(s) != null) {
				cs.put(s, cs.get(s) + count);
			} else {
				cs.put(s, count);
			}
		}
//		System.out.println(usl.toString());

	}

	/**
	 * 求全排列
	 * @param itemset
	 * @return
	 */
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
	
	/**
	 * COFI 算法
	 * 
	 * @param scpsTree
	 */
	public void mine() {
//		System.out.println("min_sn : " + minSN);
		
		// 此时ilist顺序是和频繁降序排列相反的，即从支持度最小的项开始， 利于for遍历
		for (String key : IlistMap.keySet()) {
			
			IlistItem ii = IlistMap.get(key);
			
			// 遍历各同名兄弟节点
			// 支持度计数大于最小支持数才进行遍历
			if (ii.getC() >= minSN) {
//				Set<String> hs = new HashSet<>();
//				hs.add(ii.getN());
//				FP.put(hs, ii.getC());
				
				List<SCPSNode> nodeList = ii.getNextBrotherList();
				Map<Set<String>, Integer> cs = new HashMap<>();
				
				// 创建条件模式基
				for (SCPSNode node : nodeList) {
					
					// 如果是尾节点
					if (node.isVirtual()) {
						
						Set<String> is = new HashSet<>();
						
						// 遍历到根节点的路径
						SCPSNode temp = node.getParent();
						while (!temp.getItem().equals("root")) {
							is.add(temp.getItem());
							temp = temp.getParent();
						}
						
						// 对该路径的点进行求全排列操作, 并保存到cs集合中
//						addUniversalSet(is, key, node.getPTC() + node.getCTC(), cs);
						addUniversalSet(is, key, node.count, cs);
						
					}
				}
//				System.out.println("cs : " + cs.toString());
				
				// 支持数大于最小支持数的则添加到FP中
				for (Set<String> k : cs.keySet()) {
					int v = cs.get(k);
					if (v >= minSN) {
						FP.put(k, v);
					}
				}
				
			} // end if
		} // end for
//		System.out.println("FP size : " + FP.size());
//		System.out.println("FP : " + FP.toString());
		
		//setSum(FP);
	}
	
	public int setSum(Map<Set<String>, Integer> fp) {
		int result = 0;
		
		List<Set<String>> list = new ArrayList<>(fp.keySet());
		
		for (int i = 0; i < list.size(); i++) {
			
		}
		
		for (int i = 0; i < list.size(); i++) {
			
			Set<String> cur = list.get(i);
			for (int j = i + 1; j < list.size(); j++) {
				
				Set<String> next = list.get(j);
				Set<String> interSet = SW.getIntersectionSet(cur, next);
				
				if (!cur.containsAll(next) && !next.containsAll(cur)) {
					int res = ((1 << next.size()) - 1) + ((1 << next.size()) - 1) - ((1 << interSet.size()) - 1);
					System.out.println(String.format("%s ^ %s = %s           %d + %d - %d = %d" 
							, cur.toString(), next.toString(), interSet.toString()
							, ((1 << cur.size()) - 1), ((1 << next.size()) - 1), ((1 << interSet.size()) - 1), res));
					result += res;
				} else {
				}
			}
		}
		System.out.println(result);
		return result;
	}

	// -------------------- getters and setters  -------------------------------------
	public Map<Set<String>, Integer> getFP() {
		return FP;
	}
	
}
