package main;

import java.nio.charset.Charset;
import java.util.*;

public class ByteWriter {
	public static final int BLOCK_SHIFT = 10, BLOCK_SIZE = 1<<BLOCK_SHIFT;
	
	public ArrayList<byte[]> blocks;
	public byte[] block;
	public int ptr;
	
	public ByteWriter(){
		blocks = new ArrayList<>();
		block = new byte[BLOCK_SIZE];
		ptr = 0;
	}
	
	public int length(){
		return (blocks.size()<<BLOCK_SHIFT)|ptr;
	}
	
	public byte[] export(){
		int a = blocks.size(), b = a<<BLOCK_SHIFT, c = b|ptr;
		byte[] r = new byte[c];
		for(int i=0;i<a;i++){
			byte[] iblock = blocks.get(i);
			System.arraycopy(iblock, 0, r, i<<BLOCK_SHIFT, BLOCK_SIZE);
		}
		System.arraycopy(block, 0, r, b, ptr);
		return r;
	}
	
	public void writeByte(byte v){
		block[ptr++] = v;
		if(ptr==BLOCK_SIZE){
			blocks.add(block);
			block = new byte[BLOCK_SIZE];
			ptr = 0;
		}
	}
	
	public void writeBytes(byte[] vs){
		for(byte v:vs){
			writeByte(v);
		}
	}
	
	public void writeBool(boolean v){
		writeByte(v?(byte)1:0);
	}
	
	public void writeBools(boolean[] vs){
		for(boolean v:vs){
			writeBool(v);
		}
	}
	
	public void writeShort(short v){
		writeByte((byte)v);
		writeByte((byte)(v>>8));
	}
	
	public void writeShorts(short[] vs){
		for(short v:vs){
			writeShort(v);
		}
	}
	
	public void writeChar(char v){
		writeByte((byte)v);
		writeByte((byte)(v>>8));
	}
	
	public void writeChars(char[] vs){
		for(char v:vs){
			writeChar(v);
		}
	}
	
	public void writeInt(int v){
		writeByte((byte)v);
		writeByte((byte)(v>>8));
		writeByte((byte)(v>>16));
		writeByte((byte)(v>>24));
	}
	
	public void writeInts(int[] vs){
		for(int v:vs){
			writeInt(v);
		}
	}
	
	public void writeLong(long v){
		writeByte((byte)v);
		writeByte((byte)(v>>8));
		writeByte((byte)(v>>16));
		writeByte((byte)(v>>24));
		writeByte((byte)(v>>32));
		writeByte((byte)(v>>40));
		writeByte((byte)(v>>48));
		writeByte((byte)(v>>56));
	}
	
	public void writeLongs(long[] vs){
		for(long v:vs){
			writeLong(v);
		}
	}
	
	public void swriteuvInt(long v){
		if(v<0){
			writeByte((byte)0xff);
			writeLong(v);
		}else if(v<0xfd){
			writeByte((byte)v);
		}else if(v<=0xffff){
			writeByte((byte)0xfd);
			writeShort((short)v);
		}else if(v<=0xffffffffL){
			writeByte((byte)0xfe);
			writeInt((int)v);
		}else{
			writeByte((byte)0xff);
			writeLong(v);
		}
	}
	
	public void swritevInt(long v){
		if((0x800000000000082L<v)&(v<0xff)){
			writeByte((byte)v);
		}else if((0x800000000008000L<=v)&(v<0x8000)){
			writeByte((byte)0x80);
			writeShort((short)v);
		}else if((0x800000080000000L<=v)&(v<0x80000000L)){
			writeByte((byte)0x81);
			writeInt((int)v);
		}else{
			writeByte((byte)0x82);
			writeLong(v);
		}
	}
	
	public void swriteString(String s){
		byte[] bytes = s.getBytes(Charset.forName("utf-8"));
		swriteuvInt(bytes.length);
		writeBytes(bytes);
	}
	
}
