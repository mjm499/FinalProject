package com.example.finalproject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class MainActivity extends Activity {

	ArrayList<Integer> hightemps = new ArrayList<Integer>();
	ArrayList<Integer> lowtemps = new ArrayList<Integer>();
	ArrayList<String> weatherConditions = new ArrayList<String>();
	
	EditText et1, et2;
	Spinner dropdown;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//ArrayList<String> test = new ArrayList<String>();
		Resources res = getResources();
		String[] zips = res.getStringArray(R.array.zipcodes_array);
		ArrayList<String> zipcodes = new ArrayList<String>(Arrays.asList(zips));
		
		AsyncTask<ArrayList<String>, String, ArrayList<String>> retrieval = new GetInfo().execute(zipcodes);
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
			
		}
		/*catch(ExecutionException ex)
		{
			System.out.println(ex.getMessage());
		}
		catch(InterruptedException ex)
		{
			System.out.println(ex.getMessage());
		}*/
		
	}
	
	class GetInfo extends AsyncTask<ArrayList<String>, String, ArrayList<String>> 
	{
		// Method called to do all the work in the background	
		@Override
		protected ArrayList<String> doInBackground(ArrayList<String>... strings)
		{
			ArrayList<String> keys = new ArrayList<String>();
			keys.add("2d9ec69f2b5067cd");
			//keys.add(next api key)
			//keys.add(next api key)
			//keys.add(next api key)
			//keys.add(next api key)
			//Use the arraylist of api keys to make the weather requests.
			//Fix weather url so that it uses the api keys for its requests from the arraylist of keys
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
	        for(int k = 0; k < 10; k++) //zipcodes.size(); k++)
	        {
	        	if(k >= 10)
	        		break;
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
	        int high = 0, low = 0;
	        ArrayList<String> weathercon = new ArrayList<String>();
	        int snow = 0, clear = 0, fog = 0, rain = 0, cloud = 0, sun = 0;
	        for(int i = 0; i < forecastList.size(); i++)
	        {
	        	high += forecastList.get(i).getAsJsonObject().get("high").getAsJsonObject().get("fahrenheit").getAsInt();
	        	low += forecastList.get(i).getAsJsonObject().get("low").getAsJsonObject().get("fahrenheit").getAsInt();
	        	String con = forecastList.get(i).getAsJsonObject().get("conditions").getAsString();
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
	        	/*String value = "";
	        	value += forecastList.get(i).getAsJsonObject().get("date").getAsJsonObject().get("monthname").getAsString() + " ";
	        	value += forecastList.get(i).getAsJsonObject().get("date").getAsJsonObject().get("day").getAsString() + ", ";
	        	value += forecastList.get(i).getAsJsonObject().get("date").getAsJsonObject().get("year").getAsString() + "\n";
	        	value += "High Temperature: " + forecastList.get(i).getAsJsonObject().get("high").getAsJsonObject().get("fahrenheit").getAsString() + " ";
	        	value += "Low Temperature: " + forecastList.get(i).getAsJsonObject().get("low").getAsJsonObject().get("fahrenheit").getAsString() + "\n";
	        	value += forecastList.get(i).getAsJsonObject().get("conditions").getAsString();
	        	weatherInfo.add(value);*/
	        }
	        int[] conditionshere = {snow, clear, fog, rain, sun, cloud};
	        int max = -1;
	        for (int n : conditionshere) {
	            max = Math.max(max, n);
	        }
	        hightemps.add(high/10);
	        lowtemps.add(low/10);
	        
	        
	        
	        
	        }
	        // Close the http client and return the arraylist of strings.
	        httpclient.getConnectionManager().shutdown();
	        return weatherInfo;
		}
	}

}
