package com.github.moctavio.androidrestclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;	
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class RestFulWebservice extends Activity  {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);  

		final Button GetServerData = (Button) findViewById(R.id.GetServerData);

		GetServerData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				/** Web service URI */
				String serverURL = "http://rails-api.herokuapp.com/clients/1.json";
				new JSONAsyncTask().execute(serverURL);
			}
		});    

	}  


	/** AsyncTask to handle the process of getting and parsing the JSON. */
	private class JSONAsyncTask extends AsyncTask<String, Void, Void> {

		private ProgressDialog Dialog = new ProgressDialog(RestFulWebservice.this);
		private String Content;
		private String Error = null;

		final TextView output = (TextView) findViewById(R.id.output);
		final ListView listView = (ListView) findViewById(R.id.listView);
		final ArrayList<String> list = new ArrayList<String>();		

		/** UI Message */
		protected void onPreExecute() {
			Dialog.setMessage("Please wait..");
			Dialog.show();		
		}		

		/** Background process to obtain the JSON data */
		protected Void doInBackground(String... urls) 
		{
			StringBuilder stringBuilder = new StringBuilder();
			HttpClient client = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(urls[0]);
			try {
				HttpResponse response = client.execute(httpGet);
				StatusLine statusLine = response.getStatusLine();
				int statusCode = statusLine.getStatusCode();
				if (statusCode == 200) {
					HttpEntity entity = response.getEntity();
					InputStream content = entity.getContent();
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(content));
					String line;
					while ((line = reader.readLine()) != null) {
						stringBuilder.append(line);
					}
				} else {
					Log.e("JSON", "Failed to download file");
				}
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			Content = stringBuilder.toString();

			return null;
		}   

		protected void onPostExecute(Void unused) {           

			if (Error != null) {                  
				output.setText("Output : "+Error);                  
			} else {
				output.setText( Content );                              

				JSONObject jsonResponse;

				try {
					jsonResponse = new JSONObject(Content);

					/** Parse JSONObject into JSONArray */                     
					JSONArray jArrayObject = new JSONArray();
					jArrayObject.put(jsonResponse);

					/** Process each JSON Node */

					int lengthJsonArr = jArrayObject.length();  

					for(int i=0; i < lengthJsonArr; i++) 
					{
						JSONObject jsonChildNode = jArrayObject.getJSONObject(i);

						/** Fetch nodes according to the JSON response structure*/
						list.add("ID: "+jsonChildNode.optString("id").toString());
						list.add("Last name: "+jsonChildNode.optString("last_name").toString());
						list.add("First name: "+jsonChildNode.optString("first_name").toString());
						list.add("City: "+jsonChildNode.optString("city").toString());
						list.add("Country: "+jsonChildNode.optString("country").toString());
						list.add("Primary phone: "+jsonChildNode.optString("primary_phone").toString());
						list.add("Secondary phone: "+jsonChildNode.optString("secondary_phone").toString());
						list.add("Email: "+jsonChildNode.optString("email").toString());
						list.add("Created at: "+jsonChildNode.optString("created_at").toString());
						list.add("Updated at: "+jsonChildNode.optString("updated_at").toString());	
					}

					/** Show Parsed Output on screen (activity) */
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),R.layout.list_view, list);	
					listView.setAdapter(adapter);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}                 
			}
			Dialog.dismiss();
		}
	}
}