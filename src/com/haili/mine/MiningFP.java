package com.haili.mine;

import java.util.Map;
import java.util.Set;

public interface MiningFP {

	public void mine();
	
	public Map<Set<String>, Integer> getFP();
}
