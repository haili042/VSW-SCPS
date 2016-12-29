package com.haili.scps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeNode {
	
	int count = 0; // 支持度计数 count
	String item; // 项名
	TreeNode parent; // 父节点
	LinkedList<TreeNode> children = new LinkedList<>(); // 孩子节点
	boolean isRoot;
	
	public TreeNode(String item) {
		this.item = item;
		isRoot = "root".equals(item);
	}
	
	public TreeNode(String item, int count) {
		this.item = item;
		this.count = count;
		isRoot = "root".equals(item);
	}
	
	
	/**
	 * 打印树
	 * 广度优先
	 */
	public void print(TreeNode root) {
		
		LinkedList<TreeNode> queue = new LinkedList<>();
		queue.add(root);
		int nextLevelSize = 0; // 下一层节点个数， 打印输出用
		int currentLevelSize = 1; // 本层节点个数， 打印输出用
		
		TreeNode lastNode = null; // 同层上一个访问节点， 打印输出用
		List<List<TreeNode>> resultList = new ArrayList<>();
		List<TreeNode> row = new ArrayList<>();
		
		while (!queue.isEmpty()) {
			TreeNode temp = queue.poll();
			LinkedList<TreeNode> children = temp.children;
			
			for (int i = 0; i < getPathSize(lastNode) - 1; i++) {
				row.add(null); // 占位
			}
			
			if (resultList.size() > 0) {
				List<TreeNode> lastRow = resultList.get(resultList.size() - 1); // 上一层
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
			
			for (TreeNode n : children) {
				queue.add(n);
			}
			
			if (--currentLevelSize == 0) {
				currentLevelSize = nextLevelSize;
				nextLevelSize = 0;
				lastNode = null;
				List<TreeNode> emptyRow = new ArrayList<>(); // 空行来画线
				for (TreeNode n : row) {
					if (n != null) {
						emptyRow.add(new TreeNode("|"));
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
			List<TreeNode> line = resultList.get(i);
			List<TreeNode> nextLine = resultList.get(i + 1);
			// 横线
			for (int j = 0; j < nextLine.size(); j++) {
				TreeNode n = nextLine.get(j);
				if (line.size() < j + 1) {
					line.add(null);
				}
				if (n != null && n.item.equals("|") && line.get(j) == null) {
					// 如果是横线且没有对应的上面的元素和其连接，则设置为竖线
					line.set(j, new TreeNode("^"));
					
					// 处理线相交
					for (int k = j - 1; k >= 0; k--) {
						if (line.get(k) == null) {
							line.set(k, new TreeNode("-"));
						} else if (line.get(k).item.equals("^")) {
							line.set(k, new TreeNode("+"));
						} else {
							break;
						}
					}
				}
				
			}
			
			nextLine = resultList.get(i+1);
		}
		
		for (int i = 1; i < resultList.size(); i++) {
			List<TreeNode> line = resultList.get(i);
			
			// 打印
			for (int j = 0; j < line.size(); j++) {
				TreeNode sn = line.get(j);
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
    public int getPathSize(TreeNode root) {
    	List<TreeNode> leaves = new ArrayList<>();
        travelDFS(leaves, root, "leaves");
        return leaves.size();
    }
    
	
	/***
	 * 深度优先遍历,
	 * 获取叶子或者尾节点集合
	 * 
	 */
	public void travelDFS(List<TreeNode> result, TreeNode node, String type) {
		if (node == null) {
			return;
		}
			
//		if (type.equals("leaves") && node.children.size() == 0) {
//			// 叶子节点
//			result.add(node);
//		} else if (type.equals("tailNodes") && node.isTailNode) {
//			// 尾节点
//			result.add(node);
//		}
		if (type.equals("leaves") && node.children.size() == 0) {
			// 叶子节点
			result.add(node);
		}
		
		
		for (TreeNode child : node.children) {
			travelDFS(result, child, type);
		}
		
	}
}
