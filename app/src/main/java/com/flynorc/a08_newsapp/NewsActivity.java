package com.flynorc.a08_newsapp;

import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Article>> {
    //constants
    private static final int NEWS_LOADER_ID = 1;
    private static final String BASE_URL = "http://content.guardianapis.com/search";

    //variables
    private List<Article> articles;
    private TextView noResultsView;
    private ProgressBar loadingSpinner;
    private LoaderManager loaderManager;
    private EmptyRecyclerView newsListView;
    private RecyclerAdapter recyclerAdapter;
    private ConnectivityManager connectivityManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        //store reference to connectivity manager
        connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //get the reference to the layout elements we need to manipulate
        noResultsView = (TextView) findViewById(R.id.no_results);
        loadingSpinner = (ProgressBar) findViewById(R.id.results_loading);
        //get the reference to the list for news results that match the search
        newsListView = (EmptyRecyclerView) findViewById(R.id.news_result_list);

        //set LayoutManager to be LinearLayoutManager for the RecyclerView
        LinearLayoutManager llm = new LinearLayoutManager(this);
        newsListView.setLayoutManager(llm);
        newsListView.setEmptyView(noResultsView);

        //use the custom recycler adapter on the recyclerView
        articles = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(articles);
        newsListView.setAdapter(recyclerAdapter);

        // Get a reference to the LoaderManager, in order to interact with loaders.
        loaderManager = getLoaderManager();

        //call the function that delegates the dirty work of getting the news on to other functions
        fetchNews();
    }

    /*
     * adding the settings to the menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        if (id == R.id.action_refresh) {
            loaderManager.restartLoader(NEWS_LOADER_ID, null, this);
            fetchNews();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*
     * helper method for fetching news
     */
    private void fetchNews() {
        //make sure user has internet access
        if(checkConnectivity()) {
            // show loading spinner
            loadingSpinner.setVisibility(View.VISIBLE);

            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), R.string.no_internet_toast,Toast.LENGTH_LONG);
            toast.show();
        }
    }

    /*
     * helper method to check if user has internet access
     */
    private boolean checkConnectivity() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /*
     * implementation of LoaderManager.LoaderCallbacks interface
     */
    @Override
    public Loader<List<Article>> onCreateLoader(int id, Bundle args) {

        //get the number of results and query from shared preferences
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String nrResults = sharedPrefs.getString( getString(R.string.settings_nr_results_key), getString(R.string.settings_nr_results_default));
        String query = sharedPrefs.getString( getString(R.string.settings_query_key), getString(R.string.settings_query_default));

        //build up the URL from BASE_URL, the parameters from the settings and some herdcoded parameters such as api-key and show-fields
        Uri baseUri = Uri.parse(BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter("api-key", "test");
        uriBuilder.appendQueryParameter("show-fields", "byline");
        uriBuilder.appendQueryParameter("q", query);
        uriBuilder.appendQueryParameter("page-size", nrResults);

        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Article>> loader, List<Article> data) {
        //hide the spinner
        loadingSpinner.setVisibility(View.GONE);
        //pass the results (or an empty ArrayList) to the recyclerAdapter
        if (data == null) {
            recyclerAdapter.setArticles(new ArrayList<Article>());
        }
        else {
            recyclerAdapter.setArticles(data);
        }

        //notify the adapter to take the changes
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<List<Article>> loader) {
        recyclerAdapter.setArticles(new ArrayList<Article>());
        recyclerAdapter.notifyDataSetChanged();
    }
}
