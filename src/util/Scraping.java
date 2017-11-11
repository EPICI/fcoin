package util;

import java.util.*;
import java.awt.*;
import java.awt.image.*;
import javax.imageio.*;
import java.io.*;
import java.net.*;

public class Scraping {
	
	private Scraping(){}
	
	public static BufferedImage getImage(String url){
		BufferedImage result = null;
		try{
			result = ImageIO.read(new URL(url));
		}catch(IOException e){
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getHtml(String url){
		String result = null;
		URLConnection connection = null;
		Scanner scanner = null;
		try{
			connection = new URL(url).openConnection();
			scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\0");
			result = scanner.next();
		}catch(Exception e){
			e.printStackTrace();
		}
		if(scanner!=null)scanner.close();
		return result;
	}
	
}
