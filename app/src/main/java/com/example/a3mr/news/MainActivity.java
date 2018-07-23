package com.example.a3mr.news;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderCallbacks <List <String[]>> {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String USGS_REQUEST_URL = "https://content.guardianapis.com/search?tag=politics%2Fpolitics&from-date=2017-01-01&show-tags=contributor&q=debate&api-key=" + BuildConfig.API_Key;
    ListView listView;
    private ArrayAdapter <String[]> adapter;
    private ProgressBar progressBar;
    private TextView error;
    public static ArrayList <String[]> newsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_main );
        progressBar = findViewById( R.id.loading_indicator );
        error = findViewById( R.id.empty_view );
        newsList = new ArrayList <>();
        listView = findViewById( R.id.list );
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService( Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();


        adapter =
                new ArrayAdapter <String[]>( this, android.R.layout.simple_list_item_2, android.R.id.text1, newsList ) {
                    @NonNull
                    @Override
                    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                        convertView = super.getView( position, convertView, parent );
                        String[] currentString = newsList.get( position );
                        TextView textView1 = convertView.findViewById( android.R.id.text1 );
                        TextView textView2 = convertView.findViewById( android.R.id.text2 );
                        textView1.setTextColor( ContextCompat.getColor( getContext(), R.color.colorAccent ) );
                        textView2.setTextColor( ContextCompat.getColor( getContext(), R.color.colorPrimary ) );
                        textView1.setText( currentString[0] );
                        textView2.setText( currentString[1] );
                        return convertView;
                    }
                };
        listView.setAdapter( adapter );
        listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView <?> adapterView, View view, int i, long l) {
                String[] urlString = newsList.get( i );
                Uri uri = Uri.parse( urlString[2] );
                Intent intent = new Intent( Intent.ACTION_VIEW, uri );
                if (intent.resolveActivity( getPackageManager() ) != null) {
                    startActivity( intent );
                }
            }
        } );
        listView.setEmptyView( progressBar );

        if (networkInfo != null && networkInfo.isConnected()) {
            getLoaderManager().initLoader( 0, null, this ).forceLoad();
        } else {
            error.setText( R.string.no_internet_connection );
            progressBar.setVisibility( View.GONE );
        }
    }


    @Override
    public Loader <List <String[]>> onCreateLoader(int i, Bundle bundle) {
        return new NewsLoader( MainActivity.this, USGS_REQUEST_URL );
    }

    @Override
    public void onLoadFinished(Loader <List <String[]>> loader, List <String[]> strings) {
        error.setText( R.string.no_news );
        progressBar.setVisibility( View.GONE );
        adapter.clear();
        if (strings != null && !strings.isEmpty()) {
            error.setVisibility( View.GONE );
            adapter.addAll( strings );
        }

    }

    @Override
    public void onLoaderReset(Loader <List <String[]>> loader) {
        error.setVisibility( View.GONE );
        adapter.clear();
    }
}