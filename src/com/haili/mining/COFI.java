package com.haili.mining;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.haili.scps.Ilist;
import com.haili.scps.IlistItem;
import com.haili.scps.SCPSNode;
import com.haili.scps.SCPSTree;

public class COFI {

	private SCPSTree scpsTree; // 类fp树
	private Map<String, IlistItem> IlistMap; // ilist
	private int treeSize; // 数据库大小
	private double minSup; // 最小支持度
	private int minSN; // 最小支持数
	private ItemSet FP;

	public COFI() {

	}

	public COFI(SCPSTree scpsTree, double minSup) {
		this.scpsTree = scpsTree;
		this.IlistMap = scpsTree.getIlist().getIlistMap();
		this.minSup = minSup;
		treeSize = scpsTree.getCurrentWindowSize();
		this.minSN = (int) Math.ceil(this.minSup * this.treeSize);
	}


	/**
	 * 求交集
	 * 
	 * @return
	 */
	public Set<String> getIntersectionSet(Set<String> set1, Set<String> set2) {
		Set<String> result = new HashSet<>();
		result.addAll(set1);
		result.retainAll(set2);
		return result;
	}

	/**
	 * 求并集
	 * 
	 * @return
	 */
	public Set<String> getUnionSet(Set<String> set1, Set<String> set2) {
		Set<String> result = new HashSet<>();
		result.addAll(set1);
		result.addAll(set2);
		return result;
	}

	/**
	 * 求差集
	 * 
	 * @return
	 */
	public Set<String> getDifferenceSet(Set<String> set1, Set<String> set2) {
		Set<String> result = new HashSet<>();
		result.addAll(set1);
		result.removeAll(set2);
		return result;
	}

	/**
	 * 求全集
	 * 
	 * @return
	 */
	public Set<ItemSet> getUniversalSet(ItemSet set, String item) {
		Set<ItemSet> result = new HashSet<>();

		String setStr = set.getSetStr();
		int len = setStr.length();
		
		int n = 1 << len;
		for (int i = 1; i < n; i++) { // 从 1 循环到 2^len -1
			ItemSet s = new ItemSet(set.getC());
			for (int j = 0; j < len; j++) {
				int temp = i;
				if ((temp & (1 << j)) != 0) { // 对应位上为1，则输出对应的字符
					s.addItem(setStr.charAt(j) + "");
				}
			}
			s.addItem(item);
			result.add(s);
		}
		return result;
	}

	/**
	 * COFI 算法
	 * 
	 * @param scpsTree
	 */
	public void mine() {
		
		// 此时ilist顺序是和频繁降序排列相反的，即从支持度最小的项开始， 利于for遍历
		for (String key : IlistMap.keySet()) {
			
			IlistItem ii = IlistMap.get(key);
			List<SCPSNode> nodeList = ii.getNextBrotherList();
			
			// 遍历各同名兄弟节点
			// 支持度计数大于最小支持数才进行遍历
			if (ii.getC() >= minSN) {
				
				// 创建条件模式基
				SubTree st = new SubTree(key);
				
				Set<ItemSet> setlist = new HashSet<>();
				for (SCPSNode node : nodeList) {
					
					// 如果是尾节点
					if (node.isTailNode()) {
						
						ItemSet is = new ItemSet(node.getPTC() + node.getCTC()); // PTC + CTC = 尾节点出现的次数
						
						SCPSNode temp = node.getParent();
						while (!temp.getN().equals("root")) {
							is.addItem(temp.getN());
							temp = temp.getParent();
						}
						
						setlist.add(is);
						
						System.out.println(getUniversalSet(is, key).toString());
					}
					
				}
				st.addItemSet(setlist);
				System.out.println(setlist.toString());
			}
		}
	}
	
	public void add() {
		
	}

}
