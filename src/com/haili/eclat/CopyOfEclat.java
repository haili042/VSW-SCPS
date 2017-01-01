package com.haili.eclat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

public class CopyOfEclat {

	private String srcPath = "dataset/statical/test.dat";
	private String destPath = "result/eclat";
	private File file = new File(srcPath);

	private double minSup;
	private int transNum = 0;
	private ArrayList<HeadNode> array = new ArrayList<HeadNode>();
	private HashHeadNode[] hashTable;// 存放临时生成的频繁项集，作为重复查询的备选集合
	public long newItemNum = 0;

	private File tempFile = null;
	private BufferedWriter bw = null;

	
	private Map<String, Integer> keyMap = new TreeMap<>(); // 键=>值
	private Map<Integer, String> valMap = new TreeMap<>(); // 值=>键
	
	public static long modSum = 0;
	
	
	private Map<Set<String>, Integer> FP = new LinkedHashMap<>(); // 频繁项集， 键是项集， 值是计数
	
	List<Map<String, Object>> dataList; // 窗口内的数据

	public CopyOfEclat(List<Map<String, Object>> dataList, double minSup) {
		this.dataList = dataList;
		this.minSup = minSup;
	}
	
	public CopyOfEclat(double minSup) {
		this.dataList = dataList;
		this.minSup = minSup;
	}
	


	/**
	 * 第一遍扫描数据库，确定Itemset,根据阈值计算出支持度数
	 */
	public void init() {
		Set<String> itemSet = new TreeSet();
//		MyMap<Integer, Integer> itemMap = new MyMap<Integer, Integer>();
		Map<String, Integer> itemMap = new TreeMap<>();

		int itemNum = 0;
		Set[][] a;

		try {
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String str = null;

			// 第一次扫描数据集合
			while ((str = br.readLine()) != null) {
				transNum++;
				String[] line = str.split(" ");
				for (int i = 0; i < line.length; i++) {
					String item = line[i];
					
					itemSet.add(item);
					if (itemMap.containsKey(item)) {
						itemMap.put(item, itemMap.get(item) + 1);
					} else {
						itemMap.put(item, 1);
					}
					
				}
			}
			br.close();
			
			int keycount = 0;
			for (String k : itemMap.keySet()) {
				++keycount;
				keyMap.put(k, keycount);
				valMap.put(keycount, k);
			}
			
			// System.out.println("itemMap lastKey:"+itemMap.lastKey());
			// System.out.println("itemsize:"+itemSet.size());
			// System.out.println("trans: "+transNum);
			// ItemSet.limitSupport=(int)Math.ceil(transNum*limitValue);//上取整
			ItemSet.minSN = (int) Math.ceil(transNum * minSup);// 下取整
			ItemSet.ItemSize = keyMap.size();
			ItemSet.TransSize = transNum;
			hashTable = new HashHeadNode[ItemSet.ItemSize * 3];// 生成项集hash表
			for (int i = 0; i < hashTable.length; i++) {
				hashTable[i] = new HashHeadNode();
			}

			// System.out.println("limitSupport:"+ItemSet.limitSupport);

            SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd hhmmss");
            String time = "[" + minSup + "](" + sdf.format(new Date()) + ")";
            File dir = new File(destPath);
            if (!dir.exists()) {
                dir.mkdir();
            }
            tempFile = new File(destPath + "/" + file.getName().replaceAll("(?=\\.)", time));
            if (tempFile.exists()) {
                tempFile.delete();
            }
//            tempFile.createNewFile();

			bw = new BufferedWriter(new FileWriter(tempFile));

			Set oneItem = itemMap.keySet();
			int countOneItem = 0;
			for (Iterator it = oneItem.iterator(); it.hasNext();) {
				String key = (String) it.next();
				int value = (Integer) itemMap.get(key);
				if (value >= ItemSet.minSN) {
					Set<String> set = new HashSet<>();
					set.add(key);
					FP.put(set, value);
					
					bw.write(key + " " + ":" + " " + value);
					bw.write("\n");
					countOneItem++;
				}
			}
			bw.flush();
			modSum += countOneItem;

			itemNum = keyMap.size();

			a = new TreeSet[itemNum + 1][itemNum + 1];
			array.add(new HeadNode());// 空项

			for (short i = 1; i <= itemNum; i++) {
				HeadNode hn = new HeadNode();
				// hn.item=i;
				array.add(hn);
			}

			BufferedReader br2 = new BufferedReader(new FileReader(file));

			// 第二次扫描数据集合,形成2-项候选集
			int counter = 0;// 事务
			int max = 0;
			while ((str = br2.readLine()) != null) {
				max++;
				String[] line = str.split(" ");
				counter++;
				for (int i = 0; i < line.length; i++) {
					
					int sOne = keyMap.get(line[i]);
//                    int sOne = Integer.parseInt(line[i]);

					
					for (int j = i + 1; j < line.length; j++) {
						int sTwo = keyMap.get(line[j]);
						if (a[sOne][sTwo] == null) {
							Set set = new TreeSet();
							set.add(counter);
							a[sOne][sTwo] = set;
						} else {
							a[sOne][sTwo].add(counter);

						}
					}
				}
			}
			// 将数组集合转换为链表集合

			for (int i = 1; i <= itemNum; i++) {
				HeadNode hn = array.get(i);
				for (int j = i + 1; j <= itemNum; j++) {
					if (a[i][j] != null
							&& a[i][j].size() >= ItemSet.minSN) {
						hn.items++;
						ItemSet is = new ItemSet(true);
						is.item = 2;
						is.items.set(i);
						is.items.set(j);
						is.supports = a[i][j].size();
						
						Set<String> set = new HashSet<>();
						set.add(valMap.get(i));
						set.add(valMap.get(j));
						FP.put(set, is.supports);
						
						bw.write(valMap.get(i) + " " + valMap.get(j) + " " + ": " + is.supports);
						bw.write("\n");
						// 统计频繁2-项集的个数
						modSum++;
						for (Iterator it = a[i][j].iterator(); it.hasNext();) {
							int value = (Integer) it.next();
							is.trans.set(value);
						}
						if (hn.first == null) {
							hn.first = is;
							hn.last = is;
						} else {
							hn.last.next = is;
							hn.last = is;
						}
					}
				}
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void start() {
		boolean flag = true;
		// TreeSet ts=new TreeSet();//临时存储项目集合，防止重复项集出现，节省空间

		int count = 0;

		ItemSet shareFirst = new ItemSet(false);

		while (flag) {
			flag = false;
			// System.out.println(++count);
			for (int i = 1; i < array.size(); i++) {
				HeadNode hn = array.get(i);

				if (hn.items > 1)// 项集个数大于1
				{
					generateLargeItemSet(hn, shareFirst);
					flag = true;

				}
				clear(hashTable);
			}

		}
		try {
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void generateLargeItemSet(HeadNode hn, ItemSet shareFirst) {

		BitSet bsItems = new BitSet(ItemSet.ItemSize);// 存放链两个k-1频繁项集的ItemSet交
		BitSet bsTrans = new BitSet(ItemSet.TransSize);// 存放两个k-1频繁项集的Trans交
		BitSet containItems = new BitSet(ItemSet.ItemSize);// 存放两个k-1频繁项集的ItemSet的并
		BitSet bsItems2 = new BitSet(ItemSet.ItemSize);// 临时存放容器BitSet

		ItemSet oldCurrent = null, oldNext = null;
		oldCurrent = hn.first;
		long countItems = 0;

		ItemSet newFirst = new ItemSet(false), newLast = newFirst;
		while (oldCurrent != null) {
			oldNext = oldCurrent.next;
			while (oldNext != null) {
				// 生成k—项候选集，由两个k-1项频繁集生成
				bsItems.clear();
				bsItems.or(oldCurrent.items);
				bsItems.and(oldNext.items);

				if (bsItems.cardinality() < oldCurrent.item - 1) {
					break;
				}
				// 新合并的项集是否已经存在

				containItems.clear();
				containItems.or(oldCurrent.items);// 将k-1项集合并
				containItems.or(oldNext.items);

				if (!containItems(containItems, bsItems2, newFirst)) {

					bsTrans.clear();
					bsTrans.or(oldCurrent.trans);
					bsTrans.and(oldNext.trans);
					if (bsTrans.cardinality() >= ItemSet.minSN) {
						ItemSet is = null;

						if (shareFirst.next == null)// 没有共享ItemSet链表
						{
							is = new ItemSet(true);
							newItemNum++;
						} else {
							is = shareFirst.next;
							shareFirst.next = shareFirst.next.next;

							is.items.clear();
							is.trans.clear();
							is.next = null;

						}
						is.item = (oldCurrent.item + 1);// 生成k—项候选集，由两个k-1项频繁集生成

						is.items.or(oldCurrent.items);// 将k-1项集合并
						is.items.or(oldNext.items);// 将k-1项集合并

						is.trans.or(oldCurrent.trans);// 将bs1的值复制到bs中
						is.trans.and(oldNext.trans);

						is.supports = is.trans.cardinality();

						writeToFile(is.items, is.supports);// 将频繁项集及其支持度写入文件
						countItems++;

						modSum++;
						newLast.next = is;
						newLast = is;

					}
				}
				oldNext = oldNext.next;
			}
			oldCurrent = oldCurrent.next;
		}

		ItemSet temp1 = hn.first;
		ItemSet temp2 = hn.last;

		temp2.next = shareFirst.next;
		shareFirst.next = temp1;

		hn.first = newFirst.next;
		hn.last = newLast;
		hn.items = countItems;

	}

	public boolean containItems(BitSet containItems, BitSet bsItems2,
			ItemSet first) {
		long size = containItems.cardinality();// 项集数目

		int itemSum = 0;
		int temp = containItems.nextSetBit(0);
		while (true) {
			itemSum += temp;
			temp = containItems.nextSetBit(temp + 1);
			if (temp == -1) {
				break;
			}
		}

		int hash = itemSum % (ItemSet.ItemSize * 3);

		HashNode hn = hashTable[hash].next;
		Node pre = hashTable[hash];
		while (true) {
			if (hn == null)// 不包含containItems
			{
				HashNode node = new HashNode();
				node.bs.or(containItems);

				pre.next = node;

				return false;
			}
			if (hn.bs.isEmpty()) {
				hn.bs.or(containItems);

				return false;
			}

			bsItems2.clear();
			bsItems2.or(containItems);
			bsItems2.and(hn.bs);

			if (bsItems2.cardinality() == size) {
				return true;
			}
			pre = hn;
			hn = hn.next;
		}

	}

	public void clear(HashHeadNode[] hashTable) {
		for (int i = 0; i < hashTable.length; i++) {
			HashNode node = hashTable[i].next;
			while (node != null) {
				node.bs.clear();
				node = node.next;
			}
		}
	}

	public void writeToFile(BitSet items, int supports) {
		StringBuilder sb = new StringBuilder();
		// sb.append("<");
		int temp = items.nextSetBit(0);
		sb.append(valMap.get(temp));
		
		Set<String> set = new HashSet<>();
		set.add(valMap.get(temp));
		
		while (true) {
			temp = items.nextSetBit(temp + 1);
			if (temp == -1) {
				break;
			}
			// sb.append(",");
			set.add(valMap.get(temp));
			sb.append(" ");
			sb.append(valMap.get(temp));
		}
		sb.append(" :" + " " + supports);
		FP.put(set, supports);
		try {
			bw.write(sb.toString());
			bw.write("\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Map<Set<String>, Integer> getFP() {
		return FP;
	}
	
	
	public static void main(String[] args) {
		CopyOfEclat e = new CopyOfEclat(0.2);
		long begin = System.currentTimeMillis();
		e.init();
		e.start();
		long end = System.currentTimeMillis();

		double time = (double) (end - begin) / 1000;
		System.out.println("共耗时" + time + "秒");
		System.out.println("频繁模式数目:" + CopyOfEclat.modSum);
	}
}

