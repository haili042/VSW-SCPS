package com.haili.mining;

import java.util.Map;

import com.haili.scps.SCPSTree;

public class COFI {

	SCPSTree scpsTree; // ��fp��
	Map<String, Integer> Ilist;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	public COFI(SCPSTree scpsTree) {
		this.scpsTree = scpsTree;
		this.Ilist = scpsTree.getIList();
	}

	// �ھ�
	public void getFP() {
		// ����
		for (String key : Ilist.keySet()) {
			
			
		}
	}

}
