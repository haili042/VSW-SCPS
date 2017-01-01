package com.haili.eclat;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import com.haili.mine.MiningFP;

public class Eclat implements MiningFP {

	private double minSup; // 最小支持度
	private ArrayList<HeadNode> array = new ArrayList<HeadNode>();
	private HashHeadNode[] hashTable;// 存放临时生成的频繁项集，作为重复查询的备选集合
	private Map<String, Integer> keyMap = new TreeMap<>(); // 键=>值
	private Map<Integer, String> valMap = new TreeMap<>(); // 值=>键
	private Map<Set<String>, Integer> FP = new LinkedHashMap<>(); // 频繁项集， 键是项集， 值是计数
	List<Map<String, Object>> dataList; // 窗口内所有数据

	/**
	 * 构造函数
	 * 
	 * @param dataList
	 *            窗口内所有数据
	 * @param minSup
	 *            最小支持度
	 */
	public Eclat(List<Map<String, Object>> dataList, double minSup) {
		this.dataList = dataList;
		this.minSup = minSup;
	}

	/**
	 * 开始挖掘
	 */
	public void mine() {
		init();
		start();
		System.out.println("fp size : " + FP.size() + ", window size : " + dataList.size());
	}

	/**
	 * 第一遍扫描数据库，确定Itemset,根据阈值计算出支持度数
	 */
	public void init() {
		Set<String> itemSet = new TreeSet<>();
		// MyMap<Integer, Integer> itemMap = new MyMap<Integer, Integer>();
		Map<String, Integer> itemMap = new TreeMap<>();

		int itemNum = 0;
		Set[][] a;

		String str = null;

		// 第一次扫描数据集合
		for (Map<String, Object> line : dataList) {

			// int tid = (int) line.get("tid");
			List<String> record = (List<String>) line.get("record");

			for (String item : record) {

				itemSet.add(item);
				if (itemMap.containsKey(item)) {
					itemMap.put(item, itemMap.get(item) + 1);
				} else {
					itemMap.put(item, 1);
				}
			}
		}

		int keycount = 0;
		for (String k : itemMap.keySet()) {
			++keycount;
			keyMap.put(k, keycount);
			valMap.put(keycount, k);
		}

		// ItemSet.limitSupport=(int)Math.ceil(transNum*limitValue);//上取整

		ItemSet.minSN = (int) Math.ceil(dataList.size() * minSup);// 下取整
		ItemSet.ItemSize = keyMap.size();
		ItemSet.TransSize = dataList.size();
		hashTable = new HashHeadNode[ItemSet.ItemSize * 3];// 生成项集hash表
		for (int i = 0; i < hashTable.length; i++) {
			hashTable[i] = new HashHeadNode();
		}

		// System.out.println("limitSupport:"+ItemSet.limitSupport);

		for (String key : itemMap.keySet()) {
			int value = itemMap.get(key);
			if (value >= ItemSet.minSN) {
				Set<String> set = new HashSet<>();
				set.add(key);
				FP.put(set, value);
			}
		}

		itemNum = keyMap.size();
		a = new TreeSet[itemNum + 1][itemNum + 1];
		array.add(new HeadNode());// 空项

		for (short i = 1; i <= itemNum; i++) {
			HeadNode hn = new HeadNode();
			// hn.item=i;
			array.add(hn);
		}

		// 第二次扫描数据集合,形成2-项候选集
		int counter = 0;// 事务

		for (Map<String, Object> transaction : dataList) {
			counter++;
			int tid = (int) transaction.get("tid");
			List<String> line = (List<String>) transaction.get("record");

			for (int i = 0; i < line.size(); i++) {

				int sOne = keyMap.get(line.get(i));

				for (int j = i + 1; j < line.size(); j++) {
					int sTwo = keyMap.get(line.get(j));
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
				if (a[i][j] != null && a[i][j].size() >= ItemSet.minSN) {
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

					// 统计频繁2-项集的个数
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
	}

	/**
	 * 生成最大项集
	 * 
	 * @param hn
	 * @param shareFirst
	 */
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

						addToFP(is.items, is.supports);// 将频繁项集及其支持度写入文件
						countItems++;

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

	/**
	 * 是否包含项
	 * 
	 * @param containItems
	 * @param bsItems2
	 * @param first
	 * @return
	 */
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

	/**
	 * 添加到FP中
	 * 
	 * @param items
	 * @param supports
	 */
	public void addToFP(BitSet items, int supports) {
		// sb.append("<");
		int temp = items.nextSetBit(0);

		Set<String> set = new HashSet<>();
		set.add(valMap.get(temp));

		while (true) {
			temp = items.nextSetBit(temp + 1);
			if (temp == -1) {
				break;
			}
			set.add(valMap.get(temp));
		}
		FP.put(set, supports);
	}

	/****************** getters and setters **********************/

	public Map<Set<String>, Integer> getFP() {
		return FP;
	}

}
