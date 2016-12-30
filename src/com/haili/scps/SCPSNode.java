package com.haili.scps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class SCPSNode {

	public int preCount = 0; // ����֮ǰ�ļ��� previous count
	public int curCount = 0; // ����֮��ļ��� current count
	public int count = 0; // ֧�ֶȼ��� count
	public String item; // ����
	public SCPSNode next;

	public SCPSNode parent; // ���ڵ�
	public LinkedList<SCPSNode> children = new LinkedList<>(); // ���ӽڵ�
//	SCPSNode nextHomonym = null; // ��һ��ͬ���ֵܽڵ�
//	SCPSNode lastHomonym = null; // ǰһ��ͬ���ֵܽڵ�
	
	public boolean isTailNode; // �Ƿ���β�ڵ�
	public boolean isVirtual; // �Ƿ�������ڵ�
	public boolean isRoot;
	
	public SCPSNode(String item) {
		this.item = item;
		this.count = 1;
        this.isRoot = item.equals("root");
	}
	
	public SCPSNode(String item, int count) {
		this.item = item;
		this.count = count;
        this.isRoot = item.equals("root");
	}

	public SCPSNode getChild(String item) {

		for (SCPSNode node : children) {
			if (item.equals(node.item)) {
				return node;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		String res = "";
		String bracketL = "[";
		String bracketR = "]";
		
		if (isVirtual) {
			bracketL = "<";
			bracketR = ">";
		}

		if (item.equals("root")) {
			res = String.format(bracketL + "======root=====" + bracketR);
		} else if (isTailNode) {
			// ���ש��ϩ�ǩ��ߩ��������������������Щ��ȩ��ة�����
			res = String.format(bracketL + "%3s,%3d,%3d,%3d" + bracketR, item, count, preCount, curCount);
		} else {
			res = String.format(bracketL + "%3s,%3d,---,---" + bracketR, item, count);
		}
		
		return res;
	}
	
	/**
	 * ɾ���ڵ� 
	 */
	public void remove() {
		parent.children.remove(this);
	}
	
	/**
	 * �����ͨ�ڵ��ӽڵ�
	 * @param node
	 */
	public void addChild(SCPSNode node) {
		node.parent = this;
		this.children.add(node);
	}
	
	/**
	 * ���β�ڵ��ӽڵ�
	 * @param node
	 * @param checkPoint
	 */
	public void addChild(SCPSNode node, int tid, int checkPoint) {
		node.parent = this;
		
		if(tid < checkPoint) {
			// �ڼ���֮ǰ
			node.preCount += 1;
		} else {
			// �ڼ���֮��
			node.curCount += 1;
		}
		node.isTailNode = true;
		this.children.add(node);
	}
	
	/**
	 * ����������ͨ�ڵ�
	 * @param node
	 */
	public void updateChild(int count) {
		this.count += count;
	}
	
	/**
	 * ��������β�ڵ�
	 * @param node
	 */
	public void updateChild(int count, int tid, int checkPoint) {
		this.count += count;

		if(tid < checkPoint) {
			// �ڼ���֮ǰ
			this.preCount++;
		} else {
			this.curCount++;
		}
		isTailNode = true;

	}
	
	/**
	 * �����ڵ㰴��Ƶ������Ƚϴ�С
	 * @param item
	 * @return
	 */
	public int compareTo(SCPSNode node) {

		// ��ʼҲ�����ֵ�������
		// Ȩֵ��ͬ�����ֵ�������
		if (this.count == node.count) {
			return node.item.compareTo(this.item);
		} else {
			// ����Ƚϼ�����С
			return this.count - node.count;
		}
	}

	/******************** getters and setters **********************/


}
