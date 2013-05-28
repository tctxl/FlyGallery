package com.opdar.flygallery;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.util.SparseArray;

/**
 * @author Jeffrey Shi
 * QQ 362116120
 * MAIL to shijunfan@163.com
 */

public class FlyMap {
	private SparseArray<SoftReference<Bitmap>> map = new SparseArray<SoftReference<Bitmap>>();
	public Bitmap get(int position){
		int key = map.indexOfKey(position);
		if(key >=0){
			return map.get(position).get();
		}
		return null;
	}
	
	public void remove(int position){
		map.remove(position);
	}
	
	public void append(int position,Bitmap bitmap) {
		map.append(position, new SoftReference<Bitmap>(bitmap));
	}
	
	public boolean contains(int position){
		return map.indexOfKey(position)>=0;
	}

	public void clear() {
		map.clear();
	}
}
