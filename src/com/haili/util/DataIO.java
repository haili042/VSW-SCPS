package com.haili.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataIO extends Thread {

	private String fileName;
	private static Map<String, String> dataSets = new HashMap<>();
	private static Map<String, String> output = new HashMap<>();
	private static List<Set<String>> records = new ArrayList<Set<String>>();

	{
		dataSets.put("accidents", "dataset/statical/accidents.dat");
		dataSets.put("mushroom", "dataset/statical/mushroom.dat");
		dataSets.put("T10I4D100K", "dataset/statical/T10I4D100K.dat");
		dataSets.put("T40I10D100K", "dataset/statical/T40I10D100K.dat");
		dataSets.put("test", "dataset/statical/test.dat");
	}

	// ���캯��
	public DataIO(String fileName) {
		this.fileName = fileName;
	}
	
	

	// ����̬���ݿ�
	public List<Map<String, Object>> readData() {
		List<Map<String, Object>> records = new ArrayList<>();
		try {
			FileReader fr = new FileReader(new File(dataSets.get(fileName)));
			BufferedReader br = new BufferedReader(fr);

			String line = null;
			int lineNum = 0;
			while ((line = br.readLine()) != null) {
				if (line.trim() != "") {
					HashMap<String, Object> transaction = new HashMap<>();
					
					List<String> record = new ArrayList<>();
					String[] items = line.split(" ");
					
					for (String item : items) {
						record.add(item);
					}
					transaction.put("tid", lineNum++);
					transaction.put("record", record);
					records.add(transaction);
				}
			}
		} catch (IOException e) {
			System.out.println("��ȡ�����ļ�ʧ�ܡ�");
		}
		return records;
	}

	// д�����ļ�
	public void wirte() {
		File destDir = new File("result/apriori");
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd hhmmss");
		String time = "(" + sdf.format(new Date()) + ")";

		// String targetFile = "result/apriori/" +
		// shortFileName.replaceAll("(?=\\.)", time);
	}

	/**
	 * �����̣߳���ȡ������
	 */
	@Override
	public void run() {

		FileReader fr;
		
		try {
			fr = new FileReader(new File(dataSets.get(fileName)));

			BufferedReader br = new BufferedReader(fr);

			String line = null;

			while ((line = br.readLine()) != null) {
				int random = (int) (Math.random() * 100); // �����ģ���������ĵ���

				if (line.trim() != "") {
					Set<String> record = new HashSet<String>();
					String[] items = line.split(" ");
					for (String item : items) {
						record.add(item);
					}
					records.add(record);
				}

				System.out.println("interval: " + random + ", line: " + line);

				try {
					Thread.sleep(random); // ����ӳ���ģ��������
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
