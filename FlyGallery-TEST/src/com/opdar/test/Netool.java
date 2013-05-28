package com.opdar.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * @author Jeffrey Shi
 * QQ 362116120
 * MAIL to shijunfan@163.com
 */

public class Netool {
	public static InputStream POST(String url,String params)throws Exception{
		HashMap<String, String> map = new HashMap<String, String>();
		String[] pp=params.split("&");
		for (int i = 0; i < pp.length; i++) {
			String kv=pp[i];
			String[] keyValue=kv.split("=");
			if(keyValue.length>1){
				map.put(keyValue[0], keyValue[1]);
			}else{ 
				map.put(keyValue[0],"");
			}
		}
		HttpPost request = new HttpPost(url);
		List<NameValuePair> p = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> entry : map.entrySet()) {
			p.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}
		HttpEntity entity = new UrlEncodedFormEntity(p, "UTF-8");
		request.setEntity(entity);
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 40000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 40000;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpResponse response = client.execute(request);
		System.out.println("POST:  "+response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			InputStream is=response.getEntity().getContent();
			return is;
		}
		return null;
	}

	public static byte[] read(InputStream inputStream) throws Exception {
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

	public static Bitmap getImages(String imgUrl) throws Exception {
		URL url = new URL(imgUrl);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setConnectTimeout(60 * 1000);
		Bitmap bitmap = null;
		if (urlConnection.getResponseCode() == 200) {
			InputStream inputStream = urlConnection.getInputStream();
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			bitmap = BitmapFactory.decodeStream(inputStream, null, opt);
			inputStream.close();
			return bitmap;
			// return read(inputStream);
		}
		return bitmap;
	}
	/**
	 * POST
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static InputStream POSTJson(String url, String json)
			throws Exception {
		HttpPost request = new HttpPost(url);
		HttpEntity entity = new StringEntity(json,"UTF-8");
		request.setEntity(entity);
		request.addHeader("Content-Type", "application/json;charset=UTF-8");
		HttpParams httpParameters = new BasicHttpParams();
		int timeoutConnection = 5000;
		HttpConnectionParams.setConnectionTimeout(httpParameters,
				timeoutConnection);
		int timeoutSocket = 5000; 
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
		HttpClient client = new DefaultHttpClient(httpParameters);
		HttpResponse response = client.execute(request);
		System.out.println("POST:  "+response.getStatusLine().getStatusCode());
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		return null;
	}
	public static InputStream GET(String url) throws Exception {
		URL requestUrl = new URL(url);
		HttpURLConnection urlConnection = (HttpURLConnection) requestUrl
				.openConnection();
		urlConnection.setRequestMethod("GET");
		urlConnection.setConnectTimeout(45 * 1000);
		if (urlConnection.getResponseCode() == 200) {
			return urlConnection.getInputStream();
		}
		return null;
	}
	
	public static byte[] inputStreamToBytes(InputStream inputStream) throws Exception {
		ByteArrayOutputStream arrayBuffer = new ByteArrayOutputStream();
		byte[] b = new byte[1024];
		int len = -1;
		while ((len = inputStream.read(b)) != -1) {
			arrayBuffer.write(b, 0, len);
		}
		byte[] bytes=arrayBuffer.toByteArray();
		inputStream.close();
		arrayBuffer.close();
		return bytes;
	}

}
