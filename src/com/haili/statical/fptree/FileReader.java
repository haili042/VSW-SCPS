package com.haili.statical.fptree;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class FileReader {
	
	protected String filePath; 
	protected String spliter = " "; 
	protected LinkedList<LinkedList<String>> database;
	
	/**
	 * 构造函数1
	 * @param filePath	文件路径
	 */
	public FileReader(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * 构造函数2
	 * @param filePath	文件路径
	 * @param spliter	分隔符, 默认 " "
	 */
	public FileReader(String filePath, String spliter) {
		this.filePath = filePath;
		this.spliter = spliter;
	}
	
	/**
	 * 读取数据集, 生成 database
	 * @return	
	 * @throws IOException
	 */
	public LinkedList<LinkedList<String>> getDB() throws IOException {
		LinkedList<LinkedList<String>> database = new LinkedList<LinkedList<String>>();
		
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(filePath)));
		
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			if (line.length() == 0 || "".equals(line))
				continue;
			String[] str = line.split(spliter);
			LinkedList<String> transition = new LinkedList<String>();
			for (int i = 0; i < str.length; i++) {
				transition.add(str[i].trim());
			}
			database.add(transition);
		}
		br.close();
		
		return database;
	}
	
	/**
	 * 获取滑动窗口
	 * @return
	 */
	public LinkedList<LinkedList<String>> getSW(int windowSize) throws IOException {
		// TODO
		
		return null;
	}
	
}
