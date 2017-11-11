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
		public ArrayList<ScriptChallenge> challenges;
		
	}
	
	public static interface Challenge{
		public boolean test(ReceiveAttempt attempt);
	}
	
	/**
	 * Requires a signature
	 * 
	 * @author EPICI
	 */
	public static class SignChallenge implements Challenge{
		
		public byte[] hbytes, pkbytes;
		
		public boolean test(ReceiveAttempt attempt){
			return false;
		}
		
	}
	
	/**
	 * Requires a level be solved, piece restrictions, unconnected flag and piece limit
	 * included
	 * 
	 * @author EPICI
	 */
	public static class SolveChallenge implements Challenge{
		
		public byte[] bytes;
		public long levelid;
		public int mask;
		public boolean uncon;
		public int piecelimit;
		
		public boolean test(ReceiveAttempt attempt){
			return false;
		}
		
	}
	
	/**
	 * Requires a script be satisfied
	 * <br>
	 * The script is a simple stack-based boolean language, chosen over
	 * direct expression evaluation for possible compactness, less redundancy,
	 * and more flexibility
	 * 
	 * @author EPICI
	 */
	public static class ScriptChallenge implements Challenge{
		
		public byte[] sbytes, cbytes;
		public ArrayList<Challenge> challenges;

		public boolean test(ReceiveAttempt attempt){
			return false;
		}
		
	}
	
	public static class ReceiveAttempt{
		
		public Context context;
		
	}
	
}
