package com.haili.scps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Ilist {
	
	private Map<String, IlistItem> ilistMap = new LinkedHashMap<>(); // i-list
	
	public Ilist() {
	}
	
	
	/**
	 * �������i-list��������
	 * @param transaction
	 * @return
	 */
	public void sortTransaction(List<String> transaction) {
		
		Collections.sort(transaction, new Comparator<String>(){
			
			@Override
			public int compare(String o1, String o2) {
				if (ilistMap.get(o1) == null || ilistMap.get(o2) == null) {
					// Ȩֵ��ͬ�����ֵ�������
					// ��ʼҲ�����ֵ�������
					return o1.compareTo(o2);
				} else {
					// ���Ȱ���֧������������
					return ilistMap.get(o2).compareTo(ilistMap.get(o1));
				}
			}
		});
	}
	
	
	/**
	 * ������������i-list˳��
	 * ÿ����һ��pane�����ݺ�ִ��һ��
	 */
	public void addPane(List<Map<String, Object>> pane) {
		System.out.println("update i-list\nfrom : " + ilistMap.toString());
		for (Map<String, Object> transaction : pane) {
			int tid = (int) transaction.get("tid");
			List<String> record = (List<String>) transaction.get("record");
			
			for (String str : record) {
				if (ilistMap.get(str) == null) {
					ilistMap.put(str, new IlistItem(str)); // ��ʼֵΪ1
				} else {
					ilistMap.get(str).updateC(1); // ֧������һ
//					IList.put(str, IList.get(str) + 1);
				}
			}
		}
		
		// Ƶ���������򣬷���forѭ������
		sort();
		System.out.println("to : " + ilistMap.toString());
	}
	
	/**
	 * ����ilist����
	 * @param item ����
	 * @param n Ҫ���µļ���
	 */
	public void updateItem(String item, int n) {
		getItem(item).updateC(n); // ilist ��ȥ����
		
		// ������Ϊ0���ilist��ɾ������
		if (getItem(item).getC() == 0) {
			removeItem(item);
		}
	}
	
	/**
	 * �ж��Ƿ��Ѿ�����i-list˳��
	 * @param transaction
	 * @return
	 */
	public boolean isSorted(List<String> transaction) {
		for (int i = 0; i < transaction.size() - 1; i++) {
			String cur = transaction.get(i);
			String next = transaction.get(i + 1);
			if (ilistMap.get(cur).compareTo(ilistMap.get(next)) < 0) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * ����
	 * Ƶ����������
	 * ����forѭ��
	 */
	public void sort() {
	    Map<String, IlistItem> sortedMap = new TreeMap<>(new Comparator<String>() {  
	        public int compare(String key1, String key2) {  
	        	IlistItem v1 = ilistMap.get(key1), v2 = ilistMap.get(key2);  
	        	
	            return v1.compareTo(v2);  
	        }});  
	    sortedMap.putAll(ilistMap);
	    
	    Map<String, IlistItem> result = new LinkedHashMap<>();
	    for (String key : sortedMap.keySet()) {
	    	result.put(key, sortedMap.get(key));
		}
	    ilistMap = result;
	}
	
	/**
	 * ��ȡilist
	 * @return
	 */
	public Map<String, IlistItem> getIlistMap() {
		return ilistMap;
	}

	
	/**
	 * ��ȡͷ�����
	 * @param key
	 * @return
	 */
	public IlistItem getItem(String key) {
		return ilistMap.get(key);
	}
	
	/**
	 * ������ڵ㵽ilist��Ӧ����ֵܽڵ����
	 * @param item
	 * @param node
	 */
	public void addItemBrother(String item, SCPSNode node) {
		if (getItem(item) == null) {
			ilistMap.put(item, new IlistItem(item));
		}
		getItem(item).getNextBrotherList().add(node);
	}
	
	/**
	 * ɾ��ͬ���ֵܽڵ�
	 */
	public void removeItemBrother(String item, SCPSNode node) {
		getItem(item).getNextBrotherList().remove(node);
	}
	
	/**
	 * ɾ����
	 */
	public void removeItem(String item) {
		ilistMap.remove(item);
	}

}
