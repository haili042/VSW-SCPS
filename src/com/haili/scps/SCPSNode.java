package com.haili.scps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SCPSNode {

	int preCount = 0; // ����֮ǰ�ļ��� previous count
	int curCount = 0; // ����֮��ļ��� current count
	int count = 0; // ֧�ֶȼ��� count
	String item; // ����
	SCPSNode next = null;

	SCPSNode parent; // ���ڵ�
	LinkedList<SCPSNode> children = new LinkedList<>(); // ���ӽڵ�
//	SCPSNode nextHomonym = null; // ��һ��ͬ���ֵܽڵ�
//	SCPSNode lastHomonym = null; // ǰһ��ͬ���ֵܽڵ�
	
	boolean isTailNode; // �Ƿ���β�ڵ�
	boolean isVirtual; // �Ƿ�������ڵ�
	boolean isRoot;
	

	public SCPSNode(String item) {
		this.item = item;
		this.count = 1;
		this.isRoot = "root".equals(item);
	}
	
	public SCPSNode(String item, int count) {
		this.item = item;
		this.count = count;
		this.isRoot = "root".equals(item);

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

	/******************** getters and setters **********************/


}
