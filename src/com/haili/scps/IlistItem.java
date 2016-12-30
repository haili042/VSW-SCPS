package com.haili.scps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IlistItem {

	private String N; // 名称
	private int C; // 支持度计数
	private SCPSNode next; // 指向同名兄弟的指针

	public IlistItem(String N) {
		this.N = N;
		this.C = 0;
	}

	public IlistItem(String N, int C) {
		this.N = N;
		this.C = C;
	}

	public String toString() {
		return String.format("%s(%d)", N, C);
	}

	/**
	 * 频繁降序比较大小
	 * @param item
	 * @return
	 */
	public int compareTo(IlistItem item) {

		// 初始也按照字典序排序
		// 权值相同则按照字典序排序
		if (this.C == item.getC()) {
			return item.getN().compareTo(this.N);
		} else {
			// 否则比较计数大小
			return this.C - item.getC();
		}
	}
	
	/**
	 * 添加兄弟节点
	 */
	public void addBrother(SCPSNode node) {
		if (!next.contains(node)) {
			next.add(node);
		}
	}
	
	/**
	 * 更新计数
	 * @param n
	 */
	public void updateC(int n) {
		this.C = this.C + n;
	}
	
	public String getN() {
		return N;
	}

	public void setN(String n) {
		N = n;
	}

	public int getC() {
		return C;
	}

	public void setC(int c) {
		C = c;
	}

	public List<SCPSNode> getNextBrotherList() {
		return next;
	}

}
