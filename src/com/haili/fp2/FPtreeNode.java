package com.haili.fp2;

import java.util.ArrayList;
import java.util.List;

import com.haili.scps.IlistItem;

/**
 *
 * @author Kamran
 */
public class FPtreeNode {

    boolean isRoot;
    String item;
    List<FPtreeNode> children = new ArrayList<>();
    FPtreeNode parent;
    FPtreeNode next = null;
    int count;

    public FPtreeNode(String item) {
        this.item = item;
        this.isRoot = item.equals("root");
    }
    
    
	@Override
	public String toString() {
		String res = "";
		String bracketL = "[";
		String bracketR = "]";
		
		if (item.equals("root")) {
			res = String.format(bracketL + "======root=====" + bracketR);
		} else {
			res = String.format(bracketL + "%3s,%3d,---,---" + bracketR, item, count);
		}
		
		return res;
	}
	

}