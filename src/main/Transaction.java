package main;

import java.util.*;
import net.i2p.crypto.eddsa.EdDSAPublicKey;

public class Transaction {
	
	public BlockEntry origin;
	public String senderName;
	public EdDSAPublicKey senderPk;
	public long amount;
	public ArrayList<Output> outputs;
	
	public Transaction(BlockEntry origin){
		this.origin = origin;
		outputs = new ArrayList<>();
	}
	
	public static class Output{
		
		public byte[] bytes;
		
		
	}
	
	public static interface Challenge{
		public boolean test(ReceiveAttempt attempt);
	}
	
	public static class ReceiveAttempt{
		
	}
	
}
