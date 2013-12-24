package com.github.moctavio.restclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import android.widget.Button;
import android.widget.TextView;

public class RestFulWebservice extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rest_ful_webservice);  

		final Button GetServerData = (Button) findViewById(R.id.GetServerData);

		GetServerData.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				String serverURL = "http://rails-api.herokuapp.com/clients/1.json";
				new JSONAsyncTask().execute(serverURL);
			}
		});    

	}  

	private class JSONAsyncTask extends AsyncTask<String, Void, Void> {
		
		private ProgressDialog Dialog = new ProgressDialog(RestFulWebservice.this);
		private String Content;
		private String Error = null;

		TextView uiUpdate = (TextView) findViewById(R.id.output);
		TextView jsonParsed = (TextView) findViewById(R.id.jsonParsed);             
        
		protected void onPreExecute() {
			Dialog.setMessage("Please wait..");
			Dialog.show();
		}		
		
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

            // Close progress dialog
            Dialog.dismiss();
              
            if (Error != null) {                  
                uiUpdate.setText("Output : "+Error);                  
            } else {
                uiUpdate.setText( Content );                              
                 
                String OutputData = "";
                JSONObject jsonResponse;
                       
                try {
                       
                     /****** Creates a new JSONObject with name/value mappings from the JSON string. ********/
                     jsonResponse = new JSONObject(Content);
                       
                     /***** Returns the value mapped by name if it exists and is a JSONArray. ***/
                     /*******  Returns null otherwise.  *******/                     
                     JSONArray jArrayObject = new JSONArray();
                     jArrayObject.put(jsonResponse);
                       
                     /*********** Process each JSON Node ************/
   
                     int lengthJsonArr = jArrayObject.length();  
   
                     for(int i=0; i < lengthJsonArr; i++) 
                     {
                         /****** Get Object for each JSON node.***********/
                         JSONObject jsonChildNode = jArrayObject.getJSONObject(i);
                           
                         /******* Fetch node values **********/
                         
                         String id = jsonChildNode.optString("id").toString();
                         String last_name = jsonChildNode.optString("last_name").toString();
                         String first_name = jsonChildNode.optString("first_name").toString();
                         String city = jsonChildNode.optString("city").toString();
                         String country = jsonChildNode.optString("country").toString();
                         String primary_phone = jsonChildNode.optString("primary_phone").toString();
                         String secondary_phone = jsonChildNode.optString("secondary_phone").toString();
                         String email = jsonChildNode.optString("email").toString();
                         String created_at = jsonChildNode.optString("created_at").toString();
                         String updated_at = jsonChildNode.optString("updated_at").toString();
                         
                          
                         
                         OutputData += "ID              : "+ id +"  "
									 + "Last Name       : "+ last_name +"  "
									 + "Firts Name      : "+ first_name +"  "
                                     + "City            : "+ city +"  " 
                                     + "Country         : "+ country +"  " 
                                     + "Primary Phone   : "+ primary_phone +"  " 
                                     + "Secondary Phone : "+ secondary_phone +"  " 
                                     + "Email           : "+ email +"  " 
                                     + "Created at      : "+ created_at +"  " 
                                     + "Updated at      : "+ updated_at +"  " 
                                     +"--------------------------------------------------";                                                   
                    }
                 /****************** End Parse Response JSON Data *************/    
                      
                     //Show Parsed Output on screen (activity)
                     jsonParsed.setText( OutputData );
                      
                       
                 } catch (JSONException e) {
           
                     e.printStackTrace();
                 }   
                  
             }
		}

	}

}