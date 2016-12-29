package com.haili.fp2;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kamran
 */
public class FPtreeNode {

    boolean root;
    String item;
    List<FPtreeNode> children = new ArrayList<>();
    FPtreeNode parent;
    FPtreeNode next;
    int count;

    public FPtreeNode(String item) {
        this.item = item;
        this.next = null;
        this.root = item.equals("root");
    }
    
    boolean isRoot(){
        return root;
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