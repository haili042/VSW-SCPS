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
	 * ���캯��1
	 * @param filePath	�ļ�·��
	 */
	public FileReader(String filePath) {
		this.filePath = filePath;
	}
	
	/**
	 * ���캯��2
	 * @param filePath	�ļ�·��
	 * @param spliter	�ָ���, Ĭ�� " "
	 */
	public FileReader(String filePath, String spliter) {
		this.filePath = filePath;
		this.spliter = spliter;
	}
	
	/**
	 * ��ȡ���ݼ�, ���� database
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
	 * ��ȡ��������
	 * @return
	 */
	public LinkedList<LinkedList<String>> getSW(int windowSize) throws IOException {
		// TODO
		
		return null;
	}
	
}
