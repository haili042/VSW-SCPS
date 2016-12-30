package com.haili.scps;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.text.html.MinimalHTMLWriter;

public class SCPSTree {

//	private Map<String, IlistItem> IList = new LinkedHashMap<>(); // i-list
	
	private Ilist ilist = new Ilist(); // i-list
	
//	private List<SCPSNode> tailNodeList = new ArrayList<>(); // 记录尾节点， 减少遍历树次数
	private SCPSNode root = new SCPSNode("root");
	private int currentWindowSize = 0; // 当前窗口大小
	private double minSup; // 最小支持度

	
	public SCPSTree(double minSup) {
		this.minSup = minSup;
	}
	
	
	/**
	 * 插入一批数据到树中
	 * @param pane
	 */
	public void insertPane(List<Map<String, Object>> pane, int checkPoint) {
		
		// 1 插入一批数据
		for (Map<String, Object> transaction : pane) {
			currentWindowSize++;
			insertPath(transaction, checkPoint); // 排序好的事务插入到SCPS树中
		}
		// 2 更新i-list
		// 频繁升序排序，方便for循环遍历
		ilist.addPane(pane);
		
		reconstruct(); // 根据最新I-list重构SCPS-tree
	}
	
	
	/***
	 * 在树重构阶段的插入， 与和tid，检查点无关
	 * @param record
	 */
	public void insertPath(List<String> record, int lastC, int lastPTC, int lastCTC, String virtualItem) {

		SCPSNode temp = root;
		
		for (int i = 0; i < record.size(); i++) {
			String item = record.get(i);
			SCPSNode child = temp.getChild(item);
			
			boolean isVirtual = item.equals(virtualItem); // 大于支持度的最后一个点
			
			if (i == record.size() - 1) {
				// 添加尾节点
				if (child == null) {
					SCPSNode node = new SCPSNode(item, lastC);
					node.isTailNode = true;
					node.preCount = lastPTC;
					node.curCount = lastCTC;
					node.isVirtual = isVirtual;
					temp.addChild(node);
					temp = node;
					ilist.addItemBrother(node.item, node);

				} else {
					child.updateChild(lastC); // 更新已有节点计数
					child.isTailNode = true;
					child.isVirtual = isVirtual;

					child.preCount = (lastPTC);
					child.curCount = (lastCTC);
					temp = child;
				}
				
			} else {
				// 普通节点插入到树中
				if (child == null) {
					SCPSNode node = new SCPSNode(item, lastC);
					node.isVirtual = isVirtual;
					temp.addChild(node);
					temp = node;
					ilist.addItemBrother(node.item, node);

				} else {
					child.updateChild(lastC); // 更新已有节点计数
					child.isVirtual = isVirtual;
					temp = child;
				}
			}
				
		}

	}
	
	/**
	 * 插入有序的事务到SCPS树中
	 * @param record 有序的事务
	 */
	public void insertPath(Map<String, Object> transaction, int checkPoint) {

		SCPSNode temp = root;
		int tid = (int) transaction.get("tid");
		List<String> record = (List<String>) transaction.get("record");
		
		ilist.sortTransaction(record); // 根据i-list排序
//		 System.out.println("insert tid " + tid + " : " + record.toString());
		
		for (int i = 0; i < record.size(); i++) {
			String item = record.get(i);
			SCPSNode child = temp.getChild(item);
			
			// 插入到树中
			if (i == record.size() - 1) {
				// 添加尾节点
				if (child == null) {
					SCPSNode node = new SCPSNode(item);
					
					temp.addChild(node, tid, checkPoint);
					temp = node;
					ilist.addItemBrother(node.item, node);
					
				} else {
					child.updateChild(1, tid, checkPoint); // 更新已有节点计数
					temp = child;
				}
				
			} else {
				// 普通节点插入到树中
				if (child == null) {
					SCPSNode node = new SCPSNode(item);
					
					temp.addChild(node);
					temp = node;
					ilist.addItemBrother(node.item, node);

				} else {
					child.updateChild(1); // 更新已有节点计数
					temp = child;
				}
			}
				
		}

	}
	
	/**
	 * 从树中删除路径, 从叶子节点开始
	 * @param leaf
	 * @param leafCount
	 * @param needUpdateIlistCount 是否是删除检查点之前数据时的删除操作
	 */
	public void removePath(SCPSNode leaf, int leafCount, boolean needUpdateIlistCount) {
		SCPSNode temp = leaf;
		while (!temp.item.equals("root")) {
			temp.count = temp.count - leafCount;
			temp.isVirtual = false; // 重置虚拟节点
			
			// 更新 i-list 计数
			if (needUpdateIlistCount) {
				// 更新PTC和CTC的值
				temp.preCount = (temp.curCount);
				temp.curCount = 0;
				
				ilist.updateItem(temp.item, -leafCount); // ilist 减去计数
//				IList.put(temp.getN(), IList.get(temp.getN()) - leafCount);
			}
			
			// 若计数为0， 则删除该节点
			if (temp.count == 0) {
				ilist.removeItemBrother(temp.item, temp); // 删除ilist兄弟节点
				temp.remove();
			}
			temp = temp.parent;
		}

		// 更新窗口大小
		if (needUpdateIlistCount) {
			this.currentWindowSize -= leafCount;
		}
	}
	
	
	/**
	 * 根据BSM策重构整树的结构
	 * 并为虚拟节点打上标记， 因为fp树在挖掘的时候是要删除支持数<minSup的节点的
	 * SCPStree中保存了这些节点， 并为最后的节点打上虚拟节点标记， 
	 * 虚拟节点到以下所有的子树都是虚拟的
	 */
	public void reconstruct() {
//		System.out.println("reconstructing...");
//		 print(root);
	  	List<SCPSNode> tailNodes = new ArrayList<>();
        travelDFS(tailNodes, root, "tailNodes");
        
        for (int i = tailNodes.size() - 1; i >= 0; i--) {
			
        	SCPSNode tailNode = tailNodes.get(i);
        	int lastC = tailNode.count;
        	int lastPTC = tailNode.preCount;
        	int lastCTC = tailNode.curCount;
        	
			// 获取指定格式
			SCPSNode temp = tailNode;
			List<String> record = new ArrayList<>();
			while (!temp.item.equals("root")) {
				record.add(0, temp.item);
				// 清空原来的虚拟标记
				temp.isVirtual = false;
				temp = temp.parent;
			}
			
			if (!ilist.isSorted(record)) {
				// 如果路径无序
				// 从树中删除一条路径
				removePath(tailNode, tailNode.count, false);
//				print(root);
				
				// record 根据i-list排序
				ilist.sortTransaction(record); 
				// 支持数
				int minSN = (int) Math.ceil(this.minSup * this.currentWindowSize);
				// 找到虚拟项
				String virtualItem = ilist.getVirtualItem(record, minSN);
				
//				System.out.println(record.toString());
//				System.out.println("virtual item : " + virtualItem);
				
				// 排序后重新插入到树中， 此时与检查点无关
				insertPath(record, lastC, lastPTC, lastCTC, virtualItem);
//				print(root);
			} else {
				// 如果有序就不用删除了
				ilist.sortTransaction(record); 
				// 支持数
				int minSN = (int) Math.ceil(this.minSup * this.currentWindowSize);
				// 找到虚拟项
				String virtualItem = ilist.getVirtualItem(record, minSN);
				
				// 更新虚拟节点
				SCPSNode temp2 = tailNode;
				while (temp2 != null && !temp2.item.equals("root")) {
					if (virtualItem != null && temp2.item.equals(virtualItem)) {
						tailNode.isVirtual = true;
						break;
					}
					temp2 = temp.parent;
				}
			}
			
		}
//		System.out.println("after reconstruction...");
//		print(root);
		System.out.println("current window size : " + getCurrentWindowSize() + "\n");

	}
	
	/**
	 * 删除过期数据
	 */
	public void removeStaleWindow(SCPSNode root) {
//		System.out.println("removing old window .... ");
	  	List<SCPSNode> tailNodes = new ArrayList<>();
        travelDFS(tailNodes, root, "tailNodes");
        
        for (SCPSNode leaf : tailNodes) {
			// 从树中删除一条路径, 权值为该路径叶子节点的PTC值
        	int ptc = leaf.preCount;
			removePath(leaf, ptc, true);
		}
		
        reconstruct();

	}
	
	/**
	 * 打印树
	 * 广度优先
	 */
	public void print(SCPSNode root) {
		
		LinkedList<SCPSNode> queue = new LinkedList<>();
		queue.add(root);
		int nextLevelSize = 0; // 下一层节点个数， 打印输出用
		int currentLevelSize = 1; // 本层节点个数， 打印输出用
		
		SCPSNode lastNode = null; // 同层上一个访问节点， 打印输出用
		List<List<SCPSNode>> resultList = new ArrayList<>();
		List<SCPSNode> row = new ArrayList<>();
		
		while (!queue.isEmpty()) {
			SCPSNode temp = queue.poll();
			LinkedList<SCPSNode> children = temp.children;
			
			for (int i = 0; i < getPathSize(lastNode) - 1; i++) {
				row.add(null); // 占位
			}
			
			if (resultList.size() > 0) {
				List<SCPSNode> lastRow = resultList.get(resultList.size() - 1); // 上一层
				int pos = 0;
				for (int i = 0; i < lastRow.size(); i++) {
					// 对应上父节点
					if (temp.parent.equals(lastRow.get(i))) {
						pos = i;
					}
				}
				// 对应上父节点
				if ((pos - row.size()) > 0) {
					for (int i = 0; i <= pos - row.size(); i++) {
						row.add(null); // 占位
					}
				}
			}
			
			row.add(temp);
			
			nextLevelSize += temp.children.size();
			lastNode = temp;
			
			for (SCPSNode n : children) {
				queue.add(n);
			}
			
			if (--currentLevelSize == 0) {
				currentLevelSize = nextLevelSize;
				nextLevelSize = 0;
				lastNode = null;
				List<SCPSNode> emptyRow = new ArrayList<>(); // 空行来画线
				for (SCPSNode n : row) {
					if (n != null) {
						emptyRow.add(new SCPSNode("|"));
					} else {
						emptyRow.add(n);
					}
				}
				resultList.add(emptyRow);
				
				resultList.add(row);
				row = new ArrayList<>();
			}
		}
		
		
		for (int i = 1; i < resultList.size() - 1; i += 2) {
			List<SCPSNode> line = resultList.get(i);
			List<SCPSNode> nextLine = resultList.get(i + 1);
			// 横线
			for (int j = 0; j < nextLine.size(); j++) {
				SCPSNode n = nextLine.get(j);
				if (line.size() < j + 1) {
					line.add(null);
				}
				if (n != null && n.item.equals("|") && line.get(j) == null) {
					// 如果是横线且没有对应的上面的元素和其连接，则设置为竖线
					line.set(j, new SCPSNode("^"));
					
					// 处理线相交
					for (int k = j - 1; k >= 0; k--) {
						if (line.get(k) == null) {
							line.set(k, new SCPSNode("-"));
						} else if (line.get(k).item.equals("^")) {
							line.set(k, new SCPSNode("+"));
						} else {
							break;
						}
					}
				}
				
			}
			
			nextLine = resultList.get(i+1);
		}
		
		for (int i = 1; i < resultList.size(); i++) {
			List<SCPSNode> line = resultList.get(i);
			
			// 打印
			for (int j = 0; j < line.size(); j++) {
				SCPSNode sn = line.get(j);
				if (sn != null) {
					String s = sn.item;
					
					if (s.equals("|")) {
						System.out.print("            │        ");
					} else if (s.equals("-")) {
						System.out.print("─────────────────────");
					} else if (s.equals("+")) {
						System.out.print("────────────┬────────");
					} else if (s.equals("^")) {
						System.out.print("────────────┐        ");
					} else {
						System.out.print("    " + sn.toString());
					}
				} else {
					System.out.print("                     ");
				}
			}
			System.out.println();
		}
		System.out.println("\n");
	}
	
	/***
	 * 求树有几条路径
	 * 
	 */
    public int getPathSize(SCPSNode root) {
    	List<SCPSNode> leaves = new ArrayList<>();
        travelDFS(leaves, root, "leaves");
        return leaves.size();
    }
	
	/***
	 * 深度优先遍历,
	 * 获取叶子或者尾节点集合
	 * 
	 */
	public void travelDFS(List<SCPSNode> result, SCPSNode node, String type) {
		if (node == null) {
			return;
		}
			
		if (type.equals("leaves") && node.children.size() == 0) {
			// 叶子节点
			result.add(node);
		} else if (type.equals("tailNodes") && node.isTailNode) {
			// 尾节点
			result.add(node);
		}
		
		
		for (SCPSNode child : node.children) {
			travelDFS(result, child, type);
		}
		
	}
	

	/******************** getters and setters *******************/
	public Ilist getIlist() {
		return ilist;
	}

	public SCPSNode getRoot() {
		return root;
	}

	public void setRoot(SCPSNode root) {
		this.root = root;
	}

	public int getCurrentWindowSize() {
		return currentWindowSize;
	}

	public void setCurrentWindowSize(int currentWindowSize) {
		this.currentWindowSize = currentWindowSize;
	}

	
}
