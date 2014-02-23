package com.example.finalproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ArrayList<String> test = new ArrayList<String>();
		test.add("19015");
		test.add("19064");
		test.add("19104");
		AsyncTask<ArrayList<String>, String, ArrayList<String>> retrieval = new GetInfo().execute(test);
		ArrayList<String> conditions = new ArrayList<String>();
		conditions.add("Cloudy");
		conditions.add("Clear");
		conditions.add("Rainy");
		conditions.add("Snowy");
		conditions.add("Foggy");
		
		try{
		Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, retrieval.get());
		dropdown.setAdapter(adapter);
		}
		catch(ExecutionException ex)
		{
			System.out.println(ex.getMessage());
		}
		catch(InterruptedException ex)
		{
			System.out.println(ex.getMessage());
		}
		
	}
	
	class GetInfo extends AsyncTask<ArrayList<String>, String, ArrayList<String>> 
	{
		// Method called to do all the work in the background	
		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... strings)
		{
			String weatherUrl = "http://api.wunderground.com/api/2d9ec69f2b5067cd/forecast10day/q/";
			String json = ".json";
			// Open an http client and make a request to a given url which is passed as a parameter
			HttpClient httpclient = new DefaultHttpClient();  
	        HttpGet request;   
	        ResponseHandler<String> handler = new BasicResponseHandler();
	        String result = null;
	        ArrayList<String> zipcodes = strings[0];
	        ArrayList<String> weatherInfo = new ArrayList<String>();
	        JsonParser jp = new JsonParser();
	        for(int k = 0; k < zipcodes.size(); k++)
	        {
	        // Make a new request to the second passed url.
	        request = new HttpGet(weatherUrl + zipcodes.get(k) + json);
	        
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
	        
	        // Create an ArrayList to hold the strings that will populate the listview.
	        
	        
	        // Use the array of weather data to get relevant information and add it to the arraylist.
	        weatherInfo.add("Weather information for " + zipcodes.get(k));
	        for(int i = 0; i < forecastList.size(); i++)
	        {
	        	String value = "";
	        	value += forecastList.get(i).getAsJsonObject().get("date").getAsJsonObject().get("monthname").getAsString() + " ";
	        	value += forecastList.get(i).getAsJsonObject().get("date").getAsJsonObject().get("day").getAsString() + ", ";
	        	value += forecastList.get(i).getAsJsonObject().get("date").getAsJsonObject().get("year").getAsString() + "\n";
	        	value += "High Temperature: " + forecastList.get(i).getAsJsonObject().get("high").getAsJsonObject().get("fahrenheit").getAsString() + " ";
	        	value += "Low Temperature: " + forecastList.get(i).getAsJsonObject().get("low").getAsJsonObject().get("fahrenheit").getAsString() + "\n";
	        	value += forecastList.get(i).getAsJsonObject().get("conditions").getAsString();
	        	weatherInfo.add(value);
	        }
	        }
	        // Close the http client and return the arraylist of strings.
	        httpclient.getConnectionManager().shutdown();
	        return weatherInfo;
		}
	}

}
