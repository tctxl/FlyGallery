package com.opdar.test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.gallerytest.R;
import com.opdar.flygallery.FlyAdapter;
import com.opdar.flygallery.FlyGallery;
import com.opdar.flygallery.OnFlyGalleryItemListener;

/**
 * @author Jeffrey Shi
 * QQ 362116120
 * MAIL to shijunfan@163.com
 */

public class MainActivity extends Activity {
	private String url = "http://opdar.com/imglist.json";
	private String[] urls = {};
	public interface Callback{
		public void back(String result);
	}
	
	class Request implements Runnable{
		Callback callback;
		public Request(Callback callback) {
			this.callback = callback;
	}
		
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			InputStream inputs = Netool.GET(url);
			byte[] bytes = Netool.read(inputs);
			String str = new String(bytes);
			callback.back(str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
		
	}
	FlyGallery flyGallery;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		flyGallery = new FlyGallery(this);
		flyGallery.setFlyGalleryItemListener(new OnFlyGalleryItemListener() {
			
			@Override
			public void onItemEnd(int position) {
				System.out.println(String.format("ITEM SLIDE END : %s", position));
			}
			
			@Override
			public void onItemClick(int position) {
				System.out.println(String.format("ITEM CLICK : %s", position));
			}
			
			@Override
			public void onItemChange(int position) {
				System.out.println(String.format("ITEM CHANGE : %s", position));
			}
		});
		
		flyGallery.setAdapter(new FlyAdapter() {
			
			@Override
			public Bitmap getImage(int position) {
				byte[] bytes = null;
				InputStream inputStream = null;
				FileOutputStream fos = null;
				try {
					URL urls = new URL(MainActivity.this.urls[position]);
					HttpURLConnection connection = (HttpURLConnection) urls
							.openConnection();
					connection.setDoInput(true);
					connection.connect();
					inputStream = connection.getInputStream();
					if (inputStream != null) {
						bytes = read(inputStream);
					} else {
						return null;
					}
					inputStream.close();
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				Bitmap bitmap  = getThumbnail(bytes, -1).copy(Bitmap.Config.ARGB_8888, true);
//				Bitmap bitmap  = getThumbnail(bytes, 100).copy(Bitmap.Config.ARGB_8888, true);
						Canvas canvas = new Canvas(bitmap);
						Paint paint = new Paint();
						paint.setColor(Color.parseColor("#5A000000"));
						canvas.drawRect(0, 30, 100, 50, paint );
						paint.setColor(Color.parseColor("#ffffff"));
						canvas.drawText("ÆÀ·Ö£º10.52", 10, 45, paint);
				return bitmap;
			}

		    public Bitmap getThumbnail(byte[] data,int width){
		        BitmapFactory.Options options = new BitmapFactory.Options();
		        options.inJustDecodeBounds = true;
		        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		        options.inJustDecodeBounds = false;
		        if(width!=-1)
		            options.inSampleSize = options.outWidth / width;
		        options.inPurgeable = true;
		        options.inInputShareable = true;
		        bmp = BitmapFactory.decodeByteArray(data, 0, data.length, options);
		        return bmp;
		    }
		    private byte[] read(InputStream inputStream) throws Exception {
		        ByteArrayOutputStream arrayBuffer = new ByteArrayOutputStream();
		        byte[] b = new byte[1024];
		        int len = -1;
		        while ((len = inputStream.read(b)) != -1) {
		            arrayBuffer.write(b, 0, len);
		        }
		        inputStream.close();
		        arrayBuffer.close();
		        return arrayBuffer.toByteArray();
		    }
			@Override
			public int getCount() {
				return urls.length;
			}

			@Override
			public String getItem() {
				
				return null;
			}
		});
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(1);
//		flyGallery.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, 200));
		layout.addView(flyGallery);
		ImageView iv = new ImageView(this);
		iv.setImageResource(R.drawable.ic_launcher);
		layout.addView(iv);
		setContentView(layout);
		handler.sendEmptyMessage(10);
	}
	private Handler handler = new Handler(){
		public void handleMessage(android.os.Message msg) {

			new Thread(new Request(new MainActivity.Callback() {
				
				@Override
				public void back(String result) {
					System.out.println(result);
					try {
						JSONObject json = new JSONObject(result);
						JSONArray jar = json.getJSONArray("list");
						urls = new String[jar.length()];
						for(int i=0;i<jar.length();i++){
							urls[i]=jar.getString(i);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					flyGallery.request();
				}
			})).start();
		};
	};
}
