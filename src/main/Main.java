package main;

import java.util.*;
import java.io.*;
import java.nio.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.xml.bind.*;
import org.bouncycastle.jcajce.provider.digest.*;
import de.ntcomputer.crypto.eddsa.*;
import net.i2p.crypto.eddsa.*;
import net.i2p.crypto.eddsa.spec.*;

public class Main {
	
	public static final String APP_NAME = "FCoin Wallet 1.0.0";

	public static final EdDSANamedCurveSpec P_SPEC = EdDSANamedCurveTable.getByName(EdDSANamedCurveTable.CURVE_ED25519_SHA512);
	
	public static byte[] skb;
	public static byte[] pkb;
	public static EdDSAPrivateKey sk;
	public static EdDSAPublicKey pk;
	
	public static final int[] V = {10,14,15,21}, U = {0,1,1,0};
	public static final long N = 4, M = 4, D = 1000000;

	public static void main(String[] args) {
		JFrame loginFrame = new JFrame(join(" - ",APP_NAME,"Login"));
		JPanel loginPanel = new JPanel();
		BoxLayout layout = new BoxLayout(loginPanel, BoxLayout.Y_AXIS);
		loginPanel.setLayout(layout);
		JLabel loginLabel = new JLabel("Enter password");
		JPasswordField loginField = new JPasswordField();
		loginPanel.add(loginLabel,null);
		loginPanel.add(loginField,null);
		loginFrame.add(loginPanel);
		loginField.addKeyListener(new KeyListener(){

			@Override
			public void keyPressed(KeyEvent event) {}

			@Override
			public void keyReleased(KeyEvent event) {}

			@Override
			public void keyTyped(KeyEvent event) {
				if(event.getKeyChar()<' '){
					setPassword(loginField.getPassword());
					loginFrame.dispose();
				}
			}
			
		});
		loginFrame.pack();
		loginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loginFrame.setVisible(true);
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
		skb = bytes;
		sk = new EdDSAPrivateKey(new EdDSAPrivateKeySpec(skb, P_SPEC));
		pk = new EdDSAPublicKey(new EdDSAPublicKeySpec(sk.getA(),P_SPEC));
		pkb = pk.getAbyte();
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
