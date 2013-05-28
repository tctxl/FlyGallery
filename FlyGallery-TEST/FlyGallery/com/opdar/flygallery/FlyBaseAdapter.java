package com.opdar.flygallery;

import android.graphics.Bitmap;
import android.view.View;
import android.view.View.MeasureSpec;

/**
 * @author Jeffrey Shi
 * QQ 362116120
 * MAIL to shijunfan@163.com
 */

public abstract class FlyBaseAdapter implements FlyAdapter{

	//adapterView
	private View baseView;
	
	@Override
	public Bitmap getImage(int position) {
		return viewToBitmap(getView(position, baseView));
	}

	private Bitmap viewToBitmap(View v){
		v.setDrawingCacheEnabled(true); 
        v.measure( 
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), 
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)); 
        v.layout(0, 0, v.getMeasuredWidth(), 
                v.getMeasuredHeight()); 
        v.buildDrawingCache(); 
        Bitmap bitmap= v.getDrawingCache(); 
        return bitmap;
	}
	
	public abstract View getView(int position,View v);
}
