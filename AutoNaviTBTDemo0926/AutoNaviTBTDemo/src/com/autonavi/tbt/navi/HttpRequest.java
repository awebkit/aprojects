package com.autonavi.tbt.navi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.util.ByteArrayBuffer;

import com.autonavi.tbt.TBT;

public class HttpRequest extends Thread{

	private TBT m_tbt;
	private int m_connectId;
	private int m_type;
	private String m_url;
	private String m_head;
	private byte[] m_data;
	private int m_dataLength;
	
	public HttpRequest(TBT tbt, int connectId, int type, String url, String head, byte[] data, int dataLength){
		m_tbt = tbt;
		m_connectId = connectId;
		m_type = type;
		m_url = url;
		m_head = head;
		m_data=data;
		m_dataLength = dataLength;
	}
	
	public void run(){
		if(m_type == 1) {
			httpGet();
		}
		else {
			httpPost();
		}
	}
	
	private void httpPost() {
    	try{
			URL myUrl = new URL(m_url);
    		HttpURLConnection myConn = (HttpURLConnection) myUrl.openConnection();	//打开连接
    		
    		//myConn.setRequestMethod("GET");
    		myConn.setRequestMethod("POST");
    		myConn.setDoOutput(true);
			myConn.setDoInput(true);
			myConn.setUseCaches(false);
			if(m_dataLength==0){
				
				myConn.setRequestProperty("nContent-Length", "0");
				myConn.setConnectTimeout(8000);
	    		InputStream in = myConn.getInputStream();		//获取输入流
	    		BufferedInputStream bis = new BufferedInputStream(in);//获取BufferedInputStream对象
	    		ByteArrayBuffer baf = new ByteArrayBuffer(bis.available());
	    		int getdata = 0;
	    		while((getdata = bis.read())!= -1){		//读取BufferedInputStream中数据
	    			baf.append((byte)getdata);				//将数据读取到ByteArrayBuffer中
	    		}
	    		m_tbt.receiveNetData(m_connectId, baf.buffer(), baf.length());
			}
			else
			{        
				System.out.println(m_url);
				String s1="";
				for(int i=0;i<m_dataLength;i++){	
					String s = "" + m_data[i]+" ";
					s1+=s;	
				}
			   System.out.println(s1);	
			   myConn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			   myConn.connect();
			   OutputStream os=myConn.getOutputStream();
		       DataOutputStream dos=new DataOutputStream(os);
		       dos.write(m_data);
		       dos.flush();
		       dos.close(); 	
		       ByteArrayOutputStream bis = new ByteArrayOutputStream();
			   InputStream response = myConn.getInputStream();
				int status=myConn.getResponseCode();
				if (status!=HttpURLConnection.HTTP_OK) {
				}
				int off = 0;
				byte[] buffer = new byte[1024];
				while ((off = response.read(buffer)) >-1) {
					bis.write(buffer, 0, off);
				}
				m_tbt.receiveNetData(m_connectId, bis.toByteArray(), bis.size());
			}
    		System.out.println("receiveNetData OK");
    		m_tbt.setNetRequestState(m_connectId, 1);
    		System.out.println("setNetRequestState OK");	
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		m_tbt.setNetRequestState(m_connectId, 2);
    	} 
	}
	
	private void httpGet() {
    	try{
			URL myUrl = new URL(m_url);
    		HttpURLConnection myConn = (HttpURLConnection) myUrl.openConnection();	//打开连接
    		
    		myConn.setRequestMethod("GET");
    		myConn.setDoOutput(true);
			myConn.setDoInput(true);
			myConn.setUseCaches(false);
    		myConn.setConnectTimeout(8000);
    		
    		if(myConn.getResponseCode() != 404)
    		{
	    		InputStream in = myConn.getInputStream();		//获取输入流
	    		BufferedInputStream bis = new BufferedInputStream(in);//获取BufferedInputStream对象
	    		ByteArrayBuffer baf = new ByteArrayBuffer(bis.available());
	    		int getdata = 0;
	    		while((getdata = bis.read())!= -1){		//读取BufferedInputStream中数据
	    			baf.append((byte)getdata);				//将数据读取到ByteArrayBuffer中
	    		}
	    		m_tbt.receiveNetData(m_connectId, baf.buffer(), baf.length());
	    		
    		}
    		else
    		{
    			String s = "Not Found";
    			m_tbt.receiveNetData(m_connectId, s.getBytes(), s.length());
    		}
    		
    		System.out.println("receiveNetData OK");
    		m_tbt.setNetRequestState(m_connectId, 1);
    		System.out.println("setNetRequestState OK");
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		
    		// 网络请求失败
    		m_tbt.setNetRequestState(m_connectId, 2);
    	} 
	}
}
