package com.example.finalproject;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


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

	//objects that will be used in the Activity within asynctasks.
	ArrayList<locationObject> locations = new ArrayList<locationObject>();
	ArrayList<locationObject> chosenPlaces = new ArrayList<locationObject>();
	ArrayList<String> conditionsArray = new ArrayList<String>();
	ArrayList<String> hightempArray = new ArrayList<String>();
	ArrayList<String> lowtempArray = new ArrayList<String>();
	ArrayList<String> citynameArray = new ArrayList<String>();
	
	EditText et1, et2;
	Spinner dropdown;
	Button submit;
	int highTemperature, lowTemperature;
	String selectedConditions;
	ListView lv;
	CustomAdapter adapter;
	
	//Cloudmine keys
	static final String APP_ID = "15f993b560f341079b4ac75e8519a0fd";
	static final String API_KEY = "614cd9867e304889b12608a4a581199e";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		submit = (Button) findViewById(R.id.submitButton);
		lv = (ListView) findViewById(R.id.listView);
		lv.setVisibility(View.INVISIBLE);
		@SuppressWarnings("unused")
		AsyncTask<Void, String, Void> retrieval = new GetInfo().execute();
		
		//Create and populate the weather conditions array.
		ArrayList<String> conditions = new ArrayList<String>();
		conditions.add("Cloudy");
		conditions.add("Clear");
		conditions.add("Rainy");
		conditions.add("Snowy");
		conditions.add("Foggy");
		conditions.add("Sunny");
		
		//try{
		dropdown = (Spinner)findViewById(R.id.spinner1);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, conditions);
		dropdown.setAdapter(adapter);
		et1 = (EditText) findViewById(R.id.editText1);
		et2 = (EditText) findViewById(R.id.editText2);
		
		//Add an onclick listener to display a toast while the application is processing background data.
		submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) 
			{
				Toast.makeText(getApplicationContext(), "Background data still processing", Toast.LENGTH_LONG).show();
			}
			
		});
		//}
		/*catch(Exception e)
		{
			e.printStackTrace();
		}*/
		
	}
	
	@SuppressLint("DefaultLocale")
	class GetInfo extends AsyncTask<Void, String, Void> 
	{
		// Method called to do all the work in the background	
		@SuppressLint("DefaultLocale")
		@Override
		protected Void doInBackground(Void... voids)
		{
			
			//Cloudmine query to get the Wunderground api keys and zipcodes
			ObjectGetResultSet objectGetResults = null;
			try{
			TembooSession session = new TembooSession("mjm499", "myFirstApp", "39f872092c7d4e909e11d8d07cf470f2");
			ObjectGet objectGetChoreo = new ObjectGet(session);
			ObjectGetInputSet objectGetInputs = objectGetChoreo.newInputSet();
			objectGetInputs.set_APIKey(API_KEY);
			objectGetInputs.set_ApplicationIdentifier(APP_ID);
			objectGetResults = objectGetChoreo.execute(objectGetInputs);
			}
			catch(TembooException ex)
			{
				ex.printStackTrace();
				System.exit(1);
			}
			//Parsing the json into a zipcodes array, a keys array, and an image array.
			JsonParser jp = new JsonParser();
			JsonElement root = jp.parse(objectGetResults.get_Response());
			JsonObject rootObj = root.getAsJsonObject();
			JsonArray zipcodes = rootObj.get("success").getAsJsonObject().get("57c1d84a22be9040799a2465c9020f33").getAsJsonObject().get("1").getAsJsonObject().get("zipcodeArray").getAsJsonArray();
			JsonArray keys = rootObj.get("success").getAsJsonObject().get("57c1d84a22be9040799a2465c9020f33").getAsJsonObject().get("2").getAsJsonObject().get("apiArray").getAsJsonArray();
			JsonArray imageUrls = rootObj.get("success").getAsJsonObject().get("57c1d84a22be9040799a2465c9020f33").getAsJsonObject().get("3").getAsJsonObject().get("urlArray").getAsJsonArray();
			String weatherUrlp1 = "http://api.wunderground.com/api/";
			String weatherUrlp2 = "/forecast10day/q/";
			String json = ".json";
			
			// Open an http client and make a request to a given url which is passed as a parameter
			HttpClient httpclient = new DefaultHttpClient();  
	        HttpGet request;   
	        ResponseHandler<String> handler = new BasicResponseHandler();
	        String result = null;
	        int keyIter = -1;
	        
	        //Iterate through our keys and make requests
	        for(int k = 0; k < zipcodes.size(); k++) 
	        {
	        	if(k % 10 == 0)
	        		keyIter++;
	        
	        // Make a new request to weather underground
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
	        
	        // Get the average high and low temperatures, and the most occurring conditions.
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
	        ArrayList<String> possibleWeather = new ArrayList<String>();
	        if(max == cloud)
	        	possibleWeather.add("Cloudy");
	        if(max == snow)
	        	possibleWeather.add("Snowy");
	        if(max == clear)
	        	possibleWeather.add("Clear");
	        if(max == fog)
	        	possibleWeather.add("Foggy");
	        if(max == rain)
	        	possibleWeather.add("Rainy");
	        if(max == sun)
	        	possibleWeather.add("Sunny");
	        if(possibleWeather.size() == 1)
	        {
	        	finalWeather = possibleWeather.get(0);
	        }
	        else if(possibleWeather.size() > 1)
	        {
	        	finalWeather = "Equal parts: ";
	        	for(int tmp = 0; tmp < possibleWeather.size(); tmp++)
	        	{
	        		if(tmp == possibleWeather.size() -1)
	        		{
	        			finalWeather += possibleWeather.get(tmp) + ".";
	        		}
	        		else
	        		{
	        			finalWeather += possibleWeather.get(tmp) + " and ";
	        		}
	        	}
	        }
	        else
	        {
	        	finalWeather = "Undefined";
	        }
	        
	        //Add to the locationObject array
	        locations.add(new locationObject(high/10, low/10, finalWeather, zipcodes.get(k).getAsString(),imageUrls.get(k).getAsString()));
	        }
	        // Close the http client and return the arraylist of strings.
	        httpclient.getConnectionManager().shutdown();
	        return null;
		}
		
		@Override
		protected void onPostExecute(Void k)
		{
			//Used to set the button listener and call the next asynctask.
			super.onPostExecute(k);
			Toast.makeText(getApplicationContext(), "Ready.", Toast.LENGTH_SHORT).show();
			submit.setOnClickListener(null);
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
						selectedConditions = dropdown.getItemAtPosition(dropdown.getSelectedItemPosition()).toString();
						highTemperature = Integer.parseInt(hightemp);
						lowTemperature = Integer.parseInt(lowtemp);
						@SuppressWarnings("unused")
						AsyncTask<Void, Void, Void> doIt = new DoWork().execute();
					}
				}
				
				
			});
		}
	}
	
	@SuppressLint("DefaultLocale")
	class DoWork extends AsyncTask<Void, Void, Void> 
	{
		
		// Method called to do all the work in the background	
		@SuppressLint("DefaultLocale")
		@Override
		protected Void doInBackground(Void... voids)
		{
			//Gets the places that match the user's conditions
			int weatherMod = 10;
			chosenPlaces = getCorrectPlaces(weatherMod);
			while(weatherMod < 20 && chosenPlaces.size() == 0)
			{
				weatherMod += 5;
				chosenPlaces.clear();
				chosenPlaces = getCorrectPlaces(weatherMod);
			}
			
			//request to the ziptastic api to get the city name
			HttpClient httpclient = new DefaultHttpClient();  
	        HttpGet request;   
	        ResponseHandler<String> handler = new BasicResponseHandler();
	        String result = null;
	        String zipUrl = "http://www.ziptasticapi.com/";
	        JsonParser jsp = new JsonParser();
			for(int val = 0; val < chosenPlaces.size(); val++)
			{
				if(val < chosenPlaces.size() * .60)
					publishProgress();
				request = new HttpGet(zipUrl + chosenPlaces.get(val).getZipcode());
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
				
				//Sets the cityname of the location object.
				JsonElement zipRoot = jsp.parse(result);
		        JsonObject zipObj = zipRoot.getAsJsonObject();
		        String cityname = zipObj.get("city").getAsString() + ", " + zipObj.get("state").getAsString();
		        chosenPlaces.get(val).setCityName(cityname);
				
			}
			httpclient.getConnectionManager().shutdown();
			return null;
		}
		
		//Produces an update message for the user.
		protected void onProgressUpdate (Void... values)
		{
			Toast.makeText(getApplicationContext(), "Finding acceptable locations", Toast.LENGTH_SHORT).show();
		}
		
		//Modifies the listview to contain the new information.
		@Override
		protected void onPostExecute(Void vo)
		{
			super.onPostExecute(vo);
			lv.setVisibility(View.INVISIBLE);
			if(chosenPlaces.size() == 0)
			{
				Toast.makeText(getApplicationContext(), "No locations match your criteria", Toast.LENGTH_SHORT).show();
			}
			else
			{
				conditionsArray.clear();
				hightempArray.clear();
				lowtempArray.clear();
				citynameArray.clear();
				for(int s = 0; s < chosenPlaces.size(); s++)
				{
					conditionsArray.add(chosenPlaces.get(s).getConditions());
					hightempArray.add(Integer.toString(chosenPlaces.get(s).getHighTemp()));
					lowtempArray.add(Integer.toString(chosenPlaces.get(s).getLowTemp()));
					citynameArray.add(chosenPlaces.get(s).getCityName());
				}
				adapter = new CustomAdapter();
				lv.setAdapter(adapter);
				lv.setVisibility(View.VISIBLE);
				
				//Set the listivew to be clickable, and to start an intent to a different activity on click.
				lv.setOnItemClickListener(new OnItemClickListener() {
					public void onItemClick(AdapterView<?> parent, View view,int position, long id) 
				    {
						Intent web = new Intent(getApplicationContext(), Image.class);
						web.putExtra("url", chosenPlaces.get(position).getImageUrl());
						web.putExtra("text", chosenPlaces.get(position).getCityName());
						startActivity(web);
				    }});
				
			}
		}
		
		//This method checks all the location objects to see if any of them match the user-specified conditions.
		public ArrayList<locationObject> getCorrectPlaces(int weatherMod)
		{
			ArrayList<locationObject> returnVal = new ArrayList<locationObject>();
			for(int l = 0; l < locations.size(); l++)
			{
				locationObject temp = locations.get(l);
				int val = temp.getHighTemp();
				int val2 = temp.getLowTemp();
				if(val + weatherMod >= highTemperature && val - weatherMod <= highTemperature)
				{
					Log.i("hmm", "High temp within range");
					if(val2 + weatherMod >= lowTemperature && val2 - weatherMod <= lowTemperature)
					{
						Log.i("hmm", "low temp within range");
						if(temp.getConditions().contains(selectedConditions))
						{
							Log.i("hmm", "Conditions match");
							returnVal.add(temp);
						}
					}
				}
			}
			return returnVal;
		}
	}
	
	//Custom adapter for the listview
	class CustomAdapter extends ArrayAdapter<String> 
	{
		//Constructor
		CustomAdapter() 
		{
			super(MainActivity.this, R.layout.listrow, R.id.zipcodeText, citynameArray);
		}
		
		//Method which will populate the view
		@Override
		public View getView(int pos, View convertView, ViewGroup parent)
		{
			View row = super.getView(pos, convertView, parent);
			TextView ht = (TextView) row.findViewById(R.id.highTempText);
			ht.setText("High: " + hightempArray.get(pos));
			TextView lt = (TextView) row.findViewById(R.id.lowTempText);
			lt.setText("Low: " + lowtempArray.get(pos));
			TextView con = (TextView) row.findViewById(R.id.conditionsText);
			con.setText(conditionsArray.get(pos));
			return row;
		}
	}
}
