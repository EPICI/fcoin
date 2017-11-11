package main;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.security.InvalidKeyException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.xml.bind.*;
import org.bouncycastle.jcajce.provider.digest.*;
import de.ntcomputer.crypto.eddsa.*;
import net.i2p.crypto.eddsa.*;
import net.i2p.crypto.eddsa.spec.*;
import util.*;

public class Main {
	
	public static final String APP_NAME = "FCoin Wallet 1.0.0";
	
	public static byte[] skb;
	public static byte[] pkb;
	public static EdDSAPrivateKey sk;
	public static EdDSAPublicKey pk;
	
	public static JFrame loginFrame;

	public static void main(String[] args) {
		loginFrame = new JFrame(join(" - ",APP_NAME,"Login"));
		JPanel loginPanel = new JPanel();
		BoxLayout layout = new BoxLayout(loginPanel, BoxLayout.Y_AXIS);
		loginPanel.setLayout(layout);
		JLabel loginLabel = new JLabel("Enter password");
		JPasswordField loginField = new JPasswordField(60);
		JLabel keyLabel = new JLabel("Or enter 256-bit private key as 32 hex digits");
		JTextField keyField = new JTextField(40);
		loginPanel.add(loginLabel,null);
		loginPanel.add(loginField,null);
		loginPanel.add(keyLabel);
		loginPanel.add(keyField);
		loginFrame.add(loginPanel);
		loginField.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent event) {
				int kc = event.getKeyCode();
				if(kc==KeyEvent.VK_ENTER){
					setPassword(loginField.getPassword());
					toMain();
				}
			}

			@Override
			public void keyReleased(KeyEvent event) {}

			@Override
			public void keyTyped(KeyEvent event) {}
			
		});
		keyField.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent event) {
				int kc = event.getKeyCode();
				if(kc==KeyEvent.VK_ENTER){
					setKey(DatatypeConverter.parseHexBinary(keyField.getText().trim().replaceAll("\\s", "")));
					toMain();
				}
			}

			@Override
			public void keyReleased(KeyEvent event) {}

			@Override
			public void keyTyped(KeyEvent event) {}
			
		});
		loginFrame.pack();
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setVisible(true);
	}
	
	public static void toMain(){
		loginFrame.dispose();
	}
	
	public static void setPassword(char[] pw){
		int cn = pw.length, bn = cn<<1;
		byte[] bytes = new byte[bn], salt = DatatypeConverter.parseHexBinary("2a3b2a1ff7054c1e6c7aa10d04133bcf");
		ByteBuffer bbuf = ByteBuffer.wrap(bytes);
		bbuf.order(ByteOrder.LITTLE_ENDIAN);
		bbuf.asCharBuffer().put(pw);
		SHA3.Digest256 rhash = new SHA3.Digest256();
		rhash.update(salt);
		Arrays.fill(pw, '\0');
		for(int i=1<<16;i>0;i--){// shitty pbkdf
			SHA3.Digest256 hash = null;
			try {
				hash = (SHA3.Digest256) rhash.clone();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			hash.update(bytes);
			Arrays.fill(bytes, (byte)0);
			bytes = hash.digest();
			hash.engineReset();
		}
		// derive key?
		setKey(bytes);
	}
	public static void setKey(byte[] bytes){
		skb = bytes;
		sk = CryptoApiUtils.bytesToSk(bytes);
		pk = CryptoApiUtils.skToPk(sk);
		pkb = CryptoApiUtils.pkToBytes(pk);
	}
	
	public static String join(String con,String... vs){
		if(vs==null)return "";
		int n = vs.length;
		if(n==0)return "";
		StringBuilder sb = new StringBuilder();
		sb.append(vs[0]);
		for(int i=1;i<n;i++){
			sb.append(con);
			sb.append(vs[i]);
		}
		return sb.toString();
	}

}
