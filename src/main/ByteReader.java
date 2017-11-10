package main;

public class ByteReader {
	
	public byte[] array;
	public int ptr;
	
	public ByteReader(byte[] array){
		this.array=array;
		ptr=0;
	}
	
	public byte readByte(){
		return array[ptr++];
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

}
