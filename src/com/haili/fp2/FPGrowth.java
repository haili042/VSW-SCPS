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
import com.haili.scps.SCPSNode;
import com.haili.scps.SCPSTree;
import com.haili.sw.SW;

public class FPGrowth implements MiningFP {

	private int minSN;
	private double minSup;
	private int total = 0;

	SCPSNode fptree;
	List<SCPSNode> headerTable = new ArrayList<>(); // headertable ����ÿ���ֵܽڵ��������ʼ��ָ��
	Map<Set<String>, Integer> FP = new HashMap<>(); // Ƶ������

	public FPGrowth(SCPSTree tree, double minSup, int minSN, String dataset) throws FileNotFoundException {
		this.minSup = minSup;
		this.minSN = minSN;
		fptree = tree.getRoot();
		
		for (String key : tree.getIlist().getIlistMap().keySet()) {
			SCPSNode n = tree.getIlist().getIlistMap().get(key);
			if (n.count > minSN) {
				headerTable.add(n);
			}
		}
		
		fpgrowth(fptree, new HashSet<String>(), minSN, headerTable);
//		SW.writeResult(FP, "fp2", dataset, minSup);
	}
	
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
	 * ����ģʽ��
	 * @param conditionalPatternBase
	 * @param conditionalItemsMaptoFrequencies
	 * @param threshold
	 * @param conditional_headerTable
	 * @return
	 */
	private SCPSNode conditional_fptree_constructor(
			Map<Set<String>, Integer> conditionalPatternBase,
			Map<String, Integer> conditionalItemsMaptoFrequencies,
			int threshold, List<SCPSNode> conditional_headerTable) {
		// FPTree constructing
		// the null node!
		SCPSNode conditional_fptree = new SCPSNode("root");
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
		// Ԥ����
		preProcessing(file, itemsMaptoFrequencies, input,
				sortedItemsbyFrequencies, itemstoRemove);
		// ����fp��
		construct_fpTree(file, itemsMaptoFrequencies, input,
				sortedItemsbyFrequencies, itemstoRemove);

	}

	/**
	 * Ԥ����
	 * 1 ���� ilist 
	 * 2 �ҳ���ɾ�� ilist �з�Ƶ����
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
		
		this.minSN = (int) Math.ceil(total * minSup); // ��ȡ��
		// orderiiiiiiiiiiiiiiiiiiiiiiiiiiiing
		// also elimating non-frequents

		// for breakpoint for comparison
		// ��ilist����Ƶ����������
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
		
		// ɾ��ilist�з�Ƶ����
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

		// �õ� Ƶ���������е� ilist map
	}

	/**
	 * ����fp��
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
			headerTable.add(new SCPSNode(itemsforTable));
		}
		// FPTree constructing
		input = new Scanner(file);
		// the null node!
		fptree = new SCPSNode("root"); // �������ڵ�
		fptree.item = null;
		// ordering frequent items transaction
		while (input.hasNextLine()) {
			String line = input.nextLine();
			StringTokenizer tokenizer = new StringTokenizer(line);
			List<String> transactionSortedbyFrequencies = new ArrayList<>();
			while (tokenizer.hasMoreTokens()) {
				String item = tokenizer.nextToken();
				if (itemstoRemove.contains(item)) {// ɾ�������з�Ƶ����
					continue;
				}
				int index = 0;
				for (String vectorString : transactionSortedbyFrequencies) {
					// some lines of condition is for alphabetically check in
					// equals situatioans Ƶ��������������
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
		// first calculating item frequencies in tree �����ֵܽڵ�����
		for (SCPSNode item : headerTable) {
			int count = 0;
			SCPSNode itemtemp = item;
			while (itemtemp.next != null) {
				itemtemp = itemtemp.next;
				count += itemtemp.count;
			}
			item.count = count;
		}
		Comparator c = new sortInFrequency();
		Collections.sort(headerTable, c);// Ƶ����������ͷ��
		input.close();
	}
	
	/**
	 * ���������������У� �ݹ�ÿһ����뵽fp����
	 * @param sortedTransaction
	 * @param fptree
	 * @param headerTable
	 */
	void insert(List<String> sortedTransaction, SCPSNode fptree,
			List<SCPSNode> headerTable) {
		if (sortedTransaction.isEmpty()) {
			return;
		}
		String itemtoAddtotree = sortedTransaction.get(0);
		SCPSNode newNode = null;
		boolean ifisdone = false; 
		for (SCPSNode child : fptree.children) {
			if (child.item.equals(itemtoAddtotree)) {
				newNode = child;
				child.count++;
				ifisdone = true;
				break;
			}
		}
		if (!ifisdone) {
			newNode = new SCPSNode(itemtoAddtotree);
			newNode.count = 1;
			newNode.parent = fptree;
			fptree.children.add(newNode);
			for (SCPSNode headerPointer : headerTable) {
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
	 * fp growth �㷨��ʼ
	 * @param fptree
	 * @param base
	 * @param threshold
	 * @param headerTable
	 * @param fp
	 */
	public void fpgrowth(SCPSNode fptree, Set<String> base, int threshold,
			List<SCPSNode> headerTable) {
		for (SCPSNode iteminTree : headerTable) {
			
			Set<String> currentPattern = new HashSet<>();
			currentPattern.addAll(base);
			if (iteminTree.item != null && !iteminTree.item.equals("root")) { // �ж��ǲ��Ǹ��ڵ�
				currentPattern.add(iteminTree.item);
			}
			
			int supportofCurrentPattern = 0;
			Map<Set<String>, Integer> conditionalPatternBase = new HashMap<>();
			while (iteminTree.next != null) {
				iteminTree = iteminTree.next;
				supportofCurrentPattern += iteminTree.count;
				Set<String> conditionalPattern = new HashSet<>();
				SCPSNode conditionalItem = iteminTree.parent;

				while (!conditionalItem.isRoot) {
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
			List<SCPSNode> conditional_headerTable = new ArrayList<>();
			for (String itemsforTable : conditionalItemsMaptoFrequencies.keySet()) {
				int count = conditionalItemsMaptoFrequencies.get(itemsforTable);
				if (count < threshold) {
					continue;
				}
				SCPSNode f = new SCPSNode(itemsforTable);
				f.count = count;
				conditional_headerTable.add(f);
			}
			SCPSNode conditional_fptree = conditional_fptree_constructor(
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
	 * ģʽ������
	 * @param patternList
	 * @param count_of_pattern
	 * @param conditional_fptree
	 * @param conditional_headerTable
	 */
	private void insert(List<String> patternList, int count_of_pattern,
			SCPSNode conditional_fptree, List<SCPSNode> conditional_headerTable) {
		if (patternList.isEmpty()) {
			return;
		}
		String itemtoAddtotree = patternList.get(0);
		SCPSNode newNode = null;
		boolean ifisdone = false;
		for (SCPSNode child : conditional_fptree.children) {
			if (child.item.equals(itemtoAddtotree)) {
				newNode = child;
				child.count += count_of_pattern;
				ifisdone = true;
				break;
			}
		}
		if (!ifisdone) {
			for (SCPSNode headerPointer : conditional_headerTable) {
				// this if also gurantees removing og non frequets
				if (headerPointer.item.equals(itemtoAddtotree)) {
					newNode = new SCPSNode(itemtoAddtotree);
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


	@Override
	public Map<Set<String>, Integer> getFP() {
		return this.FP;
	}


//	public static void main(String[] args) throws FileNotFoundException {
//		String file = "dataset\\statical\\";
//		String dataset = "test";
//		file += dataset + ".dat";
//		
//		long start = System.currentTimeMillis();
//		new FPGrowth(new File(file), 0.3, dataset);
//		System.out.println("cost : " + (System.currentTimeMillis() - start) + "ms");
//	}


}

/**
 * ����i-listƵ����������
 * 
 * @author Administrator
 * 
 */
class sortInFrequency implements Comparator<SCPSNode> {

	public int compare(SCPSNode o1, SCPSNode o2) {
		if (o1.count > o2.count) {
			return 1;
		} else if (o1.count < o2.count) {
			return -1;
		} else {
			return 0;
		}
	}

}