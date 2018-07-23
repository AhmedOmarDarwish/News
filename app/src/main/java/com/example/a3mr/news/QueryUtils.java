package com.example.a3mr.news;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static java.net.HttpURLConnection.HTTP_OK;

public final class QueryUtils {
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    public static List <String[]> fetchNewsData(String requestUrl) {
        try {
            Thread.sleep( 2000 );
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl( requestUrl );

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest( url );
        } catch (IOException e) {
            Log.e( LOG_TAG, "Problem making the HTTP request.", e );
        }

        List <String[]> news = extractNewsFromJson( jsonResponse );

        return news;
    }
    /**
     * Returns new URL object from the given string URL.
     */
    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL( stringUrl );
        } catch (MalformedURLException e) {
            Log.e( LOG_TAG, "Problem building the URL ", e );
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout( 10000 /* milliseconds */ );
            urlConnection.setConnectTimeout( 15000 /* milliseconds */ );
            urlConnection.setRequestMethod( "GET" );
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == HTTP_OK ) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream( inputStream );
            } else {
                Log.e( LOG_TAG, "Error response code: " + urlConnection.getResponseCode() );
            }
        } catch (IOException e) {
            Log.e( LOG_TAG, "Problem retrieving the news JSON results.", e );
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader( inputStream, Charset.forName( "UTF-8" ) );
            BufferedReader reader = new BufferedReader( inputStreamReader );
            String line = reader.readLine();
            while (line != null) {
                output.append( line );
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private static List <String[]> extractNewsFromJson(String newsJSON) {
        if (TextUtils.isEmpty( newsJSON )) {
            return null;
        }
        List <String[]> news = new ArrayList <>();
        try {
            JSONObject baseJsonResponse = new JSONObject( newsJSON );
            JSONObject jsonObject = baseJsonResponse.getJSONObject( "response" );
            JSONArray results = jsonObject.optJSONArray( "results" );

            for (int i = 0; i < results.length(); i++) {
                JSONObject currentNews = results.getJSONObject( i );
                String firstString = currentNews.getString( "sectionName") ;
                String secondString = currentNews.getString( "webTitle" );
                String pageURL = currentNews.getString( "webUrl" );
                news.add( new String[]{firstString, secondString, pageURL} );
            }

        } catch (JSONException e) {
            Log.e( "QueryUtils", "Problem parsing the news JSON results", e );
        }

        return news;
    }

}

