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
	private List<SCPSNode> tailNodeList = new ArrayList<>(); // ��¼β�ڵ㣬 ���ٱ���������
	private SCPSNode root = new SCPSNode();
	private int currentWindowSize = 0; // ��ǰ���ڴ�С
	private double minSup; // ��С֧�ֶ�
    

	public SCPSTree() {}
	
	
	/**
	 * ����һ�����ݵ�����
	 * @param pane
	 */
	public void insertPane(List<Map<String, Object>> pane, int checkPoint) {
		
		// 1 ����һ������
		for (Map<String, Object> transaction : pane) {

			this.currentWindowSize++;
			insertPath(transaction, checkPoint); // ����õ�������뵽SCPS����
		}
//		updateIList(pane); // 2 ����i-list
		reconstruct(); // ��������I-list�ع�SCPS-tree
	}
	
	/**
	 * ������������i-list˳��
	 * ÿ����һ��pane�����ݺ�ִ��һ��
	 */
	public void updateIList(List<Map<String, Object>> pane) {
		System.out.print("update i-list from " + IList.toString());
		for (Map<String, Object> transaction : pane) {
			int tid = (int) transaction.get("tid");
			List<String> record = (List<String>) transaction.get("record");
			
			for (String str : record) {
				if (IList.get(str) == null) {
					IList.put(str, 1); // ��ʼֵΪ1
				} else {
					IList.put(str, IList.get(str) + 1);
				}
			}
		}
		System.out.println(" to : " + IList.toString());
	}
	
	
	/**
	 * �������������SCPS����
	 * @param record ���������
	 */
	public void insertPath(Map<String, Object> transaction, int checkPoint) {

		SCPSNode temp = root;
		int tid = (int) transaction.get("tid");
		List<String> record = (List<String>) transaction.get("record");
		
		sortTransaction(record); // ����i-list����
		System.out.println("insert tid " + tid + " : " + record.toString());
		
		for (int i = 0; i < record.size(); i++) {
			String item = record.get(i);
			SCPSNode child = temp.getChild(item);
			
			if (i == record.size() - 1) {
				// ���β�ڵ�
				if (child == null) {
					SCPSNode node = new SCPSNode(item);
					
					temp.addChild(node, tid, checkPoint);
					temp = node;
				} else {
					child.updateChild(1, tid, checkPoint); // �������нڵ����
					temp = child;
				}
				
			} else {
				// ��ͨ�ڵ���뵽����
				if (child == null) {
					SCPSNode node = new SCPSNode(item);
					
					temp.addChild(node);
					temp = node;
				} else {
					child.updateChild(1); // �������нڵ����
					temp = child;
				}
			}
				
		}

	}
	
	/**
	 * �������i-list����
	 * @param transaction
	 * @return
	 */
	public void sortTransaction(List<String> transaction) {
		
		Collections.sort(transaction, new Comparator<String>(){
			
			@Override
			public int compare(String o1, String o2) {
				
				if (IList.get(o1) == null || IList.get(o2) == null || IList.get(o1) == IList.get(o2)) {
					// Ȩֵ��ͬ�����ֵ�������
					// ��ʼҲ�����ֵ�������
					return o1.compareTo(o2);
				} else {
					// ���Ȱ���֧������������
					return IList.get(o2) - IList.get(o1);
				}
			}
		});
	}
	
	
	/**
	 * ������ɾ��·��
	 */
	public void removePath(SCPSNode path) {
		
	}
	
	/**
	 * ��·����������
	 * 
	 */
	public void sortPath() {
		
	}
	
	/**
	 * ����BSM���ع������Ľṹ
	 */
	public void reconstruct() {
		printBFS(root);
		System.out.println("reconstructing...");
	}
	
	/**
	 * ɾ������֮ǰ������
	 */
	public void removeStale() {
		
	}
	
	/**
	 * ��ӡ��
	 * �������
	 */
	public void printBFS(SCPSNode root) {
		
		LinkedList<SCPSNode> queue = new LinkedList<>();
		queue.add(root);
		int nextLevelSize = 0; // ��һ��ڵ������ ��ӡ�����
		int currentLevelSize = 1; // ����ڵ������ ��ӡ�����
		
		SCPSNode lastNode = null; // ͬ����һ�����ʽڵ㣬 ��ӡ�����
		List<List<SCPSNode>> resultList = new ArrayList<>();
		List<SCPSNode> row = new ArrayList<>();
		
		while (!queue.isEmpty()) {
			SCPSNode temp = queue.poll();
			LinkedList<SCPSNode> children = temp.getChildren();
			
			for (int i = 0; i < getPathSize(lastNode) - 1; i++) {
				row.add(null); // ռλ
			}
			
			if (resultList.size() > 0) {
				List<SCPSNode> lastRow = resultList.get(resultList.size() - 1); // ��һ��
				int pos = 0;
				for (int i = 0; i < lastRow.size(); i++) {
					// ��Ӧ�ϸ��ڵ�
					if (temp.getParent().equals(lastRow.get(i))) {
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
			
			nextLevelSize += temp.getChildren().size();
			lastNode = temp;
			
			for (SCPSNode n : children) {
				queue.add(n);
			}
			
			if (--currentLevelSize == 0) {
				currentLevelSize = nextLevelSize;
				nextLevelSize = 0;
				lastNode = null;
				List<SCPSNode> emptyRow = new ArrayList<>(); // ����������
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
			// ����
			for (int j = 0; j < nextLine.size(); j++) {
				SCPSNode n = nextLine.get(j);
				if (line.size() < j + 1) {
					line.add(null);
				}
				if (n != null && n.getN().equals("|") && line.get(j) == null) {
					// ����Ǻ�����û�ж�Ӧ�������Ԫ�غ������ӣ�������Ϊ����
					line.set(j, new SCPSNode("^"));
					
					// �������ཻ
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
			
			// ��ӡ
			for (int j = 0; j < line.size(); j++) {
				SCPSNode sn = line.get(j);
				if (sn != null) {
					String s = sn.getN();
					
					if (s.equals("|")) {
						System.out.print("            ��       ");
					} else if (s.equals("-")) {
						System.out.print("����������������������������������������");
					} else if (s.equals("+")) {
						System.out.print("�������������������������Щ�������������");
					} else if (s.equals("^")) {
						System.out.print("��������������������������       ");
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
	 * �����м���·��
	 * 
	 */
    public int getPathSize(SCPSNode root) {
    	List<SCPSNode> paths = new ArrayList<>();
        travelDFS(paths, root);
        return paths.size();
    }
	
	/***
	 * ��ӡ��
	 * ������ȱ���
	 */
	public void travelDFS(List<SCPSNode> paths, SCPSNode node) {
		if (node == null) {
			return;
		}
			
		// Ҷ�ӽڵ�
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
