package test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.haili.scps.SCPSTree;
import com.haili.util.DataIO;
import com.haili.vsw.VSW;

public class SCPSTreeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	public void insertTransaction() {
		// TODO Auto-generated method stub
		SCPSTree tree = new SCPSTree();
//		LinkedList<LinkedList<String>> db = new LinkedList<>();
//		
//		LinkedList<String> t = new LinkedList<>();
//		t.add("a");
//		t.add("b");
//		t.add("c");
//		
//		LinkedList<String> t2 = new LinkedList<>();
//		t2.add("a");
//		t2.add("b");
//		t2.add("d");
//		
//		db.add(t);
//		db.add(t2);
		
		DataIO dataset = new DataIO("test"); // accidents.dat 文件
		VSW vsw = new VSW(4, 2);
		List<Map<String, Object>> db = dataset.readData();
		
		for (Map<String, Object> transaction : db) {
			
			tree.insertPath(transaction, 3);
		}
		System.out.println(tree);		
	}
	
	public void updateIList() {
		SCPSTree tree = new SCPSTree();
		DataIO dataset = new DataIO("test"); // accidents.dat 文件
		VSW vsw = new VSW(4, 2);
		List<Map<String, Object>> db = dataset.readData();
		
		tree.updateIList(db);
		System.out.println(tree.getIList().toString());		
	}
	
	@Test
	public void sortTransaction() {
		SCPSTree tree = new SCPSTree();
		DataIO dataset = new DataIO("test"); // accidents.dat 文件
		VSW vsw = new VSW(4, 2);
		List<Map<String, Object>> db = dataset.readData();
		tree.updateIList(db);
		
		LinkedList<String> t = new LinkedList<>();
		t.add("d");
		t.add("a");
		t.add("b");
		t.add("c");
		
//		t.add("114");
//		t.add("113");
//		t.add("59");
//		t.add("16");
		
		System.out.println(tree.getIList().toString());	
		tree.sortTransaction(t);
		System.out.println(t.toString());		
	}

}
