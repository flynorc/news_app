package com.flynorc.a08_newsapp;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * Created by Flynorc on 20-May-17.
 */

public class NewsLoader extends AsyncTaskLoader<List<Article>> {

    private String url;
    private Context context;

    public NewsLoader(Context context, String url) {
        super(context);
        this.url = url;
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Article> loadInBackground() {
        if(url.isEmpty()) {
            return null;
        }

        return QueryUtils.getNewsFromApi(url, context);
    }
}
