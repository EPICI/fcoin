package main;

import java.util.*;

/**
 * A set that keeps around times of some kind for a total ordering of adds and removes,
 * that way no damage is done by repeating an old operation
 * 
 * @author EPICI
 *
 * @param <K>
 * @param <V>
 */
public class MarkedSet<K,V extends Comparable<V>> extends AbstractSet<K> {

	public HashMap<K,V> set, unset;
	
	public MarkedSet() {
		set = new HashMap<>();
		unset = new HashMap<>();
	}
	
	public MarkedSet(MarkedSet<K,V> src){
		set = new HashMap<>(src.set);
		unset = new HashMap<>(src.unset);
	}

	@Override
	public Iterator<K> iterator() {
		return set.keySet().iterator();
	}

	@Override
	public int size() {
		return set.size();
	}
	
	public boolean contains(Object o){
		return set.containsKey(o);
	}
	
	public boolean add(K k,V v){
		V av = set.get(k), bv = unset.get(k);
		if(bv==null){
			if(av==null||av.compareTo(v)<0){
				set.put(k, v);
				return true;
			}
		}else if(bv.compareTo(v)<0){
			set.put(k, v);
			unset.remove(k);
			return true;
		}
		return false;
	}
	
	public boolean remove(K k,V v){// same logic as add except we swap the order
		V bv = set.get(k), av = unset.get(k);
		if(bv==null){
			if(av==null||av.compareTo(v)<0){
				set.put(k, v);
				return true;
			}
		}else if(bv.compareTo(v)<0){
			set.put(k, v);
			unset.remove(k);
			return true;
		}
		return false;
	}

}
