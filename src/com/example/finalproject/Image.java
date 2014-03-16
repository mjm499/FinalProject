package com.example.finalproject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

//Class used to display an ImageView
public class Image extends Activity {

	ImageView im;
	Bitmap bmp;
	String url;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_image);
		
		//Get the url from the Intent, and download the image
		url = getIntent().getExtras().getString("url");
		TextView tv = (TextView) findViewById(R.id.ImageText);
		String text = getIntent().getExtras().getString("text");
		tv.setText("You've chosen: " + text);
		im = (ImageView) findViewById(R.id.thumbnail);
		
		@SuppressWarnings("unused")
		AsyncTask<Void, Void, Void> getPic = new GetInfo().execute();
	}
	
	//Class used to download an image from the internet
	class GetInfo extends AsyncTask<Void, Void, Void> 
	{
		// Method called to do all the work in the background	
		@Override
		protected Void doInBackground(Void...voids)
		{
			bmp = downloadImage(url);
			return null;
		}
		protected void onPostExecute(Void v)
		{
			im.setImageBitmap(bmp);
		}

	}
	
	//Methods from the class notes used to download an image.
	private Bitmap downloadImage(String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            stream = getHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return bitmap;
    }

    // Makes HttpURLConnection and returns InputStream
    private InputStream getHttpConnection(String urlString)
            throws IOException {
        InputStream stream = null;
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();

        try {
            HttpURLConnection httpConnection = (HttpURLConnection) connection;
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();

            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return stream;
    }
}
