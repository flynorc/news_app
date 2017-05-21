package com.flynorc.a08_newsapp;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Flynorc on 20-May-17.
 * A class holding most helper methods to get data from API and parse it to format we can use in other parts of the App
 */

public class QueryUtils {
    private static final String KEY_URL = "webUrl";
    private static final String KEY_FIELDS = "fields";
    private static final String KEY_AUTHOR = "byline";
    private static final String KEY_TITLE = "webTitle";
    private static final String KEY_RESULTS = "results";
    private static final String KEY_RESPONSE = "response";
    private static final String KEY_SECTION = "sectionName";
    private static final String KEY_PUBLISHED = "webPublicationDate";


    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    /**
     * static method to retrieve data from the guardian news API and return it as ArrayList of Article objects
     */
    public static ArrayList<Article> getNewsFromApi(String requestUrl, final Context context) {
        URL url = createUrl(requestUrl, context);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url, context);
        } catch (IOException e) {
            Log.e("GET NEWS", "Error closing input stream", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        //extract the news from the string that was returned from the API
        return extractNews(jsonResponse, context);
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl, Context context) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("create url", "Error with creating URL ", e);
            Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
            toast.show();
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     * as learned in the course
     */
    private static String makeHttpRequest(URL url, final Context context) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("make request", "Error response code: " + urlConnection.getResponseCode());

                //use a handler to create a toast from the background thread
                Handler handler =  new Handler(context.getMainLooper());
                handler.post( new Runnable(){
                    public void run(){
                        Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                        toast.show();
                    }
                });
            }
        } catch (IOException e) {
            Log.e("make request", "Problem retrieving the news JSON results.", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
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
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<Article> extractNews(String response, final Context context) {
        // Create an empty ArrayList that we can start adding earthquakes to
        ArrayList<Article> articles = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject data =  new JSONObject(response);
            JSONObject responseObject = data.getJSONObject(KEY_RESPONSE);
            JSONArray results = responseObject.getJSONArray(KEY_RESULTS);

            //parse every item and if parsing is successful it is added to the list
            for(int i=0; i < results.length(); i++) {
                JSONObject articleJSON = results.getJSONObject(i);
                articles.add(parseArticleJSON(articleJSON, context));


            }
        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);

            //use a handler to create a toast from the background thread
            Handler handler =  new Handler(context.getMainLooper());
            handler.post( new Runnable(){
                public void run(){
                    Toast toast = Toast.makeText(context, R.string.error_networking_operation,Toast.LENGTH_LONG);
                    toast.show();
                }
            });
        }

        // Return the list of articles (that were successfully parsed)
        return articles;
    }

    /*
     * helper function to parse the attributes of an article from the JSON object and return an Article object
     */
    private static Article parseArticleJSON(JSONObject articleJSON, Context context) {

        //parse the title, section and url straight from article json
        String title = articleJSON.optString(KEY_TITLE, "");
        String section = articleJSON.optString(KEY_SECTION, "");
        String url = articleJSON.optString(KEY_URL, "");

        //parse the author by accessing the "extra fields"
        String author = context.getString(R.string.default_author);
        JSONObject fields = articleJSON.optJSONObject(KEY_FIELDS);
        if(fields != null) {
            author = fields.optString(KEY_AUTHOR, "");
        }

        //parse the date from the string
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date published = null;
        try {
            published = dateFormat.parse(articleJSON.optString(KEY_PUBLISHED));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return new Article(title, section, published, author, url);
    }
}
