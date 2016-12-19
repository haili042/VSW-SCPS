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

public class SCPSTree {

//	private Map<String, IlistItem> IList = new LinkedHashMap<>(); // i-list
	
	private Ilist ilist = new Ilist(); // i-list
	
	private List<SCPSNode> tailNodeList = new ArrayList<>(); // ��¼β�ڵ㣬 ���ٱ���������
	private SCPSNode root = new SCPSNode();
	private int currentWindowSize = 0; // ��ǰ���ڴ�С
    

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
		ilist.addPane(pane); // 2 ����i-list
		reconstruct(); // ��������I-list�ع�SCPS-tree
	}
	
	
	/***
	 * �����ع��׶εĲ��룬 ���tid�������޹�
	 * @param record
	 */
	public void insertPath(List<String> record, int lastC, int lastPTC, int lastCTC) {

		SCPSNode temp = root;
		ilist.sortTransaction(record); // ����i-list����
		
		for (int i = 0; i < record.size(); i++) {
			String item = record.get(i);
			SCPSNode child = temp.getChild(item);
			
			if (i == record.size() - 1) {
				// ���β�ڵ�
				if (child == null) {
					SCPSNode node = new SCPSNode(item, lastC);
					node.setTailNode(true);
					node.setPTC(lastPTC);
					node.setCTC(lastCTC);
					temp.addChild(node);
					temp = node;
					ilist.addItemBrother(node.getN(), node);

				} else {
					child.updateChild(lastC); // �������нڵ����
					child.setTailNode(true);
					child.setPTC(lastPTC);
					child.setCTC(lastCTC);
					temp = child;
				}
				
			} else {
				// ��ͨ�ڵ���뵽����
				if (child == null) {
					SCPSNode node = new SCPSNode(item, lastC);
					
					temp.addChild(node);
					temp = node;
					ilist.addItemBrother(node.getN(), node);

				} else {
					child.updateChild(lastC); // �������нڵ����
					temp = child;
				}
			}
				
		}

	}
	
	/**
	 * �������������SCPS����
	 * @param record ���������
	 */
	public void insertPath(Map<String, Object> transaction, int checkPoint) {

		SCPSNode temp = root;
		int tid = (int) transaction.get("tid");
		List<String> record = (List<String>) transaction.get("record");
		
		ilist.sortTransaction(record); // ����i-list����
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
					ilist.addItemBrother(node.getN(), node);
					
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
					ilist.addItemBrother(node.getN(), node);

				} else {
					child.updateChild(1); // �������нڵ����
					temp = child;
				}
			}
				
		}

	}
	
	/**
	 * ������ɾ��·��, ��Ҷ�ӽڵ㿪ʼ
	 * @param leaf
	 * @param leafCount
	 * @param needUpdateIlistCount �Ƿ���ɾ������֮ǰ����ʱ��ɾ������
	 */
	public void removePath(SCPSNode leaf, int leafCount, boolean needUpdateIlistCount) {
		SCPSNode temp = leaf;
		while (!temp.getN().equals("root")) {
			temp.setC(temp.getC() - leafCount);
			
			// ���� i-list ����
			if (needUpdateIlistCount) {
				// ����PTC��CTC��ֵ
				temp.setPTC(temp.getCTC());
				temp.setCTC(0);
				
				ilist.updateItem(temp.getN(), -leafCount); // ilist ��ȥ����
//				IList.put(temp.getN(), IList.get(temp.getN()) - leafCount);
			}
			
			// ������Ϊ0�� ��ɾ���ýڵ�
			if (temp.getC() == 0) {
				ilist.removeItemBrother(temp.getN(), temp); // ɾ��ilist�ֵܽڵ�
				temp.remove();
			}
			temp = temp.getParent();
		}

		// ���´��ڴ�С
		if (needUpdateIlistCount) {
			this.currentWindowSize -= leafCount;
		}
	}
	
	
	/**
	 * ����BSM���ع������Ľṹ
	 */
	public void reconstruct() {
		
		print(root);
	  	List<SCPSNode> tailNodes = new ArrayList<>();
        travelDFS(tailNodes, root, "tailNodes");
        
        
        
        for (int i = tailNodes.size() - 1; i >= 0; i--) {
			
        	SCPSNode tailNode = tailNodes.get(i);
        	int lastC = tailNode.getC();
        	int lastPTC = tailNode.getPTC();
        	int lastCTC = tailNode.getCTC();
        	
			// ��ȡָ����ʽ
			SCPSNode temp = tailNode;
			List<String> record = new ArrayList<>();
			while (!temp.getN().equals("root")) {
				record.add(0, temp.getN());
				temp = temp.getParent();
			}
			
			// �������Ͳ���ɾ����
			if (!ilist.isSorted(record)) {
				// ������ɾ��һ��·��
				removePath(tailNode, tailNode.getC(), false);
//				print(root);
				
				// ��������²��뵽���У� ��ʱ������޹�
				insertPath(record, lastC, lastPTC, lastCTC);
//				print(root);
			}
			
		}
		System.out.println("after reconstruction...");
		print(root);
		System.out.println("current window size : " + getCurrentWindowSize() + "\n");

	}
	
	/**
	 * ɾ����������
	 */
	public void removeStaleWindow(SCPSNode root) {
		System.out.println("removing old window .... ");
	  	List<SCPSNode> tailNodes = new ArrayList<>();
        travelDFS(tailNodes, root, "tailNodes");
        
        for (SCPSNode leaf : tailNodes) {
			// ������ɾ��һ��·��, ȨֵΪ��·��Ҷ�ӽڵ��PTCֵ
        	int ptc = leaf.getPTC();
			removePath(leaf, ptc, true);
		}
		
        reconstruct();

	}
	
	/**
	 * ��ӡ��
	 * �������
	 */
	public void print(SCPSNode root) {
		
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
    public int getPathSize(SCPSNode root) {
    	List<SCPSNode> leaves = new ArrayList<>();
        travelDFS(leaves, root, "leaves");
        return leaves.size();
    }
	
	/***
	 * ������ȱ���,
	 * ��ȡҶ�ӻ���β�ڵ㼯��
	 * 
	 */
	public void travelDFS(List<SCPSNode> result, SCPSNode node, String type) {
		if (node == null) {
			return;
		}
			
		if (type.equals("leaves") && node.getChildren().size() == 0) {
			// Ҷ�ӽڵ�
			result.add(node);
		} else if (type.equals("tailNodes") && node.isTailNode()) {
			// β�ڵ�
			result.add(node);
		}
		
		
		for (SCPSNode child : node.getChildren()) {
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
