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
 * ��������ģ��
 * @author Administrator
 *
 */
public class SW {
	private int initSize; // ��ʼ���ڴ�С
	private int paneSize; // һ�����ݴ�С
	private double changeTh; // �ı�����ֵ
	private double minSup; // ��С֧�ֶ�
	private int CP; // ����
	private Map<Set<String>, Integer> lastFPSet = new HashMap<>(); // ��һ���ھ������Ƶ���
	private String spliter = " "; // ���ݼ��ָ���
	private static Properties prop = new ConfigReader().getProperties(); // ��ȡ�����ļ�
	
	/**
	 * ���캯��
	 * @param initSize
	 * @param paneSize
	 * @param minSup
	 */
	public SW(int initSize, int paneSize, double minSup, double changeTh) {
		this.initSize = initSize;
		this.paneSize = paneSize;
		this.CP = initSize; // ��ʼ����Ϊ��ʼ���ڴ�С
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
	 * ����Ƿ��Ѿ������˸���ı�
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
	 * ��ȡ������
	 */
	public void runVSW(String dataset) {
		System.out.println("runing..., init_size : " + initSize + ", pane_size: " + paneSize + ", min_sup : " + minSup);
		
		String filePath = prop.getProperty("datasetPath") + prop.getProperty(dataset);
//		String filePath = prop.getProperty("datasetPath") + System.getProperty("file.separator") + prop.getProperty(dataset);
		FileReader fr;
		int lastCP = 0; // ��һ�����㣬 ����vsw�㷨ɾ���ɴ���
		
		try {
			fr = new FileReader(new File(filePath));

			BufferedReader br = new BufferedReader(fr);

			String line = null;
			int lineNum = 0;
			List<Map<String, Object>> pane = new ArrayList<>(); // һ������
			List<Map<String, Object>> window = new ArrayList<>(); // ������������

			while ((line = br.readLine()) != null) {
				
				if (line.trim() != "") {
					Map<String, Object> transaction = new HashMap<>();
					transaction.put("tid", lineNum); // ����к�
					List<String> record = new ArrayList<String>();
					
					String[] items = line.split(spliter);
					for (String item : items) {
						record.add(item);
					}
					transaction.put("record", record);
					pane.add(transaction);
				}
				
				++lineNum;
				
				// һ���������˾Ϳ�ʼһϵ�в���
				if (lineNum % paneSize == 0) {
					
					window.addAll(pane); // һ�����ݲ��뵽������
					pane.clear(); // ����pane
					
					// ���ڳ�ʼ��ʱ�������ھ�
					if (lineNum == initSize) {
						Eclat eclat = new Eclat(window, minSup);
						eclat.mine();
						lastFPSet = eclat.getFP();
						
					} else if (lineNum > initSize) {

	//					T.insertPane(pane, CP);
						Eclat eclat = new Eclat(window, minSup);
						eclat.mine();
						Map<Set<String>, Integer> curFPSet = eclat.getFP();
						
						// ��⵽����Ư�Ƶķ���
						if (isConceptChange(curFPSet)) {
							// ������һ���ھ��Ƶ���
							lastFPSet = curFPSet;
							
							// ɾ������֮ǰ�Ĵ�������
							for (int i = 0; i < (CP - lastCP); i++) {
								window.remove(0);
							}
							
							lastCP = CP;
							// ���¼���
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
		SCPSTree T = new SCPSTree(minSup); // SCPS ��
		
		try {
			fr = new FileReader(new File(filePath));

			BufferedReader br = new BufferedReader(fr);

			String line = null;
			int lineNum = 0;
			List<Map<String, Object>> pane = new ArrayList<>(); // һ������

			while ((line = br.readLine()) != null) {
				
				if (line.trim() != "") {
					Map<String, Object> transaction = new HashMap<>();
					transaction.put("tid", ++lineNum); // ����к�
					List<String> record = new ArrayList<String>();
					
					String[] items = line.split(spliter);
					for (String item : items) {
						record.add(item);
					}
					transaction.put("record", record);
					pane.add(transaction);
				}
				
				// ��ʼһЩ�в���
				if (lineNum % paneSize == 0) {
					
					T.insertPane(pane, CP);
					pane.clear(); // ����pane
					
					// ���ڳ�ʼ��ʱ�������ھ�
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
						
						// ��⵽����Ư�Ƶķ���
						if (isConceptChange(curFPSet)) {
							// ������һ���ھ��Ƶ���
							lastFPSet = curFPSet;
							
							// ɾ������֮ǰ�Ĵ�������
							T.removeStaleWindow(T.getRoot());
							// ���¼���
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
	 * ���д���ļ�
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
	 * ��Ƶ���FPתΪ��������
	 * cofi �㷨��Ҫ���
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
	 * �󽻼�
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
	 * �󲢼�
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
	 * ��
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
