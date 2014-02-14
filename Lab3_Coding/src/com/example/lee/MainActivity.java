package com.example.lee;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.Paint.Join;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	Button getButton, getGas;
	EditText add1;
	EditText city;
	EditText state;
	ListView l1;
	
	TextView zip;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getButton = (Button)findViewById(R.id.button1);
		add1 = (EditText)findViewById(R.id.editText1);
		city = (EditText)findViewById(R.id.editText2);
		state = (EditText)findViewById(R.id.editText3);
		getGas = (Button)findViewById(R.id.button2);
		l1 = (ListView)findViewById(R.id.listView1);
		
		zip = (TextView)findViewById(R.id.textView2);
		
		
		
		getButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
			
				String add = add1.getText()+" ";
				add = add +" " +city.getText();
				add = add + " " +state.getText();
				
				BackgroundWorkerClass worker = new BackgroundWorkerClass();
				
				worker.execute(new String[]{add});
				
				
			}
		});
		
		getGas.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String zipcode = zip.getText().toString();
				BackgroundGasClass worker = new BackgroundGasClass();
				
				worker.execute(new String[]{zipcode});
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	class BackgroundWorkerClass extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			
		}
		
		@Override
		protected JSONObject doInBackground(String... arg0) {

	        StringBuilder stringBuilder = new StringBuilder();
	        try {
	        String address = arg0[0];
	        //address = address.replaceAll("\n"," "); 
	        address = address.replaceAll(" ","%20");
	        HttpPost httppost = new HttpPost("http://maps.google.com/maps/api/geocode/json?address=" + address + "&sensor=false");
	        HttpClient client = new DefaultHttpClient();
	        HttpResponse response;
	        stringBuilder = new StringBuilder();


	            response = client.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            InputStream stream = entity.getContent();
	            int b;
	            while ((b = stream.read()) != -1) {
	                stringBuilder.append((char) b);
	            }
	        } catch (ClientProtocolException e) {
	        } catch (IOException e) {
	        }

	        JSONObject jsonObject = new JSONObject();
	        try {
	        	Log.i("brand---->", stringBuilder.toString());
	            jsonObject = new JSONObject(stringBuilder.toString());
	            
	            System.out.println(stringBuilder.toString());
	        } catch (JSONException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	        return jsonObject;
	    
			
			
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			
			super.onPostExecute(result);
			try{
			String zipVal = getZipCodeFromJSon(result);
			zip.setText(zipVal);
			}catch(Exception e){
				zip.setText(e.getMessage());
			}
			
			
		}

		private String getZipCodeFromJSon(JSONObject result) throws Exception {
			JSONArray arr = result.getJSONArray("results");
			//JSONArray obj = ((JSONObject) arr.get(0)).getJSONArray("address_components");
			JSONObject obj = (JSONObject) arr.get(0);
			JSONArray addArr = obj.getJSONArray("address_components");
			JSONObject postalObj = (JSONObject)addArr.get(addArr.length()-1);
			String zip = postalObj.getString("long_name");
			
			return zip;
		}
		
		
	}
	
	class BackgroundGasClass extends AsyncTask<String, Void, JSONObject>{

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			
			
		}
		
		@Override
		protected JSONObject doInBackground(String... arg0) {

	        StringBuilder stringBuilder = new StringBuilder();
	        try {
	        String zipcod = arg0[0].trim();
	        //address = address.replaceAll("\n"," "); 
	        //address = address.replaceAll(" ","%20");
	        HttpPost httppost = new HttpPost("http://www.mshd.net/api/gasprices/" + zipcod);
	        HttpClient client = new DefaultHttpClient();
	        HttpResponse response;
	        stringBuilder = new StringBuilder();


	            response = client.execute(httppost);
	            HttpEntity entity = response.getEntity();
	            InputStream stream = entity.getContent();
	            int b;
	            while ((b = stream.read()) != -1) {
	                stringBuilder.append((char) b);
	            }
	        } catch (ClientProtocolException e) {
	        } catch (IOException e) {
	        }

	        JSONObject jsonObject = new JSONObject();
	        try {
	            jsonObject = new JSONObject(stringBuilder.toString());
	            
	            System.out.println(stringBuilder.toString());
	        } catch (JSONException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }

	        return jsonObject;
	    
			
			
		}
		
		@Override
		protected void onPostExecute(JSONObject result) {
			
			super.onPostExecute(result);
			try{
			ArrayList<String> gaslist = getGasListFromJSon(result);
			l1.setAdapter(new ArrayAdapter<String>(MainActivity.this, R.layout.list_item, gaslist));
			
			}catch(Exception e){
				
				zip.setText(e.getMessage());
			}
			
			
		}

		private ArrayList<String> getGasListFromJSon(JSONObject result) throws Exception {
			ArrayList<String> list = new ArrayList<String>();
			JSONArray gaslist = result.getJSONArray("item");
			for(int i=0;i<gaslist.length();i++){
				JSONObject ob = (JSONObject)gaslist.get(i);
				String brand = ob.getString("brand");
				list.add(brand);
				//System.out.println("brand---->"+brand);
				Log.i("brand---->", brand);
			}
			return list;
		}

		private String getZipCodeFromJSon(JSONObject result) throws Exception {
			JSONArray arr = result.getJSONArray("results");
			//JSONArray obj = ((JSONObject) arr.get(0)).getJSONArray("address_components");
			JSONObject obj = (JSONObject) arr.get(0);
			JSONArray addArr = obj.getJSONArray("address_components");
			JSONObject postalObj = (JSONObject)addArr.get(addArr.length()-1);
			String zip = postalObj.getString("long_name");
			
			return zip;
		}
		
		
	}

}
