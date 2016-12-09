package com.haili.scps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
		updateIList(pane); // 2 ����i-list
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
		
		// ɾ����Ƶ����
		// TODO
		for (String item : transaction) {
			
		}
		
		
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

	public void travelBFS(SCPSNode root) {
		
		LinkedList<SCPSNode> queue = new LinkedList<>();
		queue.add(root);
		int nextLevelSize = 0; // ��һ��ڵ������ ��ӡ�����
		int currentLevelSize = 1; // ����ڵ������ ��ӡ�����
		
		SCPSNode lastNode = null; // ͬ����һ�����ʽڵ㣬 ��ӡ�����
		List<List<String>> resultList = new ArrayList<>();
		List<String> row = new ArrayList<>();
		
		while (!queue.isEmpty()) {
			SCPSNode temp = queue.poll();
			LinkedList<SCPSNode> children = temp.getChildren();
			
			for (int i = 0; i < getMaxWidth(lastNode) - currentLevelSize; i++) {
				row.add(""); // ռλ
			}
			row.add(temp.toString());
			
			nextLevelSize += temp.getChildren().size();
			lastNode = temp;
			
			for (SCPSNode n : children) {
				queue.add(n);
			}
			
			if (--currentLevelSize == 0) {
				currentLevelSize = nextLevelSize;
				nextLevelSize = 0;
				lastNode = null;
				List<String> emptyRow = new ArrayList<>(); // ����������
				for (String s : row) {
					if (!"".equals(s)) {
						emptyRow.add("|");
					} else {
						emptyRow.add(s);
					}
				}
				resultList.add(emptyRow);
				
				resultList.add(row);
				row = new ArrayList<>();
			}
		}
		
		
		for (int i = 1; i < resultList.size() - 1; i += 2) {
			List<String> line = resultList.get(i);
			List<String> nextLine = resultList.get(i + 1);
			// ����
			for (int j = 0; j < nextLine.size(); j++) {
				String s = nextLine.get(j);
				if (line.size() < j + 1) {
					line.add("");
				}
				if (s.equals("|") && line.get(j).equals("")) {
					// ����Ǻ�����û�ж�Ӧ�������Ԫ�غ������ӣ�������Ϊ����
					line.set(j, "^");
					
					// �������ཻ
					for (int k = j - 1; k >= 0; k--) {
						
						if (line.get(k).equals("")) {
							line.set(k, "-");
						} else if (line.get(k).equals("^")) {
							line.set(k, "+");
						} else {
							break;
						}
					}
				}
				
			}
			
			nextLine = resultList.get(i+1);
		}
		
		for (int i = 1; i < resultList.size(); i++) {
			List<String> line = resultList.get(i);
			
			// ��ӡ
			for (int j = 0; j < line.size(); j++) {
				String s = line.get(j);
				if (s.equals("")) {
					System.out.print("                    ");
				} else if (s.equals("|")) {
					System.out.print("          ��         ");
				} else if (s.equals("-")) {
					System.out.print("����������������������������������������");
				} else if (s.equals("+")) {
					System.out.print("���������������������Щ�����������������");
				} else if (s.equals("^")) {
					System.out.print("����������������������         ");
				} else {
					System.out.print("    " + s);
				}
			}
			System.out.println();
		}
	}
	
	/***
	 * �����������
	 * 
	 */
    public static int getMaxWidth(SCPSNode root) {
        if (root == null)
            return 0;

        LinkedList<SCPSNode> queue = new LinkedList<>();
        int maxWitdth = 1; // �����
        queue.add(root); // ���

        while (true) {
            int len = queue.size(); // ��ǰ��Ľڵ����
            if (len == 0)
                break;
            while (len > 0) {// �����ǰ�㣬���нڵ�
            	SCPSNode t = queue.poll();
                len--;
                for (SCPSNode n : t.getChildren()) {
                	queue.add(n); // ��һ��ڵ����
				}
            }
            maxWitdth = Math.max(maxWitdth, queue.size());
        }
        return maxWitdth;
    }
	
	/***
	 * ��ӡ��
	 * ������ȱ���
	 */
	private List<List<String>> DFSList = new ArrayList<>();
	public void travelDFS(SCPSNode node) {
		
		if (node.getN() != null) {
			
			// Ҷ�ӽڵ�
			if (node.getChildren().size() == 0) {
				
				List<String> row = new ArrayList<>();
				
				// ��Ҷ�ӽڵ㵽���ڵ��·��
				SCPSNode temp = node;
				while (temp.getN() != null) {
					row.add(temp.toString());
					temp = temp.getParent();
				}
				DFSList.add(row);
			}
			
		}
		
		for (SCPSNode child : node.getChildren()) {
			travelDFS(child);
		}
		
	}
	
	public void printBFS() {
		travelBFS(root);
	}
	
	/***
	 * �����ظ��ַ���
	 * @param str
	 * @param time
	 * @return
	 */
	public String repeat(String str, int time) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < time; i++) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	public void printDFS() {
		travelDFS(root);
		for (int i = 0; i < DFSList.size(); i++) {
			
			List<String> row = DFSList.get(i);
			for (int j = row.size() - 1; j >= 0; j--) {
				String item = row.get(j);
				System.out.print(item + " ");
			}
			System.out.println();
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
