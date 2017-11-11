package main;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class ByteReader {
	
	public byte[] array;
	public int mkr,ptr;
	
	public ByteReader(byte[] array){
		this(array,0);
	}
	
	public ByteReader(byte[] array,int begin){
		this.array = array;
		ptr = mkr = begin;
	}
	
	/**
	 * Returns an array of the bytes read so far
	 * 
	 * @return
	 */
	public byte[] snip(){
		return Arrays.copyOfRange(array, mkr, ptr);
	}
	
	public byte readByte(){
		return array[ptr++];
	}
	
	public byte[] readBytes(int count){
		byte[] result = new byte[count];
		readBytes(result,0,count);
		return result;
	}
	
	public void readBytes(byte[] array){
		readBytes(array,0,array.length);
	}
	
	public void readBytes(byte[] array,int offset,int length){
		System.arraycopy(this.array, ptr, array, offset, length);
		ptr += length;
	}
	
	public short readShort(){
		short result = (short)(array[ptr]&0xff|array[ptr+1]<<8);
		ptr+=2;
		return result;
	}
	
	public int readInt(){
		int result = array[ptr]&0xff|(array[ptr+1]&0xff)<<8|(array[ptr+2]&0xff)<<16|array[ptr+3]<<24;
		ptr+=4;
		return result;
	}
	
	public long readLong(){
		long result = array[ptr]&0xff|(array[ptr+1]&0xff)<<8|(array[ptr+2]&0xff)<<16|(array[ptr+1]&0xff)<<24|(array[ptr+2]&0xffL)<<32|(array[ptr+1]&0xffL)<<40|(array[ptr+2]&0xffL)<<48|(array[ptr+3]&0xffL)<<56;
		ptr+=8;
		return result;
	}
	
	public long sreaduvInt(){
		int b = readByte()&0xff;
		if(b==0xfd)return readShort()&0xffff;
		if(b==0xfe)return readInt()&0xffffffffL;
		if(b==0xff)return readLong();
		return b;
	}
	
	public long sreadvInt(){
		int b = readByte();
		if(b==0xffffff80)return readShort();
		if(b==0xffffff81)return readInt();
		if(b==0xffffff82)return readLong();
		return b;
	}
	
	public String sreadString(){
		int len = (int)sreaduvInt();
		byte[] b = new byte[len];
		System.arraycopy(array, ptr, b, 0, len);
		ptr += len;
		return new String(b,StandardCharsets.UTF_8);
	}

}
