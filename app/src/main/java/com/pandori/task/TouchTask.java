package com.pandori.task;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import kr.co.jiran.util.HttpClientHelper;
import kr.co.jiran.webserverfileshare.Constants;

@SuppressLint("NewApi") 
public class TouchTask extends AsyncTask<Void, Void, Void> {

	public interface TouchTaskListener {

		public void onTouchRequest();
		public void onTouchResponse(Result result);

	}


	private TouchTaskListener listener = null;
	
	public TouchTask(TouchTaskListener listener) {
		super();
		this.listener = listener;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub'
		if(listener != null)
			listener.onTouchRequest();
		super.onPreExecute();
	}

	@Override
	protected void onPostExecute(Result result) {
		// TODO Auto-generated method stub
		if(listener != null)
			listener.onTouchResponse(result);
		super.onPostExecute(result);
	}

	@Override
	protected void onProgressUpdate(Progress... values) {
		// TODO Auto-generated method stub
		super.onProgressUpdate(values);
	}

	@Override
	protected Result doInBackground(Params... params) {
		// TODO Auto-generated method stub
		
		String strFileid = params[0].getFileid();
		int nBufsize = params[0].getnBufsize();
		
		boolean isSuccess = false;
		
		while(!isSuccess){
			try{
				Thread.sleep(1000);
				
				HttpClientHelper helper = new HttpClientHelper();
				DefaultHttpClient client = helper.getSSLClient(nBufsize);
				
				String strUrl = Constants.URL_SSL_DOMAIN+Constants.URL_TOUCH+strFileid;
				
				HttpGet httpGet = helper.getGetRequest(strUrl, params[0].getLang());
				
				/*
				HttpResponse response = client.execute(httpGet);
				HttpEntity resEntity = response.getEntity();
				String responseString = EntityUtils.toString(resEntity);
				*/
				
				String responseString = helper.getResponseString(client, httpGet);
				
				
				//커넥션 종료
				client.getConnectionManager().shutdown();
				
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseString);
				
				Result result = new Result();
				
				
				try{
					String strResult;
					strResult = (String) obj.get("Result");
					if(strResult.equals("Success")){
						isSuccess = true;
					}
					else
						isSuccess = false;
				}catch(Exception e){
					isSuccess = false;
				}
				result.setSuccess(isSuccess);
				
				if(!isSuccess){
					try{
						String strMsg = (String) obj.get("Message");
						result.setMsg(strMsg);
					}catch(Exception e)
					{
						result.setMsg(null);
					}
				}

				if(isSuccess)
					return result;
				
			}catch(Exception e)
			{
				return null;
			}
			
		}
		
		return null;
		
	}

}
