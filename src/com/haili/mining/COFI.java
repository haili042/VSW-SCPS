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

	private SCPSTree scpsTree; // ��fp��
	private Map<String, IlistItem> IlistMap; // ilist
	private int treeSize; // ���ݿ��С
	private double minSup; // ��С֧�ֶ�
	private int minSN; // ��С֧����
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
	 * �󽻼�
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
	 * �󲢼�
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
	 * ��
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
	 * ��ȫ��
	 * 
	 * @return
	 */
	public Set<ItemSet> getUniversalSet(ItemSet set, String item) {
		Set<ItemSet> result = new HashSet<>();

		String setStr = set.getSetStr();
		int len = setStr.length();
		
		int n = 1 << len;
		for (int i = 1; i < n; i++) { // �� 1 ѭ���� 2^len -1
			ItemSet s = new ItemSet(set.getC());
			for (int j = 0; j < len; j++) {
				int temp = i;
				if ((temp & (1 << j)) != 0) { // ��Ӧλ��Ϊ1���������Ӧ���ַ�
					s.addItem(setStr.charAt(j) + "");
				}
			}
			s.addItem(item);
			result.add(s);
		}
		return result;
	}

	/**
	 * COFI �㷨
	 * 
	 * @param scpsTree
	 */
	public void mine() {
		
		// ��ʱilist˳���Ǻ�Ƶ�����������෴�ģ�����֧�ֶ���С���ʼ�� ����for����
		for (String key : IlistMap.keySet()) {
			
			IlistItem ii = IlistMap.get(key);
			List<SCPSNode> nodeList = ii.getNextBrotherList();
			
			// ������ͬ���ֵܽڵ�
			// ֧�ֶȼ���������С֧�����Ž��б���
			if (ii.getC() >= minSN) {
				
				// ��������ģʽ��
				SubTree st = new SubTree(key);
				
				Set<ItemSet> setlist = new HashSet<>();
				for (SCPSNode node : nodeList) {
					
					// �����β�ڵ�
					if (node.isTailNode()) {
						
						ItemSet is = new ItemSet(node.getPTC() + node.getCTC()); // PTC + CTC = β�ڵ���ֵĴ���
						
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
