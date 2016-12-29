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

	private double minSup; // ��С֧�ֶ�
	private ArrayList<HeadNode> array = new ArrayList<HeadNode>();
	private HashHeadNode[] hashTable;// �����ʱ���ɵ�Ƶ�������Ϊ�ظ���ѯ�ı�ѡ����
	private Map<String, Integer> keyMap = new TreeMap<>(); // ��=>ֵ
	private Map<Integer, String> valMap = new TreeMap<>(); // ֵ=>��
	private Map<Set<String>, Integer> FP = new LinkedHashMap<>(); // Ƶ����� ������� ֵ�Ǽ���
	List<Map<String, Object>> dataList; // ��������������

	/**
	 * ���캯��
	 * 
	 * @param dataList
	 *            ��������������
	 * @param minSup
	 *            ��С֧�ֶ�
	 */
	public Eclat(List<Map<String, Object>> dataList, double minSup) {
		this.dataList = dataList;
		this.minSup = minSup;
	}

	/**
	 * ��ʼ�ھ�
	 */
	public void mine() {
		init();
		start();
		System.out.println("fp size : " + FP.size() + ", window size : " + dataList.size());
	}

	/**
	 * ��һ��ɨ�����ݿ⣬ȷ��Itemset,������ֵ�����֧�ֶ���
	 */
	public void init() {
		Set<String> itemSet = new TreeSet<>();
		// MyMap<Integer, Integer> itemMap = new MyMap<Integer, Integer>();
		Map<String, Integer> itemMap = new TreeMap<>();

		int itemNum = 0;
		Set[][] a;

		String str = null;

		// ��һ��ɨ�����ݼ���
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

		// ItemSet.limitSupport=(int)Math.ceil(transNum*limitValue);//��ȡ��

		ItemSet.minSN = (int) Math.floor(dataList.size() * minSup);// ��ȡ��
		ItemSet.ItemSize = keyMap.size();
		ItemSet.TransSize = dataList.size();
		hashTable = new HashHeadNode[ItemSet.ItemSize * 3];// �����hash��
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
		array.add(new HeadNode());// ����

		for (short i = 1; i <= itemNum; i++) {
			HeadNode hn = new HeadNode();
			// hn.item=i;
			array.add(hn);
		}

		// �ڶ���ɨ�����ݼ���,�γ�2-���ѡ��
		int counter = 0;// ����

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

		// �����鼯��ת��Ϊ������
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

					// ͳ��Ƶ��2-��ĸ���
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
		// TreeSet ts=new TreeSet();//��ʱ�洢��Ŀ���ϣ���ֹ�ظ�����֣���ʡ�ռ�

		int count = 0;

		ItemSet shareFirst = new ItemSet(false);

		while (flag) {
			flag = false;
			// System.out.println(++count);
			for (int i = 1; i < array.size(); i++) {
				HeadNode hn = array.get(i);

				if (hn.items > 1)// ���������1
				{
					generateLargeItemSet(hn, shareFirst);
					flag = true;

				}
				clear(hashTable);
			}

		}
	}

	/**
	 * ��������
	 * 
	 * @param hn
	 * @param shareFirst
	 */
	public void generateLargeItemSet(HeadNode hn, ItemSet shareFirst) {

		BitSet bsItems = new BitSet(ItemSet.ItemSize);// ���������k-1Ƶ�����ItemSet��
		BitSet bsTrans = new BitSet(ItemSet.TransSize);// �������k-1Ƶ�����Trans��
		BitSet containItems = new BitSet(ItemSet.ItemSize);// �������k-1Ƶ�����ItemSet�Ĳ�
		BitSet bsItems2 = new BitSet(ItemSet.ItemSize);// ��ʱ�������BitSet

		ItemSet oldCurrent = null, oldNext = null;
		oldCurrent = hn.first;
		long countItems = 0;

		ItemSet newFirst = new ItemSet(false), newLast = newFirst;
		while (oldCurrent != null) {
			oldNext = oldCurrent.next;
			while (oldNext != null) {
				// ����k�����ѡ����������k-1��Ƶ��������
				bsItems.clear();
				bsItems.or(oldCurrent.items);
				bsItems.and(oldNext.items);

				if (bsItems.cardinality() < oldCurrent.item - 1) {
					break;
				}
				// �ºϲ�����Ƿ��Ѿ�����

				containItems.clear();
				containItems.or(oldCurrent.items);// ��k-1��ϲ�
				containItems.or(oldNext.items);

				if (!containItems(containItems, bsItems2, newFirst)) {

					bsTrans.clear();
					bsTrans.or(oldCurrent.trans);
					bsTrans.and(oldNext.trans);
					if (bsTrans.cardinality() >= ItemSet.minSN) {
						ItemSet is = null;

						if (shareFirst.next == null)// û�й���ItemSet����
						{
							is = new ItemSet(true);
						} else {
							is = shareFirst.next;
							shareFirst.next = shareFirst.next.next;

							is.items.clear();
							is.trans.clear();
							is.next = null;

						}
						is.item = (oldCurrent.item + 1);// ����k�����ѡ����������k-1��Ƶ��������

						is.items.or(oldCurrent.items);// ��k-1��ϲ�
						is.items.or(oldNext.items);// ��k-1��ϲ�

						is.trans.or(oldCurrent.trans);// ��bs1��ֵ���Ƶ�bs��
						is.trans.and(oldNext.trans);

						is.supports = is.trans.cardinality();

						addToFP(is.items, is.supports);// ��Ƶ�������֧�ֶ�д���ļ�
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
	 * �Ƿ������
	 * 
	 * @param containItems
	 * @param bsItems2
	 * @param first
	 * @return
	 */
	public boolean containItems(BitSet containItems, BitSet bsItems2,
			ItemSet first) {
		long size = containItems.cardinality();// ���Ŀ

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
			if (hn == null)// ������containItems
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
	 * ��ӵ�FP��
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
