package com.haili.sw;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.haili.cofi.COFI;
import com.haili.eclat.Eclat;
import com.haili.eclat.ItemSet;
import com.haili.fp2.FPGrowth;
import com.haili.scps.SCPSTree;
import com.haili.util.ConfigReader;

/**
 * 滑动窗口模型
 * @author Administrator
 *
 */
public class SW {
	private int initSize; // 初始窗口大小
	private int paneSize; // 一批数据大小
	private double changeTh; // 改变率阈值
	private double minSup; // 最小支持度
	private int CP; // 检查点
	private Map<Set<String>, Integer> lastFPSet = new HashMap<>(); // 上一次挖掘出来的频繁项集
	private String spliter = " "; // 数据集分隔符
	private static Properties prop = new ConfigReader().getProperties(); // 读取配置文件
	
	/**
	 * 构造函数
	 * @param initSize
	 * @param paneSize
	 * @param minSup
	 */
	public SW(int initSize, int paneSize, double minSup, double changeTh) {
		this.initSize = initSize;
		this.paneSize = paneSize;
		this.CP = initSize; // 初始检查点为初始窗口大小
		this.minSup = minSup;
		this.changeTh = changeTh;
	}
	
	public static void main(String[] args) {
		int paneSize = 1000;
		SW vsw = new SW(paneSize * 2, paneSize, 0.2, 0.015);
		
		long begin = System.currentTimeMillis();
		
		vsw.runVSW("mushroom");
//		vsw.runSCPS("mushroom");
//		vsw.runSCPS("mushroom");
		
		long end = System.currentTimeMillis();
		System.out.println(String.format("cost : %dms", end - begin));
		
	}
	
	/**
	 * 检测是否已经发生了概念改变
	 */
	public boolean isConceptChange(Map<Set<String>, Integer> curFPSet) {
		
		// curFPSet - lastFPSet
		Set<Set<String>> F1 = getDifferenceSet(curFPSet.keySet(), lastFPSet.keySet());
		int F1Num = F1.size();
		
		// lastFPSet - curFPSet
		Set<Set<String>> F2 = getDifferenceSet(lastFPSet.keySet(), curFPSet.keySet());
		int F2Num = F2.size();
		
		int FNum = lastFPSet.size();
		
		double result = FNum == 0 ? 0 : (double)(F1Num + F2Num) / (FNum + F1Num);
		System.out.println("change ratio : " + result);
		return result > changeTh;
	}
	
	/**
	 * 读取数据流
	 */
	public void runVSW(String dataset) {
		System.out.println("runing..., init_size : " + initSize + ", pane_size: " + paneSize + ", min_sup : " + minSup);
		
		String filePath = prop.getProperty("datasetPath") + prop.getProperty(dataset);
//		String filePath = prop.getProperty("datasetPath") + System.getProperty("file.separator") + prop.getProperty(dataset);
		FileReader fr;
		int lastCP = 0; // 上一个检查点， 用于vsw算法删除旧窗口
		
		try {
			fr = new FileReader(new File(filePath));

			BufferedReader br = new BufferedReader(fr);

			String line = null;
			int lineNum = 0;
			List<Map<String, Object>> pane = new ArrayList<>(); // 一批数据
			List<Map<String, Object>> window = new ArrayList<>(); // 窗口所有数据

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
				
				// 一批数据满了就开始一系列操作
				if (lineNum % paneSize == 0) {
					
					window.addAll(pane); // 一批数据插入到窗口中
					pane.clear(); // 重置pane
					
					// 窗口初始化时不进行挖掘
					if (lineNum == initSize) {
						Eclat eclat = new Eclat(window, minSup);
						eclat.mine();
						lastFPSet = eclat.getFP();
						
					} else if (lineNum > initSize) {

	//					T.insertPane(pane, CP);
						Eclat eclat = new Eclat(window, minSup);
						eclat.mine();
						Map<Set<String>, Integer> curFPSet = eclat.getFP();
						
						// 检测到概念漂移的发生
						if (isConceptChange(curFPSet)) {
							// 更新上一次挖掘的频繁项集
							lastFPSet = curFPSet;
							
							// 删除检查点之前的窗口数据
							for (int i = 0; i < (CP - lastCP); i++) {
								window.remove(0);
							}
							
							lastCP = CP;
							// 更新检查点
							CP = lineNum;
							System.out.println(String.format("CP : %d, last CP : %d, window size : %d", CP, lastCP, window.size()));
							
						} 
					}
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println();
		SW.writeResult(lastFPSet, "vsw", dataset, minSup);
	}
	
	public void runSCPS(String dataset) {

		System.out.println("runing..., init_size : " + initSize + ", pane_size: " + paneSize + ", min_sup : " + minSup);
		
		String filePath = prop.getProperty("datasetPath") + prop.getProperty(dataset);
//		String filePath = prop.getProperty("datasetPath") + System.getProperty("file.separator") + prop.getProperty(dataset);
		FileReader fr;
		SCPSTree T = new SCPSTree(minSup); // SCPS 树
		
		try {
			fr = new FileReader(new File(filePath));

			BufferedReader br = new BufferedReader(fr);

			String line = null;
			int lineNum = 0;
			List<Map<String, Object>> pane = new ArrayList<>(); // 一批数据

			while ((line = br.readLine()) != null) {
				
				if (line.trim() != "") {
					Map<String, Object> transaction = new HashMap<>();
					transaction.put("tid", ++lineNum); // 添加行号
					List<String> record = new ArrayList<String>();
					
					String[] items = line.split(spliter);
					for (String item : items) {
						record.add(item);
					}
					transaction.put("record", record);
					pane.add(transaction);
				}
				
				// 开始一些列操作
				if (lineNum % paneSize == 0) {
					
					T.insertPane(pane, CP);
					pane.clear(); // 重置pane
					
					// 窗口初始化时不进行挖掘
					if (lineNum == initSize) {
						int minSN = (int) Math.ceil(this.minSup * T.getCurrentWindowSize());
						FPGrowth fpgrowth = new FPGrowth(T, minSup, minSN, dataset);
						fpgrowth.mine();
						lastFPSet = fpgrowth.getFP();
						
					} else if (lineNum > initSize) {
						int minSN = (int) Math.ceil(this.minSup * T.getCurrentWindowSize());
						FPGrowth fpgrowth = new FPGrowth(T, minSup, minSN, dataset);
						fpgrowth.mine();
						Map<Set<String>, Integer> curFPSet = fpgrowth.getFP();
						
						// 检测到概念漂移的发生
						if (isConceptChange(curFPSet)) {
							// 更新上一次挖掘的频繁项集
							lastFPSet = curFPSet;
							
							// 删除检查点之前的窗口数据
							T.removeStaleWindow(T.getRoot());
							// 更新检查点
							CP = lineNum;
//							System.out.println(String.format("CP : %d, window size : %d", CP, T.getCurrentWindowSize()));
							
						} 
					}
					
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println();
	
	}
	
	
	
	/**
	 * 结果写入文件
	 * @param FP
	 */
	public static void writeResult(Map<Set<String>, Integer> FP, String algorithm, String dataset, double minSup) {
		
		String outputPath = prop.getProperty("outputPath") + algorithm + "\\";
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd hhmmss");
		
		String tempFileName = String.format("%s[%1.3f](%s).dat", dataset, minSup, sdf.format(new Date()));
		String tempFilePath = outputPath + tempFileName;
		
		// create dir
        File dir = new File(outputPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
        
        // create temp file
        File tempFile = new File(tempFilePath);
        if (tempFile.exists()) {
            tempFile.delete();
        }
        BufferedWriter bw = null;
        
        try {
        	bw = new BufferedWriter(new FileWriter(tempFile));
	        for (Set<String> set : FP.keySet()) {
	        	int v = FP.get(set);
	        	StringBuilder sb = new StringBuilder();
	        	
	        	for (String item : set) {
					sb.append(item + " ");
				}
	        	
	        	sb.append(": " + v);
				sb.append("\n");
				bw.write(sb.toString());
			}
	        bw.write("total : " + FP.size());
	        
	        bw.flush();
        } catch (IOException e) {
        	e.printStackTrace();
        } finally {
        	try {
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
		
	}
	
	/**
	 * 把频繁项集FP转为单个集合
	 * cofi 算法需要如此
	 * 
	 * @return
	 */
	public static Set<String> getSets(Map<Set<String>, Integer> FP) {
		Set<String> result = new HashSet<>();
		for (Set<String> set : FP.keySet()) {
			result.addAll(set);
		}
		return null;
	}

	/**
	 * 求交集
	 * 
	 * @return
	 */
	public static Set getIntersectionSet(Set set1, Set set2) {
		Set result = new HashSet<>();
		result.addAll(set1);
		result.retainAll(set2);
		return result;
	}

	/**
	 * 求并集
	 * 
	 * @return
	 */
	public static Set getUnionSet(Set set1, Set set2) {
		Set result = new HashSet<>();
		result.addAll(set1);
		result.addAll(set2);
		return result;
	}

	/**
	 * 求差集
	 * set1 - set2
	 * @return
	 */
	public static Set getDifferenceSet(Set set1, Set set2) {
		Set result = new HashSet<>();
		result.addAll(set1);
		result.removeAll(set2);
		return result;
	}
}
