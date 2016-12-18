package com.haili.vsw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.haili.scps.SCPSTree;
import com.haili.util.ConfigReader;

public class VSW {
	private int initSize;
	private int paneSize;
	private double change_th;
	private int CP; // 检查点
	private SCPSTree T = new SCPSTree();
	private Set<String> lastFPSet = new HashSet<>();
	
	private String spliter = " ";
	
	public VSW(int initSize, int paneSize) {
		this.initSize = initSize;
		this.paneSize = paneSize;
		this.CP = initSize; // 初始检查点为初始窗口大小
	}
	
	public static void main(String[] args) {
		VSW vsw = new VSW(12, 1);
		vsw.run();
	}
	
	/**
	 * 检测是否已经发生了概念改变
	 */
	public boolean isConceptChange(Set<String> curFPSet) {
		Set<String> temp = new HashSet<>();
		
		// curFPSet - lastFPSet
		temp.clear();
		temp.addAll(curFPSet);
		temp.removeAll(lastFPSet);
		int F1 = temp.size();
		
		// lastFPSet - curFPSet
		temp.clear();
		temp.addAll(lastFPSet);
		temp.removeAll(curFPSet);
		int F2 = temp.size();
		
		int F = 1000; // TODO 全集的数量
		
		double result = (double)(F1 + F2) / (F + F1);
		if (result > change_th) {
			return true;
		}
		return false;
	}
	
	/**
	 * 读取数据流
	 */
	public void run() {
		System.out.println("vsw runing..., init_size : " + initSize + ", pane_size: " + paneSize);

		Properties properties = new ConfigReader().getProperties();
		String filePath = properties.getProperty("datasetPath") + System.getProperty("file.separator") + properties.getProperty("test");
		FileReader fr;
		
		try {
			fr = new FileReader(new File(filePath));

			BufferedReader br = new BufferedReader(fr);

			String line = null;
			int lineNum = 0;
			List<Map<String, Object>> pane = new ArrayList<>();

			while ((line = br.readLine()) != null) {
				
				if (line.trim() != "") {
					Map<String, Object> transaction = new HashMap<>();
					transaction.put("tid", lineNum); // 添加行号
					List<String> record = new ArrayList<String>();
					
					String[] items = line.split(spliter);
					for (String item : items) {
						record.add(item);
					}
					transaction.put("record", record);
					pane.add(transaction);
				}
				
				++lineNum;
				
				// 开始一些列操作
				if (lineNum % paneSize == 0) {
					
					T.insertPane(pane, CP);
					
					System.out.println("current window size : " + T.getCurrentWindowSize() + "\n");
					pane.clear(); // 重置pane
				}

			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
//		T.removeStaleWindow(T.getRoot());

	}
	
}
