package com.haili.cofi;

import java.util.HashSet;
import java.util.Set;

public class ItemSet {
	
	private Set<String> set = new HashSet<>();
	private int c;
	private String setStr = ""; // �ַ�����ʽ����
	
	public ItemSet() {}
	
	public ItemSet(int c) {
		this.c = c;
	}
	
	public static void main(String[] args) {

		COFI cofi = new COFI();

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
		
		Set<Set<String>> ssss = new HashSet<>();
		Set<String> ss1 = new HashSet<>();
		ss1.add("a");
		ss1.add("c");
		ss1.add("d");
		Set<String> ss2 = new HashSet<>();
		ss2.add("b");
		ss2.add("d");
		System.out.println(cofi.getIntersectionSet(ss1, ss2));

		System.out.println("������Intersection"
				+ cofi.getIntersectionSet(set1, set2));
		System.out
				.println("���difference " + cofi.getDifferenceSet(set1, set2));
		System.out.println("���" + cofi.getDifferenceSet(set2, set1));
		System.out.println("������union " + cofi.getUnionSet(set1, set2));
		
		Set<Set<String>> sss = new HashSet<>();
		Set<String> l1 = new HashSet<>();
		l1.add("a");
		l1.add("c");
		l1.add("d");
		Set<String> l2 = new HashSet<>();
		l2.add("a");
		l2.add("c");
		l2.add("b");
		l2.add("d");
		
		sss.add(l1);
		sss.add(l2);
		
		System.out.println(sss.toString());

	}

	
	/**
	 * �����
	 */
	public void addItem(String item) {
		set.add(item);
		setStr += item;
	}
	
	public String toString() {
		return String.format("%s(%d)", set.toString(), c);
	}
	
	public String getSetStr() {
		return setStr;
	}

	public Set<String> getSet() {
		return set;
	}

	public int getC() {
		return c;
	}
	
			
}
