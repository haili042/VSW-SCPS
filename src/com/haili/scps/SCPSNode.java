package com.haili.scps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SCPSNode {

	private int PTC = 0; // 检查点之前的计数
	private int CTC = 0; // 检查点之后的计数
	private int C = 0; // 支持度计数
	private String N; // 项名

	private SCPSNode parent; // 父节点
	private LinkedList<SCPSNode> children = new LinkedList<>(); // 孩子节点
//	private SCPSNode nextHomonym = null; // 下一个同名兄弟节点
//	private SCPSNode lastHomonym = null; // 前一个同名兄弟节点
	
	private boolean isTailNode;
	

	public SCPSNode() {
		this.N = "root";
	}

	public SCPSNode(String N) {
		this.N = N;
		this.C = 1;
	}
	
	public SCPSNode(String N, int C) {
		this.N = N;
		this.C = C;
	}

	public SCPSNode getChild(String item) {

		for (SCPSNode node : children) {
			if (item.equals(node.getN())) {
				return node;
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		String res = "";
		if (this.getN().equals("root")) {
			res = String.format("[======root=====]");
		} else if (this.isTailNode()) {
			// ┏┳┓┫╋┣┗┻┛┏━┓┃┛━┗┃┌┬┐┤┘┴└├┼
			res = String.format("[%3s,%3d,%3d,%3d]", this.getN(), this.getC(), this.getPTC(), this.getCTC());
		} else {
			res = String.format("[%3s,%3d,---,---]", this.getN(), this.getC());
		}
		
		return res;
	}
	
	/**
	 * 删除节点 
	 */
	public void remove() {
		SCPSNode parent = this.getParent();
		parent.getChildren().remove(this);
	}
	
	/**
	 * 添加普通节点子节点
	 * @param node
	 */
	public void addChild(SCPSNode node) {
		node.setParent(this);
		this.children.add(node);
	}
	
	/**
	 * 添加尾节点子节点
	 * @param node
	 * @param checkPoint
	 */
	public void addChild(SCPSNode node, int tid, int checkPoint) {
		node.setParent(this);
		
		if(tid < checkPoint) {
			// 在检查点之前
			node.setPTC(node.getPTC() + 1);
		} else {
			// 在检查点之后
			node.setCTC(node.getCTC() + 1);
		}
		node.setTailNode(true);
		this.children.add(node);
	}
	
	/**
	 * 更新已有普通节点
	 * @param node
	 */
	public void updateChild(int C) {
		this.C += C;
	}
	
	/**
	 * 更新已有尾节点
	 * @param node
	 */
	public void updateChild(int C, int tid, int checkPoint) {
		this.C += C;

		if(tid < checkPoint) {
			// 在检查点之前
			this.PTC++;
		} else {
			this.CTC++;
		}
		this.setTailNode(true);

	}
	
	/******************** getters and setters **********************/
	public int getPTC() {
		return PTC;
	}

	public void setPTC(int pTC) {
		PTC = pTC;
	}

	public int getCTC() {
		return CTC;
	}

	public void setCTC(int cTC) {
		CTC = cTC;
	}

	public int getC() {
		return C;
	}

	public void setC(int c) {
		C = c;
	}

	public String getN() {
		return N;
	}

	public void setN(String n) {
		N = n;
	}

	public SCPSNode getParent() {
		return parent;
	}

	public void setParent(SCPSNode parent) {
		this.parent = parent;
	}

	public LinkedList<SCPSNode> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<SCPSNode> children) {
		this.children = children;
	}

	public boolean isTailNode() {
		return isTailNode;
	}

	public void setTailNode(boolean isTailNode) {
		this.isTailNode = isTailNode;
	}

//	public SCPSNode getNextHomonym() {
//		return nextHomonym;
//	}
//
//	public void setNextHomonym(SCPSNode nextHomonym) {
//		this.nextHomonym = nextHomonym;
//	}
//
//	public SCPSNode getLastHomonym() {
//		return lastHomonym;
//	}
//
//	public void setLastHomonym(SCPSNode lastHomonym) {
//		this.lastHomonym = lastHomonym;
//	}

}
