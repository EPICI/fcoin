package util;

/**
 * From https://github.com/jrmelsha/keccak/blob/master/src/main/java/com/joemelsha/crypto/hash/Keccak.java
 * 
 * @author EPICI
 *
 */
public class Keccak {
	
	public long[] state;
	public int rate,ptr;
	public byte pad;
	
	public Keccak(int rate,byte pad){
		this.rate=rate;
		this.pad=pad;
		state = new long[25];
		ptr = 0;
	}
	
	public void absorbShort(short v){
		absorb((byte)v);
		absorb((byte)(v>>8));
	}
	
	public void absorbShorts(short[] vs){
		for(short v:vs){
			absorbShort(v);
		}
	}
	
	public void absorbInt(int v){
		absorb((byte)v);
		absorb((byte)(v>>8));
		absorb((byte)(v>>16));
		absorb((byte)(v>>24));
	}
	
	public void absorbInts(int[] vs){
		for(int v:vs){
			absorbInt(v);
		}
	}
	
	public void absorbLong(long v){
		absorb((byte)v);
		absorb((byte)(v>>8));
		absorb((byte)(v>>16));
		absorb((byte)(v>>24));
		absorb((byte)(v>>32));
		absorb((byte)(v>>40));
		absorb((byte)(v>>48));
		absorb((byte)(v>>56));
	}
	
	public void absorbuvInt(long v){
		if(v<0){
			absorb((byte)0xff);
			absorbLong(v);
		}else if(v<0xfd){
			absorb((byte)v);
		}else if(v<=0xffff){
			absorb((byte)0xfd);
			absorbShort((short)v);
		}else if(v<=0xffffffffL){
			absorb((byte)0xfe);
			absorbInt((int)v);
		}else{
			absorb((byte)0xff);
			absorbLong(v);
		}
	}
	
	public void absorb(byte b){
		int s = ptr&7, w = ptr>>3;
		state[w] ^= (b&0xffL)<<(s<<3);
		if(++ptr==rate){
			keccak(state);
			ptr=0;
		}
	}
	
	public void absorb(byte[] data){
		absorb(data,0,data.length);
	}
	
	public void absorb(byte[] data,int offset,int length){
		int last = offset+length;
		for(int i=offset;i<last;i++){
			absorb(data[i]);
		}
	}
	
	public void pad(){
		int tg = rate-1;
		absorb(pad);
		while(ptr!=tg)absorb((byte)0);
		absorb((byte)0x80);
	}
	
	public byte squeeze(){
		int s = ptr&7, w = ptr>>3;
		byte result = (byte)(state[w]>>(s<<3));
		if(++ptr==rate){
			keccak(state);
			ptr=0;
		}
		return result;
	}
	
	public byte[] squeeze(int count){
		byte[] result = new byte[count];
		squeeze(result,0,count);
		return result;
	}
	
	public void squeeze(byte[] array){
		squeeze(array,0,array.length);
	}
	
	public void squeeze(byte[] array,int offset,int length){
		int last = offset+length;
		for(int i=offset;i<last;i++){
			array[i] = squeeze();
		}
	}
	
	public static void keccak(long[] a) {
		//@formatter:off
		int c, i;
		long x, a_10_;
		long x0, x1, x2, x3, x4;
		long t0, t1, t2, t3, t4;
		long c0, c1, c2, c3, c4;
		long[] rc = RC;

		i = 0;
		do {
			//theta (precalculation part)
			c0 = a[0] ^ a[5 + 0] ^ a[10 + 0] ^ a[15 + 0] ^ a[20 + 0];
			c1 = a[1] ^ a[5 + 1] ^ a[10 + 1] ^ a[15 + 1] ^ a[20 + 1];
			c2 = a[2] ^ a[5 + 2] ^ a[10 + 2] ^ a[15 + 2] ^ a[20 + 2];
			c3 = a[3] ^ a[5 + 3] ^ a[10 + 3] ^ a[15 + 3] ^ a[20 + 3];
			c4 = a[4] ^ a[5 + 4] ^ a[10 + 4] ^ a[15 + 4] ^ a[20 + 4];

			t0 = (c0 << 1) ^ (c0 >>> (64 - 1)) ^ c3;
			t1 = (c1 << 1) ^ (c1 >>> (64 - 1)) ^ c4;
			t2 = (c2 << 1) ^ (c2 >>> (64 - 1)) ^ c0;
			t3 = (c3 << 1) ^ (c3 >>> (64 - 1)) ^ c1;
			t4 = (c4 << 1) ^ (c4 >>> (64 - 1)) ^ c2;

			//theta (xorring part) + rho + pi
			a[ 0] ^= t1;
			x = a[ 1] ^ t2; a_10_ = (x <<  1) | (x >>> (64 -  1));
			x = a[ 6] ^ t2; a[ 1] = (x << 44) | (x >>> (64 - 44));
			x = a[ 9] ^ t0; a[ 6] = (x << 20) | (x >>> (64 - 20));
			x = a[22] ^ t3; a[ 9] = (x << 61) | (x >>> (64 - 61));

			x = a[14] ^ t0; a[22] = (x << 39) | (x >>> (64 - 39));
			x = a[20] ^ t1; a[14] = (x << 18) | (x >>> (64 - 18));
			x = a[ 2] ^ t3; a[20] = (x << 62) | (x >>> (64 - 62));
			x = a[12] ^ t3; a[ 2] = (x << 43) | (x >>> (64 - 43));
			x = a[13] ^ t4; a[12] = (x << 25) | (x >>> (64 - 25));

			x = a[19] ^ t0; a[13] = (x <<  8) | (x >>> (64 -  8));
			x = a[23] ^ t4; a[19] = (x << 56) | (x >>> (64 - 56));
			x = a[15] ^ t1; a[23] = (x << 41) | (x >>> (64 - 41));
			x = a[ 4] ^ t0; a[15] = (x << 27) | (x >>> (64 - 27));
			x = a[24] ^ t0; a[ 4] = (x << 14) | (x >>> (64 - 14));

			x = a[21] ^ t2; a[24] = (x <<  2) | (x >>> (64 -  2));
			x = a[ 8] ^ t4; a[21] = (x << 55) | (x >>> (64 - 55));
			x = a[16] ^ t2; a[ 8] = (x << 45) | (x >>> (64 - 45));
			x = a[ 5] ^ t1; a[16] = (x << 36) | (x >>> (64 - 36));
			x = a[ 3] ^ t4; a[ 5] = (x << 28) | (x >>> (64 - 28));

			x = a[18] ^ t4; a[ 3] = (x << 21) | (x >>> (64 - 21));
			x = a[17] ^ t3; a[18] = (x << 15) | (x >>> (64 - 15));
			x = a[11] ^ t2; a[17] = (x << 10) | (x >>> (64 - 10));
			x = a[ 7] ^ t3; a[11] = (x <<  6) | (x >>> (64 -  6));
			x = a[10] ^ t1; a[ 7] = (x <<  3) | (x >>> (64 -  3));
			a[10] = a_10_;

			//chi
			c = 0;
			do {
				x0 = a[c + 0]; x1 = a[c + 1]; x2 = a[c + 2]; x3 = a[c + 3]; x4 = a[c + 4];
				a[c + 0] = x0 ^ ((~x1) & x2);
				a[c + 1] = x1 ^ ((~x2) & x3);
				a[c + 2] = x2 ^ ((~x3) & x4);
				a[c + 3] = x3 ^ ((~x4) & x0);
				a[c + 4] = x4 ^ ((~x0) & x1);

				c += 5;
			} while (c < 25);

			//iota
			a[0] ^= rc[i];

			i++;
		} while (i < 24);
		//@formatter:on
	}

	private static final long[] RC = { 0x0000000000000001L, 0x0000000000008082L, 0x800000000000808AL, 0x8000000080008000L, 0x000000000000808BL, 0x0000000080000001L, 0x8000000080008081L,
	                                   0x8000000000008009L, 0x000000000000008AL, 0x0000000000000088L, 0x0000000080008009L, 0x000000008000000AL, 0x000000008000808BL, 0x800000000000008BL,
	                                   0x8000000000008089L, 0x8000000000008003L, 0x8000000000008002L, 0x8000000000000080L, 0x000000000000800AL, 0x800000008000000AL, 0x8000000080008081L,
	                                   0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L };
}
