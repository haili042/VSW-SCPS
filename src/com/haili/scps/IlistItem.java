package com.haili.scps;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class IlistItem {

	private String N; // ����
	private int C; // ֧�ֶȼ���
	private LinkedList<SCPSNode> nextBrotherList = new LinkedList<>(); // ָ��ͬ���ֵܵ�ָ��

	public IlistItem(String N) {
		this.N = N;
		this.C = 0;
	}

	public IlistItem(String N, int C) {
		this.N = N;
		this.C = C;
	}

	public String toString() {
		return String.format("[%s, %d]", N, C);
	}

	/**
	 * Ƶ������Ƚϴ�С
	 * @param item
	 * @return
	 */
	public int compareTo(IlistItem item) {

		// ��ʼҲ�����ֵ�������
		// Ȩֵ��ͬ�����ֵ�������
		if (this.C == item.getC()) {
			return item.getN().compareTo(this.N);
		} else {
			// ����Ƚϼ�����С
			return this.C - item.getC();
		}
	}
	
	/**
	 * ����ֵܽڵ�
	 */
	public void addBrother(SCPSNode node) {
		if (!nextBrotherList.contains(node)) {
			nextBrotherList.add(node);
		}
	}
	
	/**
	 * ���¼���
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
		return nextBrotherList;
	}

}
