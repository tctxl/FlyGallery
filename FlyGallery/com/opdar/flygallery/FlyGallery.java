package com.opdar.flygallery;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * @author Jeffrey Shi
 * QQ 362116120
 * MAIL to shijunfan@163.com
 */

public class FlyGallery extends View implements OnTouchListener{
	private static final int STOP = 0;
	private static final int MOVE = 1;
	private static final int DOWN = 2;
	private static final int RECOVERY = 3;
	private float maxScale = 2f;
	private float minScale = 1f;
	private boolean isRefresh;
	private OnFlyGalleryItemListener flyGalleryItemListener;
	private ExecutorService flyThreads = Executors.newFixedThreadPool(10);
	//��ǰλ��
	private int currentPosition = 0;
	
	private FlyAdapter adapter;
	//Bitmap���ݼ�
	private FlyMap map = new FlyMap();
	//�ؼ����
	private int width,height;
	//�״�ִ��
	private boolean isFrist = true;
	//����ƫ����
	private float offsetX;
	//�������㵱ǰλ�õ�ƫ����
	private int movePosition = 0;
	//���Ҽ��
	private int spacing = 20;
	private int left;
	//��ָ����ʱ����
	private float downX;
	private int speed = 8;
	//��ǰLeft
	private float currentLeftX;
	private int status = STOP;  
	private float leftScale,rightScale,centerScale;

	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(status == RECOVERY){
				if(offsetX==0){
					status = STOP;
					return ;
				}
				if(Math.abs(offsetX)<speed){
					offsetX = 0;
					invalidate();
					return ;
				}
				if(offsetX<0){
					offsetX+=speed;
				}else{
					offsetX-=speed;
				}
				invalidate();
			}
		};
	};
	
	public FlyGallery(Context context) {
		super(context);
		setOnTouchListener(this);
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		float leftX = 0;
		refreshAdapter(movePosition);
		Bitmap bitmap = map.get(movePosition);
		if(bitmap!=null){
			offsetX %=bitmap.getWidth();
			if(isFrist){
				width = getWidth();
				height = getHeight();
				left = (int) (width / maxScale);
				leftX = left - bitmap.getWidth()+offsetX;
				currentLeftX = leftX;
				isFrist=false;
			}
			if(status==DOWN||status == MOVE|| status ==RECOVERY){
				leftX = currentLeftX+offsetX;
			}else if(status == STOP){
				leftX = currentLeftX+offsetX;
				currentLeftX= leftX;
				offsetX=0;
			}
			if(status ==RECOVERY){
				drawGallery(canvas,currentPosition , bitmap,leftX);
				handler.sendEmptyMessage(10);
			}else{
				drawGallery(canvas,movePosition , bitmap,leftX);
			}
		}
	}

	private float getLeftScale(float w){
		float scale = 0;
		if(offsetX>0)
		scale= (Math.abs(offsetX+w/maxScale)*2*maxScale)/width*2;
		if(scale>maxScale)scale=maxScale;
		if(scale<minScale)scale = minScale;
		return scale;
	}
	
	private float getRightScale(float w){
		float scale = 0;
		//float s = width/w/2/maxScale;
		//scale = Math.abs(offsetX)/s/w*2;
		//�������ű�
		if(offsetX<0)
			scale= (Math.abs(offsetX-w/maxScale)*2*maxScale)/width*2;
		if(scale>maxScale)scale=maxScale;
		if(scale<minScale)scale = minScale;
		return scale;
	}
	
	public void setFlyGalleryItemListener(
			OnFlyGalleryItemListener flyGalleryItemListener) {
		this.flyGalleryItemListener = flyGalleryItemListener;
	}
	
	private float getCenterScale(float leftScale, float rightScale){
		float scale= maxScale;
		if(offsetX<0){
			scale = minScale+maxScale-rightScale;
		}else{
			scale = minScale+maxScale-leftScale;
		}
		return scale;
	}
	
	private void drawGallery(Canvas canvas,int currentPosition, Bitmap bitmap, float leftX){
		float width = bitmap.getWidth()*maxScale>this.width/maxScale?bitmap.getWidth()/maxScale:bitmap.getWidth();
		leftScale =getLeftScale(width);
		rightScale =getRightScale(width);
		centerScale = getCenterScale(leftScale,rightScale);
		float leftSpac = (leftScale-1)*bitmap.getWidth();
		float rightSpac = (rightScale-1)*bitmap.getWidth();
		//������� 0��������Ϊ��һҳʱ�� ����>0���������������0��
		if(currentPosition==0){
			//�����������1 �����0��1λ���µ�Bitmap ��������Ϊ1 ��ֻ�����һҳ
			if(adapter.getCount()>1){
				drawBitmap(canvas, bitmap, leftX,0,currentPosition, centerScale);
				Bitmap bitmap2 = map.get(currentPosition+1);
				drawBitmap(canvas, bitmap2, leftX+bitmap.getWidth()*maxScale-rightSpac+spacing,bitmap.getHeight()/2,currentPosition+1, rightScale);
			}else if(adapter.getCount() ==1){
				//��ֻ��һ����ʱ���ܻ���
				drawBitmap(canvas, bitmap, left-bitmap.getWidth(),bitmap.getHeight()/2,currentPosition, centerScale);
			}
		}else if(currentPosition>0){
			//�����ĩβ �����ĩβҳ��ĩβǰһҳ ����������������ж�Ϊ ��ǰ��ʾҳ���м䲢�����ҿ϶���һҳ
			if(adapter.getCount()==currentPosition+1){
				drawBitmap(canvas, bitmap, leftX+leftSpac,bitmap.getHeight()/2,currentPosition, centerScale);
				Bitmap bitmap2 = map.get(currentPosition-1);
				drawBitmap(canvas, bitmap2, leftX-bitmap.getWidth()-spacing,bitmap.getHeight()/2,currentPosition-1, leftScale);
			}else{
				drawBitmap(canvas, bitmap, leftX+leftSpac,0,currentPosition ,centerScale);
				Bitmap bitmap2 = map.get(currentPosition-1);
				drawBitmap(canvas, bitmap2, leftX-bitmap.getWidth()-spacing,bitmap.getHeight()/2,currentPosition-1, leftScale);
				Bitmap bitmap3 = map.get(currentPosition+1);
				drawBitmap(canvas, bitmap3, leftX+bitmap.getWidth()*maxScale-rightSpac+spacing,bitmap.getHeight()/2,currentPosition+1, rightScale);
			}
		}
	}
	
	private void drawBitmap(Canvas canvas,Bitmap bitmap,float offset,float heightOffset,int position,float scale) {
		if(bitmap == null){
			flyThreads.execute(new ImageDownload(position));
		}else{
			Rect src = new Rect();
		    RectF dst = new RectF();
			src.right = 0;
			src.left = bitmap.getWidth();
			src.bottom = 0; 
			src.top = bitmap.getHeight();
			dst.right = offset;
			dst.bottom = bitmap.getHeight()/scale-bitmap.getHeight()/maxScale;
			dst.left = dst.right+bitmap.getWidth()*scale;
			dst.top = dst.bottom+bitmap.getHeight()*scale;
			canvas.drawBitmap(bitmap, src, dst, null);
		}
	}
	
	public void setAdapter(FlyAdapter flyAdapter) {
		this.adapter = flyAdapter;
		invalidate();
	}
	
	/**
	 * ˢ��Gallery
	 */
	public void request(){
		isFrist = true;
		isRefresh = true;
		refreshAdapter(movePosition);
		postInvalidate();
	}
	
	private void refreshAdapter(int currentPosition){
		//������� 0��������Ϊ��һҳʱ�� ����>0���������������0��
		if(currentPosition==0){
			//�����������1 �����0��1λ���µ�Bitmap ��������Ϊ1 ��ֻ�����һҳ
			if(adapter.getCount()>1){
				put(currentPosition);
				put(currentPosition+1);
			}else if(adapter.getCount() ==1){
				put(currentPosition);
			}
		}else if(currentPosition>0){
			//�����ĩβ �����ĩβҳ��ĩβǰһҳ ����������������ж�Ϊ ��ǰ��ʾҳ���м䲢�����ҿ϶���һҳ
			if(adapter.getCount()==currentPosition+1){
				put(currentPosition-1);
				put(currentPosition);
			}else{
				put(currentPosition-1);
				put(currentPosition);
				put(currentPosition+1);
			}
		}
		isRefresh = false;
	}
	
	private void put(int position){
		if(!map.contains(position)||isRefresh){
			flyThreads.execute(new ImageDownload(position));
		}
	}
	
	/**
	 * ͼƬ֮��ļ��
	 * @param spacing
	 */
	public void setSpacing(int spacing) {
		this.spacing = spacing;
	}
	
	/**
	 * ��ָ�ɿ������� �ָ��ٶ�
	 * @param speed
	 */
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	private Bitmap getImage(int i){
		return adapter.getImage(i);
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			status = DOWN;
			downX = event.getX();
			break;
		case MotionEvent.ACTION_MOVE:
			if(Math.abs(event.getX()-downX)>10){
				status = MOVE;
				Bitmap bitmap = map.get(movePosition);
				if(bitmap==null){
					break;
				}
				offsetX = event.getX()-downX;
				if(movePosition==0&&offsetX>0){
					if(offsetX>bitmap.getWidth()){
						offsetX = bitmap.getWidth()-1;
						break;
					}
				}else
				if(movePosition==adapter.getCount()-1&&offsetX<0){
					if(offsetX<-bitmap.getWidth()){
						offsetX = -bitmap.getWidth()+1;
						break;
					}
				}else{
					int position=currentPosition-(int) (offsetX/bitmap.getWidth()%adapter.getCount());
					if(position>=0&&position!=movePosition){
						movePosition = position;
						if(flyGalleryItemListener!=null){
							flyGalleryItemListener.onItemChange(movePosition);
						}
					}
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			if(status==DOWN){
				if(flyGalleryItemListener!=null)
					flyGalleryItemListener.onItemClick(currentPosition);
			}else{
				currentPosition = movePosition;
				if(flyGalleryItemListener!=null)
					flyGalleryItemListener.onItemEnd(currentPosition);
			}
			status = RECOVERY;
			break;
		}
		invalidate();
		return true;
	}
	
	class ImageDownload implements Runnable{
		private int position;
		public ImageDownload(int position) {
			this.position = position;
		}
		@Override
		public void run() {
			map.append(position, getImage(position));
			postInvalidate();
		}
		
	}
}
