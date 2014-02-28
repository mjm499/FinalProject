package com.example.finalproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.cloudmine.api.CMApiCredentials;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet.ObjectGetInputSet;
import com.temboo.Library.CloudMine.ObjectStorage.ObjectGet.ObjectGetResultSet;
import com.temboo.core.TembooException;
import com.temboo.core.TembooSession;


public class MainActivity extends Activity {

	ArrayList<Integer> hightemps = new ArrayList<Integer>();
	ArrayList<Integer> lowtemps = new ArrayList<Integer>();
	ArrayList<String> weatherConditions = new ArrayList<String>();
	
	EditText et1, et2;
	Spinner dropdown;
	
	private static final String APP_ID = "15f993b560f341079b4ac75e8519a0fd";
	private static final String API_KEY = "614cd9867e304889b12608a4a581199e";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		CMApiCredentials.initialize(APP_ID, API_KEY, getApplicationContext());
		//ArrayList<String> test = new ArrayList<String>();
		Resources res = getResources();
		String[] zips = res.getStringArray(R.array.zipcodes_array);
		ArrayList<String> zipcodes = new ArrayList<String>(Arrays.asList(zips));
		AsyncTask<ArrayList<String>, String, Void> retrieval = new GetInfo().execute(zipcodes);
		ArrayList<String> conditions = new ArrayList<String>();
		conditions.add("Cloudy");
		conditions.add("Clear");
		conditions.add("Rainy");
		conditions.add("Snowy");
		conditions.add("Foggy");
		conditions.add("Sunny");
		
		try{
		dropdown = (Spinner)findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, conditions);//retrieval.get());
		dropdown.setAdapter(adapter);
		et1 = (EditText) findViewById(R.id.editText1);
		et2 = (EditText) findViewById(R.id.editText2);
		Button submit = (Button) findViewById(R.id.submitButton);
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				String hightemp = et1.getText().toString();
				String lowtemp = et2.getText().toString();
				if(hightemp.length() == 0)
					Toast.makeText(getApplicationContext(), "Please enter a high temp", Toast.LENGTH_LONG).show();
				else if(lowtemp.length() == 0)
					Toast.makeText(getApplicationContext(), "Please enter a low temp", Toast.LENGTH_LONG).show();
				else if(Integer.parseInt(lowtemp) > Integer.parseInt(hightemp))
					Toast.makeText(getApplicationContext(), "Low temp cannot be greater than high temp", Toast.LENGTH_LONG).show();
				else
				{
				String condit = dropdown.getItemAtPosition(dropdown.getSelectedItemPosition()).toString();
				Toast.makeText(getApplicationContext(), condit, Toast.LENGTH_LONG).show();
				}
			}
			
		});
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	@SuppressLint("DefaultLocale")
	class GetInfo extends AsyncTask<ArrayList<String>, String, Void> 
	{
		// Method called to do all the work in the background	
		@SuppressLint("DefaultLocale")
		@Override
		protected Void doInBackground(ArrayList<String>... strings)
		{
			ObjectGetResultSet objectGetResults = null;
			try{
			TembooSession session = new TembooSession("mjm499", "myFirstApp", "39f872092c7d4e909e11d8d07cf470f2");
			ObjectGet objectGetChoreo = new ObjectGet(session);
			ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
			objectGetInputs.set_APIKey("614cd9867e304889b12608a4a581199e");
			objectGetInputs.set_ApplicationIdentifier("15f993b560f341079b4ac75e8519a0fd");
			objectGetResults = objectGetChoreo.execute(objectGetInputs);
			}
			catch(TembooException ex)
			{
				ex.printStackTrace();
			}
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(objectGetResults.get_Response());
			JsonObject rootObj = root.getAsJsonObject();
			JsonArray zipcodes = rootObj.get("success").getAsJsonObject().get("57c1d84a22be9040799a2465c9020f33").getAsJsonObject().get("1").getAsJsonObject().get("zipcodeArray").getAsJsonArray();
			JsonArray keys = rootObj.get("success").getAsJsonObject().get("57c1d84a22be9040799a2465c9020f33").getAsJsonObject().get("2").getAsJsonObject().get("apiArray").getAsJsonArray();
			
			String weatherUrlp1 = "http://api.wunderground.com/api/";
			String weatherUrlp2 = "/forecast10day/q/";
			String json = ".json";
			// Open an http client and make a request to a given url which is passed as a parameter
			HttpClient httpclient = new DefaultHttpClient();  
	        HttpGet request;   
	        ResponseHandler<String> handler = new BasicResponseHandler();
	        String result = null;
	        int keyIter = -1;
	        
	        for(int k = 0; k < zipcodes.size(); k++) //zipcodes.size(); k++)
	        {
	        	if(k % 10 == 0)
	        		keyIter++;
	        // Make a new request to the second passed url.
	        request = new HttpGet(weatherUrlp1 + keys.get(keyIter).getAsString() + weatherUrlp2 + zipcodes.get(k).getAsString() + json);
	        
	        // Try to get the response from the request
	        try 
	        {  
	            result = httpclient.execute(request, handler);  
	        }
	        
	        // Exceptions to be handled
	        catch (ClientProtocolException e) 
	        {  
	            e.printStackTrace();  
	        } 
	        catch (IOException e) 
	        {  
	            e.printStackTrace();  
	        }
	        
	        // Parse the json and get an array of days for which we have weather information.
	        JsonElement weatherRoot = jp.parse(result);
	        JsonObject weatherObj = weatherRoot.getAsJsonObject();
	        JsonArray forecastList = weatherObj.get("forecast").getAsJsonObject().get("simpleforecast").getAsJsonObject().get("forecastday").getAsJsonArray();
	        
	        // Use the array of weather data to get relevant information and add it to the arraylist.
	        int high = 0, low = 0;
	        int snow = 0, clear = 0, fog = 0, rain = 0, cloud = 0, sun = 0;
	        for(int i = 0; i < forecastList.size(); i++)
	        {
	        	high += forecastList.get(i).getAsJsonObject().get("high").getAsJsonObject().get("fahrenheit").getAsInt();
	        	low += forecastList.get(i).getAsJsonObject().get("low").getAsJsonObject().get("fahrenheit").getAsInt();
	        	String con = forecastList.get(i).getAsJsonObject().get("conditions").getAsString().toLowerCase();
	        	if(con.contains("snow"))
	        		snow++;
	        	if(con.contains("clear"))
	        		clear++;
	        	if(con.contains("fog"))
	        		fog++;
	        	if(con.contains("rain"))
	        		rain++;
	        	if(con.contains("cloud"))
	        		cloud++;
	        	if(con.contains("sun"))
	        		sun++;
	        }
	        int[] conditionshere = {snow, clear, fog, rain, sun, cloud};
	        int max = -1;
	        for (int n : conditionshere) {
	            max = Math.max(max, n);
	        }
	        String finalWeather;
	        if(max == cloud)
	        	finalWeather = "Cloudy";
	        else if(max == snow)
	        	finalWeather = "Snowy";
	        else if(max == clear)
	        	finalWeather = "Clear";
	        else if(max == fog)
	        	finalWeather = "Foggy";
	        else if(max == rain)
	        	finalWeather = "Rainy";
	        else if(max == sun)
	        	finalWeather = "Sunny";
	        else
	        	finalWeather = "Undefined";
	        weatherConditions.add(finalWeather);
	        hightemps.add(high/10);
	        lowtemps.add(low/10);
	        }
	        // Close the http client and return the arraylist of strings.
	        httpclient.getConnectionManager().shutdown();
	        return null;
		}
	}

}
