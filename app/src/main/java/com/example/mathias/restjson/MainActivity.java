package com.example.mathias.restjson;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import android.util.Log;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final ReadJSONFeedTask task = new ReadJSONFeedTask();
        task.execute(" http://rest-service.guides.spring.io/greeting");
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            try{
                return readJSonFeed(urls[0]);
            }
            catch (IOException ex){
                Log.e("Hello World", ex.toString());
                cancel(true);
                return ex.toString();
            }
        }
        @Override
        protected void onPostExecute(String result){
            final TextView textView = findViewById(R.id.mainResultTextView);
            try{
                JSONObject jsonObject = new JSONObject(result);
                final int id = jsonObject.getInt("id");
                final String content = jsonObject.getString("content");
                textView.append(id + ": " + content + "\n");


            } catch (JSONException ex){
                textView.append(ex.toString());
            }
        }

        @Override
        protected void onCancelled(String message){
            super.onCancelled(message);
            final TextView textView = findViewById(R.id.mainResultTextView);
            textView.setText(message);
        }
    }
    private String readJSonFeed(String urlString) throws IOException{
        StringBuilder stringBuilder = new StringBuilder();
        final InputStream content = openHttpConnection(urlString);
        final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        while (true){
            final String line = reader.readLine();
            if (line == null)
                break;
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }
    private InputStream openHttpConnection(final String urlString)
        throws IOException{
        final URL url = new URL(urlString);
        final URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an http connection");
        final HttpURLConnection httpConn = (HttpURLConnection) conn;
        httpConn.setAllowUserInteraction(false);
        httpConn.setInstanceFollowRedirects(true);
        httpConn.setRequestMethod("GET");
        httpConn.connect();
        final int response = httpConn.getResponseCode();
        if(response == HttpURLConnection.HTTP_OK){
            return  httpConn.getInputStream();
        } else {
            throw new IOException("HTTP response not OK");
        }
    }

}

