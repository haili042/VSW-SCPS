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

	private SCPSTree scpsTree; // ��fp��
	private Map<String, IlistItem> IlistMap; // ilist
	private int treeSize; // ���ݿ��С
	private double minSup; // ��С֧�ֶ�
	private int minSN; // ��С֧����
	private Map<Set<String>, Integer> FP = new LinkedHashMap<>(); // Ƶ����� ������� ֵ�Ǽ���
	

	/**
	 * ���캯��
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
	 * �����ϵ�ȫ������ӵ�cs������
	 * @param set Ҫ��ȫ���еļ���
	 * @param item ������ģʽ������
	 * @param count ���֧����
	 * @param cs Ҫ��ӽ�ȥ�ļ���
	 * @return
	 */
	public void addUniversalSet2(Set<String> set, String item, int count, Map<Set<String>, Integer> cs) {
		
		// ��ת���ַ���
		String setStr = "";
		for (String s : set) {
			setStr += s;
		}
		
		// ���ö����Ƶ�������ȫ����
		int len = setStr.length();
		int n = 1 << len;
		for (int i = 0; i < n; i++) { // �� 1 ѭ���� 2^len -1
			Set<String> s = new HashSet<>();
			
			for (int j = 0; j < len; j++) {
				int temp = i + 1;
				if ((temp & (1 << j)) != 0) { // ��Ӧλ��Ϊ1���������Ӧ���ַ�
					s.add(setStr.charAt(j) + "");
				}
			}
			if (s.size() == 0) {
				continue;
			}
			s.add(item); // ������ģʽҲ�ӵ����
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
	 * ��ȫ����
	 * @param itemset
	 * @return
	 */
	public List<Set<String>> getUniversalSet(Set<String> itemset) {
		List<Set<String>> results = new ArrayList<>();
		
		// ���ö����Ƶ�������ȫ����
		int len = itemset.size();
		int n = 1 << len;
		for (int i = 1; i < n; i++) { // �� 1 ѭ���� 2^len -1
			Set<String> s = new HashSet<>();
			
			int j = 0;
			for (String item : itemset) {
				int temp = i;
				if ((temp & (1 << j++)) != 0) { // ��Ӧλ��Ϊ1���������Ӧ���ַ�
					s.add(item);
				}
			}
			results.add(s);
		}
		return results;
	}
	
	/**
	 * COFI �㷨
	 * 
	 * @param scpsTree
	 */
	public void mine() {
//		System.out.println("min_sn : " + minSN);
		
		// ��ʱilist˳���Ǻ�Ƶ�����������෴�ģ�����֧�ֶ���С���ʼ�� ����for����
		for (String key : IlistMap.keySet()) {
			
			IlistItem ii = IlistMap.get(key);
			
			// ������ͬ���ֵܽڵ�
			// ֧�ֶȼ���������С֧�����Ž��б���
			if (ii.getC() >= minSN) {
//				Set<String> hs = new HashSet<>();
//				hs.add(ii.getN());
//				FP.put(hs, ii.getC());
				
				List<SCPSNode> nodeList = ii.getNextBrotherList();
				Map<Set<String>, Integer> cs = new HashMap<>();
				
				// ��������ģʽ��
				for (SCPSNode node : nodeList) {
					
					// �����β�ڵ�
					if (node.isVirtual()) {
						
						Set<String> is = new HashSet<>();
						
						// ���������ڵ��·��
						SCPSNode temp = node.getParent();
						while (!temp.getItem().equals("root")) {
							is.add(temp.getItem());
							temp = temp.getParent();
						}
						
						// �Ը�·���ĵ������ȫ���в���, �����浽cs������
//						addUniversalSet(is, key, node.getPTC() + node.getCTC(), cs);
						addUniversalSet(is, key, node.count, cs);
						
					}
				}
//				System.out.println("cs : " + cs.toString());
				
				// ֧����������С֧����������ӵ�FP��
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
