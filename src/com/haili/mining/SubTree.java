package com.haili.mining;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SubTree {
	private String item; // ����ģʽ
	private List<Set<ItemSet>> itemSets = new ArrayList<>();
	
	public SubTree(String item) {
		this.item = item;
	}
	
	public void addItemSet(Set<ItemSet> itemSet) {
		itemSets.add(itemSet);
	}
	
	
}
