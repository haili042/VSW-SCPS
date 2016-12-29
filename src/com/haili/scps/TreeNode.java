package com.haili.scps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class TreeNode {
	
	int count = 0; // ֧�ֶȼ��� count
	String item; // ����
	TreeNode parent; // ���ڵ�
	LinkedList<TreeNode> children = new LinkedList<>(); // ���ӽڵ�
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
	 * ��ӡ��
	 * �������
	 */
	public void print(TreeNode root) {
		
		LinkedList<TreeNode> queue = new LinkedList<>();
		queue.add(root);
		int nextLevelSize = 0; // ��һ��ڵ������ ��ӡ�����
		int currentLevelSize = 1; // ����ڵ������ ��ӡ�����
		
		TreeNode lastNode = null; // ͬ����һ�����ʽڵ㣬 ��ӡ�����
		List<List<TreeNode>> resultList = new ArrayList<>();
		List<TreeNode> row = new ArrayList<>();
		
		while (!queue.isEmpty()) {
			TreeNode temp = queue.poll();
			LinkedList<TreeNode> children = temp.children;
			
			for (int i = 0; i < getPathSize(lastNode) - 1; i++) {
				row.add(null); // ռλ
			}
			
			if (resultList.size() > 0) {
				List<TreeNode> lastRow = resultList.get(resultList.size() - 1); // ��һ��
				int pos = 0;
				for (int i = 0; i < lastRow.size(); i++) {
					// ��Ӧ�ϸ��ڵ�
					if (temp.parent.equals(lastRow.get(i))) {
						pos = i;
					}
				}
				// ��Ӧ�ϸ��ڵ�
				if ((pos - row.size()) > 0) {
					for (int i = 0; i <= pos - row.size(); i++) {
						row.add(null); // ռλ
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
				List<TreeNode> emptyRow = new ArrayList<>(); // ����������
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
			// ����
			for (int j = 0; j < nextLine.size(); j++) {
				TreeNode n = nextLine.get(j);
				if (line.size() < j + 1) {
					line.add(null);
				}
				if (n != null && n.item.equals("|") && line.get(j) == null) {
					// ����Ǻ�����û�ж�Ӧ�������Ԫ�غ������ӣ�������Ϊ����
					line.set(j, new TreeNode("^"));
					
					// �������ཻ
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
			
			// ��ӡ
			for (int j = 0; j < line.size(); j++) {
				TreeNode sn = line.get(j);
				if (sn != null) {
					String s = sn.item;
					
					if (s.equals("|")) {
						System.out.print("            ��        ");
					} else if (s.equals("-")) {
						System.out.print("������������������������������������������");
					} else if (s.equals("+")) {
						System.out.print("�������������������������Щ���������������");
					} else if (s.equals("^")) {
						System.out.print("��������������������������        ");
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
	 * �����м���·��
	 * 
	 */
    public int getPathSize(TreeNode root) {
    	List<TreeNode> leaves = new ArrayList<>();
        travelDFS(leaves, root, "leaves");
        return leaves.size();
    }
    
	
	/***
	 * ������ȱ���,
	 * ��ȡҶ�ӻ���β�ڵ㼯��
	 * 
	 */
	public void travelDFS(List<TreeNode> result, TreeNode node, String type) {
		if (node == null) {
			return;
		}
			
//		if (type.equals("leaves") && node.children.size() == 0) {
//			// Ҷ�ӽڵ�
//			result.add(node);
//		} else if (type.equals("tailNodes") && node.isTailNode) {
//			// β�ڵ�
//			result.add(node);
//		}
		if (type.equals("leaves") && node.children.size() == 0) {
			// Ҷ�ӽڵ�
			result.add(node);
		}
		
		
		for (TreeNode child : node.children) {
			travelDFS(result, child, type);
		}
		
	}
}
