package com.haili.scps;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SCPSTree {

	private Map<String, Integer> IList = new HashMap<>(); // i-list
	private List<SCPSNode> tailNodeList = new ArrayList<>(); // 记录尾节点， 减少遍历树次数
	private SCPSNode root = new SCPSNode();
	private int currentWindowSize = 0; // 当前窗口大小
	private double minSup; // 最小支持度
    

	public SCPSTree() {}
	
	
	/**
	 * 插入一批数据到树中
	 * @param pane
	 */
	public void insertPane(List<Map<String, Object>> pane, int checkPoint) {
		
		// 1 插入一批数据
		for (Map<String, Object> transaction : pane) {

			this.currentWindowSize++;
			insertPath(transaction, checkPoint); // 排序好的事务插入到SCPS树中
		}
//		updateIList(pane); // 2 更新i-list
		reconstruct(); // 根据最新I-list重构SCPS-tree
	}
	
	/**
	 * 插入事务后更新i-list顺序
	 * 每插入一个pane的数据后执行一次
	 */
	public void updateIList(List<Map<String, Object>> pane) {
		System.out.print("update i-list from " + IList.toString());
		for (Map<String, Object> transaction : pane) {
			int tid = (int) transaction.get("tid");
			List<String> record = (List<String>) transaction.get("record");
			
			for (String str : record) {
				if (IList.get(str) == null) {
					IList.put(str, 1); // 初始值为1
				} else {
					IList.put(str, IList.get(str) + 1);
				}
			}
		}
		System.out.println(" to : " + IList.toString());
	}
	
	
	/**
	 * 插入有序的事务到SCPS树中
	 * @param record 有序的事务
	 */
	public void insertPath(Map<String, Object> transaction, int checkPoint) {

		SCPSNode temp = root;
		int tid = (int) transaction.get("tid");
		List<String> record = (List<String>) transaction.get("record");
		
		sortTransaction(record); // 根据i-list排序
		System.out.println("insert tid " + tid + " : " + record.toString());
		
		for (int i = 0; i < record.size(); i++) {
			String item = record.get(i);
			SCPSNode child = temp.getChild(item);
			
			if (i == record.size() - 1) {
				// 添加尾节点
				if (child == null) {
					SCPSNode node = new SCPSNode(item);
					
					temp.addChild(node, tid, checkPoint);
					temp = node;
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
				} else {
					child.updateChild(1); // 更新已有节点计数
					temp = child;
				}
			}
				
		}

	}
	
	/**
	 * 事务根据i-list排序
	 * @param transaction
	 * @return
	 */
	public void sortTransaction(List<String> transaction) {
		
		Collections.sort(transaction, new Comparator<String>(){
			
			@Override
			public int compare(String o1, String o2) {
				
				if (IList.get(o1) == null || IList.get(o2) == null || IList.get(o1) == IList.get(o2)) {
					// 权值相同则按照字典序排序
					// 初始也按照字典序排序
					return o1.compareTo(o2);
				} else {
					// 优先按照支持数降序排序
					return IList.get(o2) - IList.get(o1);
				}
			}
		});
	}
	
	
	/**
	 * 从树中删除路径
	 */
	public void removePath(SCPSNode path) {
		
	}
	
	/**
	 * 对路径进行排序
	 * 
	 */
	public void sortPath() {
		
	}
	
	/**
	 * 根据BSM策重构整树的结构
	 */
	public void reconstruct() {
		printBFS(root);
		System.out.println("reconstructing...");
	}
	
	/**
	 * 删除检查点之前的数据
	 */
	public void removeStale() {
		
	}
	
	/**
	 * 打印树
	 * 广度优先
	 */
	public void printBFS(SCPSNode root) {
		
		LinkedList<SCPSNode> queue = new LinkedList<>();
		queue.add(root);
		int nextLevelSize = 0; // 下一层节点个数， 打印输出用
		int currentLevelSize = 1; // 本层节点个数， 打印输出用
		
		SCPSNode lastNode = null; // 同层上一个访问节点， 打印输出用
		List<List<SCPSNode>> resultList = new ArrayList<>();
		List<SCPSNode> row = new ArrayList<>();
		
		while (!queue.isEmpty()) {
			SCPSNode temp = queue.poll();
			LinkedList<SCPSNode> children = temp.getChildren();
			
			for (int i = 0; i < getPathSize(lastNode) - 1; i++) {
				row.add(null); // 占位
			}
			
			if (resultList.size() > 0) {
				List<SCPSNode> lastRow = resultList.get(resultList.size() - 1); // 上一层
				int pos = 0;
				for (int i = 0; i < lastRow.size(); i++) {
					// 对应上父节点
					if (temp.getParent().equals(lastRow.get(i))) {
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
			
			nextLevelSize += temp.getChildren().size();
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
				if (n != null && n.getN().equals("|") && line.get(j) == null) {
					// 如果是横线且没有对应的上面的元素和其连接，则设置为竖线
					line.set(j, new SCPSNode("^"));
					
					// 处理线相交
					for (int k = j - 1; k >= 0; k--) {
						if (line.get(k) == null) {
							line.set(k, new SCPSNode("-"));
						} else if (line.get(k).getN().equals("^")) {
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
					String s = sn.getN();
					
					if (s.equals("|")) {
						System.out.print("            │       ");
					} else if (s.equals("-")) {
						System.out.print("────────────────────");
					} else if (s.equals("+")) {
						System.out.print("────────────┬───────");
					} else if (s.equals("^")) {
						System.out.print("────────────┐       ");
					} else {
						System.out.print("    " + sn.toString());
					}
				} else {
					System.out.print("                    ");
				}
			}
			System.out.println();
		}
	}
	
	/***
	 * 求树有几条路径
	 * 
	 */
    public int getPathSize(SCPSNode root) {
    	List<SCPSNode> paths = new ArrayList<>();
        travelDFS(paths, root);
        return paths.size();
    }
	
	/***
	 * 打印树
	 * 深度优先遍历
	 */
	public void travelDFS(List<SCPSNode> paths, SCPSNode node) {
		if (node == null) {
			return;
		}
			
		// 叶子节点
		if (node.getChildren().size() == 0) {
			paths.add(node);
		}
		for (SCPSNode child : node.getChildren()) {
			travelDFS(paths, child);
		}
		
	}
	

	/******************** getters and setters *******************/
	public Map<String, Integer> getIList() {
		return IList;
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

	public void setIList(Map<String, Integer> iList) {
		IList = iList;
	}
	
	
}
