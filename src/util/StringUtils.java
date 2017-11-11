package util;

import java.util.*;
import java.util.regex.*;

public class StringUtils {
	
	private StringUtils(){}
	
	/**
	 * Get the first part of a given string that is between two other strings
	 * <br>
	 * Returns null if there are any problems, like that string doesn't exist
	 * 
	 * @param doc
	 * @param left
	 * @param right
	 * @return
	 */
	public static String between(String doc,String left,String right){
		if((doc==null)|(left==null)|(right==null))return null;
		int ia = doc.indexOf(left);
		if(ia<0)return null;
		int ib = doc.indexOf(right,ia);
		if(ib<0)return null;
		return doc.substring(ia+left.length(), ib);
	}
	
	/**
	 * Returns the first group from the first match for the specified regex
	 * 
	 * @param pattern pattern string
	 * @param doc string to search in
	 * @return first match, first group
	 */
	public static String regex(String pattern,String doc){
		Matcher match = Pattern.compile(pattern).matcher(doc);
		match.find();
		return match.group(1);
	}

}
