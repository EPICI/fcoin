package main;

import java.util.*;

public class Block {
	
	public ArrayList<BlockEntry> entries;
	public byte[] header;
	
	/**
	 * Takes the context (summary of transactions and other stuff so far), checks if this
	 * is valid, if no, return null, if yes, return the modified context
	 * 
	 * @param context
	 * @return
	 */
	public Context verifyAndConsume(Context context){
		return null;
	}
	
}
