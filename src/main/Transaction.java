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
		public int piecemask;
		public boolean uncon;
		public int piecelimit;
		
		public boolean active;
		
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
			try{
				int n = challenges.size();
				boolean[] subs = new boolean[n];
				for(int i=0;i<n;i++){
					subs[i] = challenges.get(i).test(attempt);
				}
				int jmpc = ~sbytes.length;
				int m = 0;
				Stack<Boolean> bstack = new Stack<>();
				ByteReader reader = new ByteReader(sbytes);
				int inst = (int)reader.sreaduvInt();
				int jmp = 0;
				while(inst!=0){// 0 signals end
					switch(inst){
					case 1:{// 1 is load challenge
						if(--jmp>-1)break;
						bstack.push(subs[(int)reader.sreaduvInt()]);
						break;
					}
					case 2:{// 2 is load FALSE
						if(--jmp>-1)break;
						bstack.push(Boolean.FALSE);
						break;
					}
					case 3:{// 3 is load TRUE
						if(--jmp>-1)break;
						bstack.push(Boolean.TRUE);
						break;
					}
					case 4:{// 4 is not
						if(--jmp>-1)break;
						bstack.push(!bstack.pop());
						break;
					}
					case 5:{// 5 is xor
						if(--jmp>-1)break;
						bstack.push(bstack.pop()^bstack.pop());
						break;
					}
					case 6:{// 6 is range xor
						long oj = reader.sreadvInt();
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int j = (int)Math.floorMod(oj, m);
						int k = (int)Math.floorMod(ok, m);
						boolean p = false;
						do{
							p ^= bstack.get(j);
							j++;
							if(j==m)j=0;
						}while(j!=k);
						bstack.push(p);
						break;
					}
					case 7:{// 7 is and
						if(--jmp>-1)break;
						bstack.push(bstack.pop()&bstack.pop());
						break;
					}
					case 8:{// 8 is range and
						long oj = reader.sreadvInt();
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int j = (int)Math.floorMod(oj, m);
						int k = (int)Math.floorMod(ok, m);
						if(--jmp>-1)break;
						boolean p = true;
						do{
							p &= bstack.get(j);
							j++;
							if(j==m)j=0;
						}while(j!=k);
						bstack.push(p);
						break;
					}
					case 9:{// 9 is or
						if(--jmp>-1)break;
						bstack.push(bstack.pop()|bstack.pop());
						break;
					}
					case 10:{// 10 is range or
						long oj = reader.sreadvInt();
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int j = (int)Math.floorMod(oj, m);
						int k = (int)Math.floorMod(ok, m);
						boolean p = false;
						do{
							p |= bstack.get(j);
							j++;
							if(j==m)j=0;
						}while(j!=k);
						bstack.push(p);
						break;
					}
					case 11:{// 11 is conditional jump
						int k = (int)reader.sreaduvInt();
						if(--jmp>-1)break;
						if(bstack.pop())jmp=k;
					}
					case 12:{// 12 is duplicate to top
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int k = (int)Math.floorMod(ok, m);
						bstack.push(bstack.get(k));
					}
					case 13:{// 13 is move to top
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int k = (int)Math.floorMod(ok, m);
						bstack.push(bstack.remove(k));
					}
					case 14:{// 14 is swap with top
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int k = (int)Math.floorMod(ok, m);
						Boolean p = bstack.pop(), q = bstack.remove(k);
						bstack.add(k,p);
						bstack.push(q);
					}
					case 15:{// 15 is swap with other
						long oj = reader.sreadvInt();
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int j = (int)Math.floorMod(oj, m);
						int k = (int)Math.floorMod(ok, m);
						Boolean p = bstack.remove(j), q = bstack.remove(k);
						bstack.add(k,p);
						bstack.add(j,q);
					}
					case 16:{// 16 is choose
						if(--jmp>-1)break;
						Boolean p = bstack.pop(), q = bstack.pop(), r = bstack.pop();
						bstack.push((q^r)&p^r);
					}
					case 17:{// 17 is majority
						if(--jmp>-1)break;
						Boolean p = bstack.pop(), q = bstack.pop(), r = bstack.pop();
						bstack.push((p|q)&r|p&q);
					}
					case 18:{// 18 is move to back
						if(--jmp>-1)break;
						bstack.add(0, bstack.pop());
					}
					case 19:{// 19 is move from back
						if(--jmp>-1)break;
						bstack.push(bstack.remove(0));
					}
					case 20:{// 20 is strip FALSE
						if(--jmp>-1)break;
						while(m>0&&!bstack.peek()){
							m--;
							bstack.pop();
						}
					}
					case 21:{// 21 is strip TRUE
						if(--jmp>-1)break;
						while(m>0&&bstack.peek()){
							m--;
							bstack.pop();
						}
					}
					case 22:{// 22 is delete all except ("isolate")
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int k = (int)Math.floorMod(ok, m);
						Boolean p = bstack.get(k);
						bstack.clear();
						bstack.push(p);
					}
					case 23:{// 23 is test if stack height is greater than a number
						long k = reader.sreaduvInt();
						if(--jmp>-1)break;
						bstack.push(m>k);
					}
					case 24:{// 24 is test if masked stack height is greater than a number
						long j = reader.sreaduvInt();
						long k = reader.sreaduvInt();
						if(--jmp>-1)break;
						bstack.push((m&k)>j);
					}
					case 25:{// 25 is remove top
						if(--jmp>-1)break;
						bstack.pop();
					}
					case 26:{// 26 is remove
						long ok = reader.sreadvInt();
						if(--jmp>-1)break;
						int k = (int)Math.floorMod(ok, m);
						bstack.remove(k);
					}
					default:{// not supported
						return false;
					}
					}
					m=bstack.size();
					if(jmp<jmpc)return false;
					inst = (int)reader.sreaduvInt();
				}
				return bstack.pop();
			}catch(Exception e){
				e.printStackTrace();
			}
			return false;
		}
		
	}
	
	public static class ReceiveAttempt{
		
		public Context context;
		
	}
	
}
