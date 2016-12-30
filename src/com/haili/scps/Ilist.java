package com.haili.scps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class Ilist {
	
	private Map<String, SCPSNode> ilistMap = new LinkedHashMap<>(); // i-list
	
	public Ilist() {
	}
	
	
	/**
	 * 事务根据i-list排序事务
	 * @param transaction
	 * @return
	 */
	public void sortTransaction(List<String> transaction) {
		
		Collections.sort(transaction, new Comparator<String>(){
			
			@Override
			public int compare(String o1, String o2) {
				if (ilistMap.get(o1) == null || ilistMap.get(o2) == null) {
					// 权值相同则按照字典序排序
					// 初始也按照字典序排序
					return o1.compareTo(o2);
				} else {
					// 优先按照支持数降序排序
					return ilistMap.get(o2).compareTo(ilistMap.get(o1));
				}
			}
		});
	}
	
	/**
	 * 找出虚拟节点（有序事务中最后一个大于最小支持数的项）
	 * @return
	 */
	public String getVirtualItem(List<String> record, int minSN) {
		if (record.size() <= 0) {
			return null;
		}
		String virtualItem = record.get(0);
		for (String str : record) {
			int v = this.getItem(str).count;
			if (v >= minSN) {
				virtualItem = str;
			} else {
				break;
			}
		}
		
		return virtualItem;
	}
	
	
	/**
	 * 插入事务后更新i-list顺序
	 * 每插入一个pane的数据后执行一次
	 */
	public void addPane(List<Map<String, Object>> pane) {
//		System.out.println("update i-list\nfrom : " + ilistMap.toString());
		for (Map<String, Object> transaction : pane) {
			List<String> record = (List<String>) transaction.get("record");
			
			for (String str : record) {
				if (ilistMap.get(str) == null) {
					ilistMap.put(str, new SCPSNode(str)); // 初始值为1
				} else {
					ilistMap.get(str).count += 1; // 支持数加一
				}
			}
		}
		
		// 频繁升序排序，方便for循环遍历
		sort();
//		System.out.println("to : " + ilistMap.toString());
	}
	
	/**
	 * 更新ilist计数
	 * @param item 项名
	 * @param n 要更新的计数
	 */
	public void updateItem(String item, int n) {
		getItem(item).count += n; // ilist 减去计数
		
		// 若计数为0则从ilist中删除该项
//		if (getItem(item).getC() == 0) {
//			removeItem(item);
//		}
	}
	
	/**
	 * 判断是否已经符合i-list顺序
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
	 * 根据
	 * 频繁升序排序
	 * 利于for循环
	 */
	public void sort() {
	    Map<String, SCPSNode> sortedMap = new TreeMap<>(new Comparator<String>() {  
	        public int compare(String key1, String key2) {  
	        	SCPSNode v1 = ilistMap.get(key1);
	        	SCPSNode v2 = ilistMap.get(key2);  
	        	
	            return v1.compareTo(v2);  
	        }});  
	    sortedMap.putAll(ilistMap);
	    
	    Map<String, SCPSNode> result = new LinkedHashMap<>();
	    for (String key : sortedMap.keySet()) {
	    	result.put(key, sortedMap.get(key));
		}
	    ilistMap = result;
	}
	
	/**
	 * 获取ilist
	 * @return
	 */
	public Map<String, SCPSNode> getIlistMap() {
		return ilistMap;
	}

	
	/**
	 * 获取头表的项
	 * @param key
	 * @return
	 */
	public SCPSNode getItem(String key) {
		return ilistMap.get(key);
	}
	
	
	/**
	 * 获取头表的项
	 * @param key
	 * @return
	 */
	public SCPSNode setItem(String key, SCPSNode node) {
		return ilistMap.put(key, node);
	}
	
	/**
	 * 添加树节点到ilist对应项的兄弟节点表中
	 * @param item
	 * @param node
	 */
	public void addItemBrother(String item, SCPSNode node) {
		SCPSNode head = getItem(item);
		// 第一个节点为空节点， 保存支持度计数
		if (head == null) {
			head = new SCPSNode(item, 0);
			ilistMap.put(item, head);
		} 
		
		// 在前面插入该节点
		node.next = head.next;
		head.next = node;
		
	}
	
	/**
	 * 删除同名兄弟节点
	 */
	public void removeItemBrother(String item, SCPSNode node) {
		SCPSNode last = getItem(item);
		if (last == null) {
			return;
		}
		SCPSNode cur = last.next;

		while (cur != null) {
			
			if (cur.equals(node)) {
				last.next = last.next.next;
				break;
			}
			last = last.next;
			cur = cur.next;
		}
	}
	
	/**
	 * 删除项
	 */
	public void removeItem(String item) {
		ilistMap.remove(item);
	}

}
