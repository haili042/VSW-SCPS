package com.haili.scps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SCPSNode {

	int preCount = 0; // 检查点之前的计数 previous count
	int curCount = 0; // 检查点之后的计数 current count
	int count = 0; // 支持度计数 count
	String item; // 项名
	SCPSNode next = null;

	SCPSNode parent; // 父节点
	LinkedList<SCPSNode> children = new LinkedList<>(); // 孩子节点
//	SCPSNode nextHomonym = null; // 下一个同名兄弟节点
//	SCPSNode lastHomonym = null; // 前一个同名兄弟节点
	
	boolean isTailNode; // 是否是尾节点
	boolean isVirtual; // 是否是虚拟节点
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
			// ┏┳┓┫╋┣┗┻┛┏━┓┃┛━┗┃┌┬┐┤┘┴└├┼
			res = String.format(bracketL + "%3s,%3d,%3d,%3d" + bracketR, item, count, preCount, curCount);
		} else {
			res = String.format(bracketL + "%3s,%3d,---,---" + bracketR, item, count);
		}
		
		return res;
	}
	
	/**
	 * 删除节点 
	 */
	public void remove() {
		parent.children.remove(this);
	}
	
	/**
	 * 添加普通节点子节点
	 * @param node
	 */
	public void addChild(SCPSNode node) {
		node.parent = this;
		this.children.add(node);
	}
	
	/**
	 * 添加尾节点子节点
	 * @param node
	 * @param checkPoint
	 */
	public void addChild(SCPSNode node, int tid, int checkPoint) {
		node.parent = this;
		
		if(tid < checkPoint) {
			// 在检查点之前
			node.preCount += 1;
		} else {
			// 在检查点之后
			node.curCount += 1;
		}
		node.isTailNode = true;
		this.children.add(node);
	}
	
	/**
	 * 更新已有普通节点
	 * @param node
	 */
	public void updateChild(int count) {
		this.count += count;
	}
	
	/**
	 * 更新已有尾节点
	 * @param node
	 */
	public void updateChild(int count, int tid, int checkPoint) {
		this.count += count;

		if(tid < checkPoint) {
			// 在检查点之前
			this.preCount++;
		} else {
			this.curCount++;
		}
		isTailNode = true;

	}

	/******************** getters and setters **********************/


}
