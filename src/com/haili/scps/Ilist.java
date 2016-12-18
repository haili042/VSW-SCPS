package com.haili.scps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Ilist {
	
	private Map<String, IlistItem> ilist = new LinkedHashMap<>(); // i-list
	
	public Ilist() {
		
	}
	
	public Ilist(Map<String, IlistItem> ilist) {
		this.ilist = ilist;
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
				if (ilist.get(o1) == null || ilist.get(o2) == null) {
					// Ȩֵ��ͬ�����ֵ�������
					// ��ʼҲ�����ֵ�������
					return o1.compareTo(o2);
				} else {
					// ���Ȱ���֧������������
					return ilist.get(o2).compareTo(ilist.get(o1));
				}
			}
		});
	}
	
	
	/**
	 * ������������i-list˳��
	 * ÿ����һ��pane�����ݺ�ִ��һ��
	 */
	public void updateIList(List<Map<String, Object>> pane) {
		System.out.print("update i-list from " + ilist.toString());
		for (Map<String, Object> transaction : pane) {
			int tid = (int) transaction.get("tid");
			List<String> record = (List<String>) transaction.get("record");
			
			for (String str : record) {
				if (ilist.get(str) == null) {
					ilist.put(str, new IlistItem(str)); // ��ʼֵΪ1
				} else {
					ilist.get(str).updateC(1); // ֧������һ
//					IList.put(str, IList.get(str) + 1);
				}
			}
		}
		
		// Ƶ���������򣬷���forѭ������
		sort();
		System.out.println(" to : " + ilist.toString());
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
			if (ilist.get(cur).compareTo(ilist.get(next)) < 0) {
				return false;
			}
		}
		return true;
	}
	
	
	// Ƶ����������
	public void sort() {
	    Map<String, IlistItem> sortedMap = new TreeMap<>(new Comparator<String>() {  
	        public int compare(String key1, String key2) {  
	        	IlistItem v1 = ilist.get(key1), v2 = ilist.get(key2);  
	        	
	            return v1.compareTo(v2);  
	        }});  
	    sortedMap.putAll(ilist);
	    
	    Map<String, IlistItem> result = new LinkedHashMap<>();
	    for (String key : sortedMap.keySet()) {
	    	result.put(key, sortedMap.get(key));
		}
	    ilist = result;
	}
	
	// ��ȡilist
	public Map<String, IlistItem> getIlist() {
		return ilist;
	}

	// ��ȡ��
	public IlistItem getItem(String key) {
		return ilist.get(key);
	}

}
