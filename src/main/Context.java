package main;

import java.util.*;
import net.i2p.crypto.eddsa.*;

public class Context {
	
	public static final int MAX_VERSION = 1;
	public int version;
	public HashMap<String,MarkedSet<EdDSAPublicKey,Long>> userPk;
	
	/**
	 * Whether to allow queries to FC server
	 */
	public boolean allowQuery;
	
	public static final int[][] vu1 = {{10,14,15,21},{0,1,1,0}};
	
	/**
	 * Blank constructor
	 */
	public Context(){
		version = 1;
		userPk = new HashMap<>();
		allowQuery = false;
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param src
	 */
	public Context(Context src){
		version = src.version;
		userPk = new HashMap<>();
		for(Map.Entry<String,MarkedSet<EdDSAPublicKey,Long>> entry:userPk.entrySet()){
			userPk.put(entry.getKey(), new MarkedSet<>(entry.getValue()));
		}
		allowQuery = src.allowQuery;
	}
	
	public boolean updateVersion(int newVersion){
		boolean canUpdate = (newVersion<=MAX_VERSION)&(newVersion==version+1);
		if(canUpdate)version = newVersion;
		return canUpdate;
	}
	
	/**
	 * Get array of modulus and exponent
	 * 
	 * @return
	 */
	public int[][] getVu(){
		return vu1;
	}
	
	public long getBlockReward(int nth){
		if((nth<=0)|(nth>1000))return 0;
		nth=1001-nth;
		return nth*nth;
	}
	
}
