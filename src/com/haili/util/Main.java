package com.haili.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.haili.statical.fptree.FPTree;
import com.haili.statical.fptree.TreeNode2;
import com.haili.sw.SW;

public class Main {

/*	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		 * String s1="i1"; int flag=s1.compareTo("I1");
		 * System.out.println(flag);
		 
		// 读取数据
		// 支持度
		int minsup = 4;
		FPTree fpg = new FPTree(2);
		LinkedList<LinkedList<String>> records = fpg.readF1();
		LinkedList<TreeNode2> orderheader = fpg.buildHeaderLink(records);
		fpg.orderF1(orderheader);
		fpg.fpgrowth(records, null);
	}
	
	// 读取文件
	public LinkedList<LinkedList<String>> readF1() throws IOException {
		LinkedList<LinkedList<String>> records = new LinkedList<LinkedList<String>>();
		// String filePath = "dataset/statical/fptreeTest.csv";
		String filePath = "dataset/statical/test.ascii";
//		String filePath = "dataset/statical/T40I10D100K.ascii";
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath)));
		int lineNum = 0;
		for (String line = br.readLine(); line != null; line = br.readLine(), lineNum++) {
			if (line.length() == 0 || "".equals(line))
				continue;
			String[] str = line.split(" ");
			LinkedList<String> litm = new LinkedList<String>();
			System.out.println("line" + lineNum + " readed");
			for (int i = 0; i < str.length; i++) {
				litm.add(str[i].trim());
			}
			records.add(litm);
		}
		br.close();
		return records;
	}*/
	
	public static void main(String[] args) throws IOException {
		
//		DataIO dataset = new DataIO("test"); // accidents.dat 文件
//		VSW vsw = new VSW(4);
//		List<Set<String>> db = dataset.readData();
		
		
//		data.start();
	}
}
