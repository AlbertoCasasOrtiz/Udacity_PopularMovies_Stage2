package com.movies.popular.albert.popularmovies;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.movies.popular.albert.popularmovies.data.MoviesContract;
import com.movies.popular.albert.popularmovies.networkutils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.movies.popular.albert.popularmovies.networkutils.NetworkUtils.getResponseFromHttpURL;

/**
 * Created by Alberto Casas Ortiz.
 */
public class MainActivity extends AppCompatActivity implements GridAdapter.ItemListenerClick, LoaderManager.LoaderCallbacks<String>{
    //ID of Loader.
    private final static int LOADER_ID = 23423;
    //Key for saving instance of sorting method boolean.
    private final static String SORTING_KEY = "sorting_key";

    //Arrays to save movie data and posters path.
    public ArrayList<String> posters;
    public ArrayList<JSONObject> movies;

    ///Number of items in the recyclerView.
    public int numItems;

    //Enum with possible sorting methods.
    private enum SortMethod{POPULAR, TOP_RATED, FAVORITE}
    private SortMethod sort;

    //Number of columns in the gridView.
    private static final int NUM_COLUMNS_PORTRAIT = 2;
    private static final int NUM_COLUMNS_LANDSCAPE = 3;

    //Adapter and RecyclerView.
    private GridAdapter adapter;
    private RecyclerView recyclerView;

    //Error message if connection fails.
    private TextView mErrorConnection;
    //ProgressBar to show during connection time.
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize elements.
        mErrorConnection = (TextView) findViewById(R.id.tv_error_connection);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);

        posters = new ArrayList<>();
        movies = new ArrayList<>();

        recyclerView = (RecyclerView) findViewById(R.id.rv_movies);

        //Set recycler view.
        GridLayoutManager layout;

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            layout = new GridLayoutManager(MainActivity.this, NUM_COLUMNS_PORTRAIT);
        }
        else{
            layout = new GridLayoutManager(MainActivity.this, NUM_COLUMNS_LANDSCAPE);
        }
        recyclerView.setLayoutManager(layout);

        recyclerView.setHasFixedSize(true);


        //Restore if savedInstance.
        if(savedInstanceState != null ){
            if(savedInstanceState.containsKey(SORTING_KEY)) {
                sort = SortMethod.values()[savedInstanceState.getInt(SORTING_KEY)];
                getSupportLoaderManager().restartLoader(LOADER_ID, new Bundle(), this);
            }
        } else {
            sort = SortMethod.POPULAR;
            getSupportLoaderManager().initLoader(LOADER_ID, new Bundle(), this);
            adapter = new GridAdapter(numItems, posters, this);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemClicked = item.getItemId();
        boolean res;

        //Get Loader.
        Bundle queryBundle = new Bundle();
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> searchLoader = loaderManager.getLoader(LOADER_ID);

        switch (itemClicked){
            case R.id.action_sort_popular:
                res = true;
                sort = SortMethod.POPULAR;
                //Start loader
                if (searchLoader == null) {
                    loaderManager.initLoader(LOADER_ID, queryBundle, this);
                } else {
                    loaderManager.restartLoader(LOADER_ID, queryBundle, this);
                }
                break;
            case R.id.action_sort_top_rated:
                res = true;
                sort = SortMethod.TOP_RATED;
                //Start loader
                if (searchLoader == null) {
                    loaderManager.initLoader(LOADER_ID, queryBundle, this);
                } else {
                    loaderManager.restartLoader(LOADER_ID, queryBundle, this);
                }
                break;
            //LOAD DATA FROM DATABASE.
            case R.id.action_sort_favorites:
                res = true;
                sort = SortMethod.FAVORITE;
                //Start loader
                if (searchLoader == null) {
                    loaderManager.initLoader(LOADER_ID, queryBundle, this);
                } else {
                    loaderManager.restartLoader(LOADER_ID, queryBundle, this);
                }
                break;
            //LOAD DATA FROM DATABASE.
            default:
                res = super.onOptionsItemSelected(item);
                break;
        }

        return res;
    }

    @Override
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        state.putInt(SORTING_KEY, sort.ordinal());
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore state from the savedInstanceState.
        sort = SortMethod.values()[savedInstanceState.getInt(SORTING_KEY)];
    }

    /**
     * With the string json returned by the http connection, get he data and store it in the arrays.
     * @param json JSON from the http connection.
     */
    public void extractJSONData(String json){
        try {
            //Delete previous values.
            posters.clear();
            movies.clear();
            JSONObject object = new JSONObject(json);
            //Extract result object.
            JSONArray arrayMovies = object.getJSONArray("results");
            //Insert values in arrays.
            for(int i = 0; i < arrayMovies.length(); i++){
                JSONObject movie = arrayMovies.getJSONObject(i);
                movies.add(movie);
                posters.add(movie.getString("poster_path"));
            }

        } catch (JSONException e) {
            Log.e("ERROR", "Failed proccessing JSON.");
            e.printStackTrace();

        }
    }

    /**
     * Refresh the data in the GUI.
     */
    public void refresh(){
        numItems = movies.size();

        //Update adapter.
        adapter = new GridAdapter(numItems, posters, this);
        recyclerView.setAdapter(adapter);
    }

    /**
     * Show posters and not show error.
     */
    public void showPosters() {
        mErrorConnection.setVisibility(View.INVISIBLE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * Show error and not show posters.
     */
    public void showErrorConnection(){
        mErrorConnection.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {

        return new AsyncTaskLoader<String>(this) {

            String moviesJson;

            @Override
            public void deliverResult(String data) {
                moviesJson = data;
                super.deliverResult(data);
            }

            @Override
            protected void onStartLoading() {
                if (moviesJson != null) {
                    deliverResult(moviesJson);
                } else {
                    forceLoad();
                    progressBar.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public String loadInBackground() {
                boolean sortBool = sort == SortMethod.FAVORITE;
                String response = null;
                //If we want to load favorite movies...
                if(sortBool){
                    try {
                        //Make query
                        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().build(), null, null, null, null);

                        if (cursor != null) {
                            cursor.moveToFirst();

                            String movieId;
                            String arrayMovies = "[";
                            URL url;

                            //Get movies from URL and store in string representing a json object.
                            for(int i = 0; i < cursor.getCount(); i++) {
                                movieId = cursor.getString(cursor.getColumnIndex(MoviesContract.MoviesEntry.COLUMN_MOVIEDB_ID));
                                url = NetworkUtils.singleMovieURL(movieId);
                                try {
                                    arrayMovies += getResponseFromHttpURL(url) + (i == cursor.getCount()-1 ? "" : ", ");
                                } catch (IOException e) {
                                    Log.e("ERROR", "Failed to load asynchronous data");
                                    e.printStackTrace();
                                    return null;
                                }
                                cursor.moveToNext();
                            }
                            arrayMovies += "]";
                            cursor.close();

                            //Convert string into JSONArray and format to be proccessed later.
                            JSONArray results = new JSONArray(arrayMovies);
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("results", results);
                            response = jsonObject.toString();

                        }
                        return response;
                    } catch (JSONException e){
                        Log.e("ERROR", "Failed to load asynchronous data");
                        e.printStackTrace();
                        return null;
                    }
                //If sort method is popular or top rated...
                }else {
                    //Get movies from URL and return.
                    sortBool = sort == SortMethod.POPULAR;
                    URL url = NetworkUtils.moviesURL(sortBool);
                    response = "";
                    try {
                        response = getResponseFromHttpURL(url);
                    } catch (IOException e) {
                        Log.e("ERROR", "Failed to load asynchronous data");
                        e.printStackTrace();
                        return null;
                    }
                }
                return response;
            }

            @Override
            protected void onStopLoading() {
                super.onStopLoading();
                progressBar.setVisibility(View.INVISIBLE);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        progressBar.setVisibility(View.INVISIBLE);
        if(data != null && !TextUtils.isEmpty(data)){
            showPosters();
            extractJSONData(data);
            //Refresh with extracted data.
            refresh();
        }else{
            showErrorConnection();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
        //Do nothing.
    }

    @Override
    public void onItemClick(int clickedItemIndex) {
        //Store movie in the intent and call MovieDetailActivity.
        Intent intent = new Intent(MainActivity.this, MovieDetailActivity.class);
        intent.putExtra(Intent.EXTRA_TEXT, movies.get(clickedItemIndex).toString());
        startActivity(intent);
    }

}
