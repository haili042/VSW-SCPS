package com.haili.fp2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import com.haili.mine.MiningFP;
import com.haili.sw.SW;

public class FPGrowth implements MiningFP {

	private int minSN;
	private double minSup;
	private int total = 0;

	FPtreeNode fptree;
	List<FPtreeNode> headerTable = new ArrayList<>(); // headertable 保存每个兄弟节点链表的起始点指针
	Map<Set<String>, Integer> FP = new HashMap<>(); // 频繁项集结果

	public FPGrowth(File file, double minSup, String dataset) throws FileNotFoundException {
		this.minSup = minSup;
		init(file);
		fpgrowth(fptree, new HashSet<String>(), minSN, headerTable);
		SW.writeResult(FP, "fp2", dataset, minSup);
	}
	
	
	@Override
	public void mine() {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 条件模式树
	 * @param conditionalPatternBase
	 * @param conditionalItemsMaptoFrequencies
	 * @param threshold
	 * @param conditional_headerTable
	 * @return
	 */
	private FPtreeNode conditional_fptree_constructor(
			Map<Set<String>, Integer> conditionalPatternBase,
			Map<String, Integer> conditionalItemsMaptoFrequencies,
			int threshold, List<FPtreeNode> conditional_headerTable) {
		// FPTree constructing
		// the null node!
		FPtreeNode conditional_fptree = new FPtreeNode("root");
		conditional_fptree.item = null;
		// remember our transactions here has oredering and non-frequent items
		// for condition items
		for (Set<String> pattern : conditionalPatternBase.keySet()) {
			// adding to tree
			// removing non-frequents and making a vector instead of string
			List<String> pattern_vector = new ArrayList<>();
			
			for (String item : pattern) {
				if (conditionalItemsMaptoFrequencies.get(item) >= threshold) {
					pattern_vector.add(item);
				}
			}
			
			// the insert method
			insert(pattern_vector, conditionalPatternBase.get(pattern),
					conditional_fptree, conditional_headerTable);
			// end of insert method
		}
		return conditional_fptree;
	}

	/**
	 * creating fp tree
	 * @param file
	 * @throws FileNotFoundException
	 */
	private void init(File file) throws FileNotFoundException {
		// preprocessing fields
		Map<String, Integer> itemsMaptoFrequencies = new HashMap<String, Integer>();
		Scanner input = new Scanner(file);
		List<String> sortedItemsbyFrequencies = new LinkedList<>();
		List<String> itemstoRemove = new ArrayList<>();
		// 预处理
		preProcessing(file, itemsMaptoFrequencies, input,
				sortedItemsbyFrequencies, itemstoRemove);
		// 构造fp树
		construct_fpTree(file, itemsMaptoFrequencies, input,
				sortedItemsbyFrequencies, itemstoRemove);

	}

	/**
	 * 预处理
	 * 1 生成 ilist 
	 * 2 找出、删除 ilist 中非频繁项
	 * @param file
	 * @param itemsMaptoFrequencies
	 * @param input
	 * @param sortedItemsbyFrequencies
	 * @param itemstoRemove
	 * @throws FileNotFoundException
	 */
	private void preProcessing(File file,
			Map<String, Integer> itemsMaptoFrequencies, Scanner input,
			List<String> sortedItemsbyFrequencies, List<String> itemstoRemove)
			throws FileNotFoundException {

		// first scan database
		while (input.hasNextLine()) {
			String line = input.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(line);
			total++;
			while (tokenizer.hasMoreTokens()) {
				String temp = tokenizer.nextToken();
				if (itemsMaptoFrequencies.containsKey(temp)) {
					int count = itemsMaptoFrequencies.get(temp);
					itemsMaptoFrequencies.put(temp, count + 1);
				} else {
					itemsMaptoFrequencies.put(temp, 1);
				}
			}
		}
		input.close();
		
		this.minSN = (int) Math.ceil(total * minSup); // 上取整
		// orderiiiiiiiiiiiiiiiiiiiiiiiiiiiing
		// also elimating non-frequents

		// for breakpoint for comparison
		// 对ilist进行频繁降序排序
		// [a, c, d, b, null]
		sortedItemsbyFrequencies.add("root");
		itemsMaptoFrequencies.put("root", 0);
		for (String item : itemsMaptoFrequencies.keySet()) {
			int count = itemsMaptoFrequencies.get(item);
			// System.out.println( count );
			int i = 0;
			for (String listItem : sortedItemsbyFrequencies) {
				if (itemsMaptoFrequencies.get(listItem) < count) {
					sortedItemsbyFrequencies.add(i, item);
					break;
				}
				i++;
			}
		}
		
		// 删除ilist中非频繁项
		// removing non-frequents
		// this pichidegi is for concurrency problem in collection iterators
		for (String listItem : sortedItemsbyFrequencies) {
			if (itemsMaptoFrequencies.get(listItem) < minSN) {
				itemstoRemove.add(listItem);
			}
		}
		
		for (String itemtoRemove : itemstoRemove) {
			sortedItemsbyFrequencies.remove(itemtoRemove);
		}
		// printttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttttt
		// for ( String key : list )
		// System.out.printf( "%-10s%10s\n", key, items.get( key ) );

		// 得到 频繁降序排列的 ilist map
	}

	/**
	 * 构造fp树
	 * @param file
	 * @param itemsMaptoFrequencies
	 * @param input
	 * @param sortedItemsbyFrequencies
	 * @param itemstoRemove
	 * @throws FileNotFoundException
	 */
	private void construct_fpTree(File file,
			Map<String, Integer> itemsMaptoFrequencies, Scanner input,
			List<String> sortedItemsbyFrequencies, List<String> itemstoRemove)
			throws FileNotFoundException {
		// HeaderTable Creation
		// first elements use just as pointers
		for (String itemsforTable : sortedItemsbyFrequencies) {
			headerTable.add(new FPtreeNode(itemsforTable));
		}
		// FPTree constructing
		input = new Scanner(file);
		// the null node!
		fptree = new FPtreeNode("root"); // 创建根节点
		fptree.item = null;
		// ordering frequent items transaction
		while (input.hasNextLine()) {
			String line = input.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(line);
			List<String> transactionSortedbyFrequencies = new ArrayList<>();
			while (tokenizer.hasMoreTokens()) {
				String item = tokenizer.nextToken();
				if (itemstoRemove.contains(item)) {// 删除事务中非频繁项
					continue;
				}
				int index = 0;
				for (String vectorString : transactionSortedbyFrequencies) {
					// some lines of condition is for alphabetically check in
					// equals situatioans 频繁降序排列事务
					if (itemsMaptoFrequencies.get(vectorString) < itemsMaptoFrequencies.get(item)
							|| ((itemsMaptoFrequencies.get(vectorString) == itemsMaptoFrequencies.get(item))
							&& (vectorString.compareToIgnoreCase(item) < 0 ? true : false))) {
						transactionSortedbyFrequencies.add(index, item);
						break;
					}
					index++;
				}
				if (!transactionSortedbyFrequencies.contains(item)) {
					transactionSortedbyFrequencies.add(item);
				}
			}
			
			// adding to tree
			insert(transactionSortedbyFrequencies, fptree, headerTable);
			transactionSortedbyFrequencies.clear();
		}
		// headertable reverse ordering 
		// first calculating item frequencies in tree 更新兄弟节点链表
		for (FPtreeNode item : headerTable) {
			int count = 0;
			FPtreeNode itemtemp = item;
			while (itemtemp.next != null) {
				itemtemp = itemtemp.next;
				count += itemtemp.count;
			}
			item.count = count;
		}
		Comparator c = new sortInFrequency();
		Collections.sort(headerTable, c);// 频繁降序排列头表
		input.close();
	}
	
	/**
	 * 插入有序事务到树中， 递归每一项插入到fp树中
	 * @param sortedTransaction
	 * @param fptree
	 * @param headerTable
	 */
	void insert(List<String> sortedTransaction, FPtreeNode fptree,
			List<FPtreeNode> headerTable) {
		if (sortedTransaction.isEmpty()) {
			return;
		}
		String itemtoAddtotree = sortedTransaction.get(0);
		FPtreeNode newNode = null;
		boolean ifisdone = false; 
		for (FPtreeNode child : fptree.children) {
			if (child.item.equals(itemtoAddtotree)) {
				newNode = child;
				child.count++;
				ifisdone = true;
				break;
			}
		}
		if (!ifisdone) {
			newNode = new FPtreeNode(itemtoAddtotree);
			newNode.count = 1;
			newNode.parent = fptree;
			fptree.children.add(newNode);
			for (FPtreeNode headerPointer : headerTable) {
				if (headerPointer.item.equals(itemtoAddtotree)) {
					while (headerPointer.next != null) {
						headerPointer = headerPointer.next;
					}
					headerPointer.next = newNode;
				}
			}
		}
		sortedTransaction.remove(0);
		insert(sortedTransaction, newNode, headerTable);
	}


	/**
	 * fp growth 算法开始
	 * @param fptree
	 * @param base
	 * @param threshold
	 * @param headerTable
	 * @param fp
	 */
	public void fpgrowth(FPtreeNode fptree, Set<String> base, int threshold,
			List<FPtreeNode> headerTable) {
		for (FPtreeNode iteminTree : headerTable) {
			
			Set<String> currentPattern = new HashSet<>();
			currentPattern.addAll(base);
			if (iteminTree.item != null && !iteminTree.item.equals("root")) { // 判断是不是根节点
				currentPattern.add(iteminTree.item);
			}
			
			int supportofCurrentPattern = 0;
			Map<Set<String>, Integer> conditionalPatternBase = new HashMap<>();
			while (iteminTree.next != null) {
				iteminTree = iteminTree.next;
				supportofCurrentPattern += iteminTree.count;
				Set<String> conditionalPattern = new HashSet<>();
				FPtreeNode conditionalItem = iteminTree.parent;

				while (!conditionalItem.isRoot()) {
					conditionalPattern.add(conditionalItem.item);
					conditionalItem = conditionalItem.parent;
				}
				if (conditionalPattern != null) {
					conditionalPatternBase.put(conditionalPattern,
							iteminTree.count);
				}
			}
			
			if (currentPattern.size() > 0) {
				FP.put(currentPattern, supportofCurrentPattern);
			}
			// counting frequencies of single items in conditional pattern-base
			Map<String, Integer> conditionalItemsMaptoFrequencies = new HashMap<String, Integer>();
			for (Set<String> conditionalPattern : conditionalPatternBase.keySet()) {
				
				for (String item : conditionalPattern) {
					
					if (conditionalItemsMaptoFrequencies.containsKey(item)) {
						int count = conditionalItemsMaptoFrequencies.get(item);
						count += conditionalPatternBase.get(conditionalPattern);
						conditionalItemsMaptoFrequencies.put(item, count);
					} else {
						conditionalItemsMaptoFrequencies.put(item,
								conditionalPatternBase.get(conditionalPattern));
					}
				}
			}
			// conditional fptree
			// HeaderTable Creation
			// first elements are being used just as pointers
			// non conditional frequents also will be removed
			List<FPtreeNode> conditional_headerTable = new ArrayList<>();
			for (String itemsforTable : conditionalItemsMaptoFrequencies.keySet()) {
				int count = conditionalItemsMaptoFrequencies.get(itemsforTable);
				if (count < threshold) {
					continue;
				}
				FPtreeNode f = new FPtreeNode(itemsforTable);
				f.count = count;
				conditional_headerTable.add(f);
			}
			FPtreeNode conditional_fptree = conditional_fptree_constructor(
					conditionalPatternBase, conditionalItemsMaptoFrequencies,
					threshold, conditional_headerTable);
			// headertable reverse ordering
			Collections.sort(conditional_headerTable,
					new sortInFrequency());
			//
			if (!conditional_fptree.children.isEmpty()) {
				fpgrowth(conditional_fptree, currentPattern, threshold,
						conditional_headerTable);
			}
		}
	}

	/**
	 * 模式基插入
	 * @param patternList
	 * @param count_of_pattern
	 * @param conditional_fptree
	 * @param conditional_headerTable
	 */
	private void insert(List<String> patternList, int count_of_pattern,
			FPtreeNode conditional_fptree, List<FPtreeNode> conditional_headerTable) {
		if (patternList.isEmpty()) {
			return;
		}
		String itemtoAddtotree = patternList.get(0);
		FPtreeNode newNode = null;
		boolean ifisdone = false;
		for (FPtreeNode child : conditional_fptree.children) {
			if (child.item.equals(itemtoAddtotree)) {
				newNode = child;
				child.count += count_of_pattern;
				ifisdone = true;
				break;
			}
		}
		if (!ifisdone) {
			for (FPtreeNode headerPointer : conditional_headerTable) {
				// this if also gurantees removing og non frequets
				if (headerPointer.item.equals(itemtoAddtotree)) {
					newNode = new FPtreeNode(itemtoAddtotree);
					newNode.count = count_of_pattern;
					newNode.parent = conditional_fptree;
					conditional_fptree.children.add(newNode);
					while (headerPointer.next != null) {
						headerPointer = headerPointer.next;
					}
					headerPointer.next = newNode;
				}
			}
		}
		patternList.remove(0);
		insert(patternList, count_of_pattern, newNode,
				conditional_headerTable);
	}


	public static void main(String[] args) throws FileNotFoundException {
		String file = "dataset\\statical\\";
		String dataset = "test";
		file += dataset + ".dat";
		
		long start = System.currentTimeMillis();
		new FPGrowth(new File(file), 0.3, dataset);
		System.out.println("cost : " + (System.currentTimeMillis() - start) + "ms");
	}


}

/**
 * 按照i-list频繁降序排序
 * 
 * @author Administrator
 * 
 */
class sortInFrequency implements Comparator<FPtreeNode> {

	public int compare(FPtreeNode o1, FPtreeNode o2) {
		if (o1.count > o2.count) {
			return 1;
		} else if (o1.count < o2.count) {
			return -1;
		} else {
			return 0;
		}
	}

}