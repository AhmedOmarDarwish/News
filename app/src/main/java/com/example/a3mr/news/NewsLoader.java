package com.example.a3mr.news;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

import static com.example.a3mr.news.QueryUtils.LOG_TAG;

public class NewsLoader extends AsyncTaskLoader<List<String[]>> {
    /**
     * Query URL
     */
    private String mUrl;

    public NewsLoader(Context context, String url) {
        super( context );
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i( LOG_TAG,"TEST: onStartLoading() called ..." );
        forceLoad();
    }


    @Override
    public List <String[]> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        List <String[]> news = QueryUtils.fetchNewsData( mUrl );
        return news;
    }
}

