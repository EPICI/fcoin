package main;

public class BlockEntry {
	
	public byte[] bytes;
	
	/**
	 * Takes the context (summary of transactions and other stuff so far), checks if this
	 * is valid, if no, return false, if yes, return true, context is modified in place
	 * 
	 * @param context
	 * @return
	 */
	public boolean verifyConsume(Context context){
		ByteReader reader = new ByteReader(bytes);
		int otype = reader.readShort()&0xffff;
		switch(otype){
		// 0000 is reserved as a terminating character
		// case 0:
		case 1:{
			// 0100 = version change
			return context.updateVersion(reader.readShort()&0xffff);
		}
		case 2:{
			// 0200 = associate username with public key
		}
		case 3:{
			// 0300 = disassociate username with public key
		}
		case 4:{
			// 0400 = send cash, post contract, etc
		}
		case 5:{
			// 0500 = receive/accept/redeem cash
		}
		}
		return false;
	}
}
