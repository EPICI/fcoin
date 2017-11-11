package util;

import main.*;
import net.i2p.crypto.eddsa.*;
import net.i2p.crypto.eddsa.spec.*;

public class CryptoApiUtils {

	public static final EdDSANamedCurveSpec P_SPEC = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
	
	private CryptoApiUtils(){}
	
	public static EdDSAPrivateKey bytesToSk(byte[] data){
		return new EdDSAPrivateKey(new EdDSAPrivateKeySpec(data, P_SPEC));
	}
	public static EdDSAPublicKey bytesToPk(byte[] data){
		return new EdDSAPublicKey(new EdDSAPublicKeySpec(data,P_SPEC));
	}
	public static EdDSAPublicKey skToPk(EdDSAPrivateKey sk){
		return new EdDSAPublicKey(new EdDSAPublicKeySpec(sk.getA(),P_SPEC));
	}
	public static byte[] skToBytes(EdDSAPrivateKey sk){
		return sk.geta();
	}
	public static byte[] pkToBytes(EdDSAPublicKey pk){
		return pk.getAbyte();
	}
	
	/**
	 * Easy sign
	 * <br>
	 * Returned value is 64 bytes (512 bits)
	 * 
	 * @param sk private key
	 * @param data 32 byte hash of data to sign
	 * @return 64 byte signature, or null if failed
	 */
	public static byte[] signWith(EdDSAPrivateKey sk,byte[] data){
		try{
			EdDSAEngine engine = new EdDSAEngine();
			engine.initSign(sk);
			engine.update(data);
			return engine.sign();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	public static byte[] signWith(EdDSAPrivateKey sk,byte[] data,int offset,int length){
		try{
			EdDSAEngine engine = new EdDSAEngine();
			engine.initSign(sk);
			engine.update(data,offset,length);
			return engine.sign();
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Easy verify
	 * <br>
	 * Returns true if successfully verified
	 * 
	 * @param pk public key
	 * @param data 32 byte hash of data to sign
	 * @param sig 64 byte signature
	 * @return verify result
	 */
	public static boolean verifyWith(EdDSAPublicKey pk,byte[] data,byte[] sig){
		try{
			EdDSAEngine engine = new EdDSAEngine();
			engine.initVerify(pk);
			engine.update(data);
			return engine.verify(sig);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	public static boolean verifyWith(EdDSAPublicKey pk,byte[] data,int offset,int length,byte[] sig){
		try{
			EdDSAEngine engine = new EdDSAEngine();
			engine.initVerify(pk);
			engine.update(data,offset,length);
			return engine.verify(sig);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Computes message digest using a nonstandard (but still secure!) SHAKE128 variant
	 * 
	 * @param message message to hash
	 * @param outputLen output length in bytes
	 * @return hash value
	 */
	public static byte[] cshake(byte[] message,int outputLen){
		if(outputLen<=0)return new byte[0];
		Keccak keccak = new Keccak(168,(byte)0x04);
		keccak.absorbInt(outputLen);
		keccak.absorb(message);
		keccak.pad();
		return keccak.squeeze(outputLen);
	}

}
