package main;

import java.util.*;
import net.i2p.crypto.eddsa.*;

public class Context {
	
	public static final int MAX_VERSION = 1;
	public int version;
	public HashMap<String,MarkedSet<EdDSAPublicKey,Integer>> userPk;
	
	/**
	 * Blank constructor
	 */
	public Context(){
		version = 1;
		userPk = new HashMap<>();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param src
	 */
	public Context(Context src){
		version = src.version;
		userPk = new HashMap<>();
		for(Map.Entry<String,MarkedSet<EdDSAPublicKey,Integer>> entry:userPk.entrySet()){
			userPk.put(entry.getKey(), new MarkedSet<>(entry.getValue()));
		}
	}
	
	public boolean updateVersion(int newVersion){
		boolean canUpdate = (newVersion<=MAX_VERSION)&(newVersion==version+1);
		if(canUpdate)version = newVersion;
		return canUpdate;
	}
	
}
