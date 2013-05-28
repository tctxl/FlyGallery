package com.opdar.flygallery;

import android.graphics.Bitmap;

/**
 * @author Jeffrey Shi
 * QQ 362116120
 * MAIL to shijunfan@163.com
 */

public interface FlyAdapter {
	public Bitmap getImage(int position);
	public int getCount();
	public Object getItem();
}
