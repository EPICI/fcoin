package main;

import java.util.*;

import net.i2p.crypto.eddsa.EdDSAPublicKey;
import util.*;

public class BlockEntry {
	
	public byte[] bytes;
	public int offset;
	public ByteReader reader;
	
	/**
	 * Takes the context (summary of transactions and other stuff so far), checks if this
	 * is valid, if no, return false, if yes, return true
	 * <br>
	 * The context is modified in place; if this is invalid (returned false) the new state
	 * of the context is undefined (as in, doesn't have a specification, but it may still be
	 * deterministic)
	 * 
	 * @param context
	 * @return
	 */
	public boolean verifyConsume(Context context){
		try{
			return verifyConsumeUnchecked(context);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public boolean verifyConsumeUnchecked(Context context){
		reader = new ByteReader(bytes,offset);
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
			byte[] pkb = reader.readBytes(32);
			long designid = reader.sreaduvInt();
			String username = reader.sreadString();
			if(context.allowQuery){
				String doc = Scraping.getHtml("https://fc.sk89q.com/design?designId="+designid);
				String title = StringUtils.between(doc, "<h1>", "</h1>").trim();
				String desc = StringUtils.between(doc, "<h2 class=\"subtitle\">", "</h2>").trim();
				String uname = StringUtils.regex("<a href=\"user\\?userId=[0-9]+\">([\\w\\s]+)<\\/a>", doc);
				if(title==null||desc==null||uname==null||!username.equals(uname)||!title.equals("FCoin:PK+"))return false;
				try{
					byte[] apkb = Base64.getDecoder().decode(desc+"=");
					if(!Arrays.equals(pkb, apkb))return false;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
			EdDSAPublicKey pk = CryptoApiUtils.bytesToPk(pkb);
			int mk = reader.ptr;
			byte[] sig = reader.readBytes(64);
			if(!CryptoApiUtils.verifyWith(pk, bytes, 0, mk, sig))return false;
			MarkedSet<EdDSAPublicKey,Long> ms = context.userPk.get(username);
			if(ms==null){
				ms = new MarkedSet<>();
				context.userPk.put(username, ms);
			}
			return ms.add(pk,designid);
		}
		case 3:{
			// 0300 = disassociate username with public key
			byte[] pkb = reader.readBytes(32);
			long designid = reader.sreaduvInt();
			String username = reader.sreadString();
			if(context.allowQuery){
				String doc = Scraping.getHtml("https://fc.sk89q.com/design?designId="+designid);
				String title = StringUtils.between(doc, "<h1>", "</h1>").trim();
				String desc = StringUtils.between(doc, "<h2 class=\"subtitle\">", "</h2>").trim();
				String uname = StringUtils.regex("<a href=\"user\\?userId=[0-9]+\">([\\w\\s]+)<\\/a>", doc);
				if(title==null||desc==null||uname==null||!username.equals(uname)||!title.equals("FCoin:PK-"))return false;
				try{
					byte[] apkb = Base64.getDecoder().decode(desc+"=");
					if(!Arrays.equals(pkb, apkb))return false;
				}catch(Exception e){
					e.printStackTrace();
					return false;
				}
			}
			EdDSAPublicKey pk = CryptoApiUtils.bytesToPk(pkb);
			int mk = reader.ptr;
			byte[] sig = reader.readBytes(64);
			if(!CryptoApiUtils.verifyWith(pk, bytes, 0, mk, sig))return false;
			MarkedSet<EdDSAPublicKey,Long> ms = context.userPk.get(username);
			if(ms==null){
				ms = new MarkedSet<>();
				context.userPk.put(username, ms);
			}
			return ms.remove(pk,designid);
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
