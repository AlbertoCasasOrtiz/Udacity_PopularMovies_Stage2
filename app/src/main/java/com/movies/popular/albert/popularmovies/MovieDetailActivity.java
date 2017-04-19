package com.movies.popular.albert.popularmovies;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.movies.popular.albert.popularmovies.data.MoviesContract;
import com.movies.popular.albert.popularmovies.networkutils.NetworkUtils;
import com.squareup.picasso.Picasso;

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
public class MovieDetailActivity extends AppCompatActivity  implements MovieDetailAdapterTrailers.ItemListenerClick, LoaderManager.LoaderCallbacks<String[]>{

    //Arrays to save trailersURL and reviewsURL data.
    private ArrayList<String> trailersId;
    private ArrayList<String> trailersName;

    private ArrayList<String> reviewsAuthor;
    private ArrayList<String> reviewsContent;

    //Switch of favorite movie.
    private Switch swFavorite;

    //ID and title of the movie.
    private String id;
    private String title;

    //Error message if connection fails.
    private TextView mErrorConnection;
    private TextView mErrorTrailers;
    private TextView mErrorReviews;

    //ProgressBar to show during connection time.
    private ProgressBar progressBar;

    //ConstraintLayout of the activity.
    private ConstraintLayout cl_movie_detail;

    //Adapter of trailersURL.
    private int numItemsAdapterTrailer;
    private MovieDetailAdapterTrailers adapterTrailers;
    private RecyclerView recyclerViewTrailers;

    //Adapter of reviewsURL.
    private int numItemsAdapterReviews;
    private MovieDetailAdapterReviews adapterReviews;
    private RecyclerView recyclerViewReviews;

    //ID of the loader.
    public final static int LOADER_ID = 5432345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        //Set display home as up.
        ActionBar actionBar = this.getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        //Setup recycler view of trailersURL.
        this.recyclerViewTrailers = (RecyclerView) findViewById(R.id.rv_trailers);

        GridLayoutManager layoutTrailers = new GridLayoutManager(this, 1);
        this.recyclerViewTrailers.setLayoutManager(layoutTrailers);

        this.recyclerViewTrailers.setHasFixedSize(true);
        this.adapterTrailers = new MovieDetailAdapterTrailers(numItemsAdapterTrailer, trailersName, this);
        this.recyclerViewTrailers.setAdapter(adapterTrailers);


        //Setup recycler view of reviewsURL.
        this.recyclerViewReviews = (RecyclerView) findViewById(R.id.rv_reviews);

        GridLayoutManager layoutReviews = new GridLayoutManager(this, 1);
        this.recyclerViewReviews.setLayoutManager((layoutReviews));

        this.recyclerViewReviews.setHasFixedSize(true);
        this.adapterReviews = new MovieDetailAdapterReviews(numItemsAdapterReviews, reviewsAuthor, reviewsContent);
        this.recyclerViewReviews.setAdapter(adapterReviews);


        //Initialize elements.
        this.trailersId = new ArrayList<>();
        this.trailersName = new ArrayList<>();
        this.reviewsAuthor = new ArrayList<>();
        this.reviewsContent = new ArrayList<>();

        this.mErrorConnection = (TextView) findViewById(R.id.tv_error_connection_details);
        this.mErrorReviews = (TextView) findViewById(R.id.tv_error_reviews);
        this.mErrorTrailers = (TextView) findViewById(R.id.tv_error_trailers);

        this.progressBar = (ProgressBar) findViewById(R.id.progress_bar_details);
        this.cl_movie_detail = (ConstraintLayout) findViewById(R.id.cl_movie_detail);

        //Get data from Intent.
        Intent intentThatStartedThisActivity = getIntent();

        //If there is data, initialize GUI, show error otherwise.
        if(intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)){
            showInformation();
            initializeGUI();
            getSupportLoaderManager().initLoader(LOADER_ID, new Bundle(), this);
        } else{
            showError();
        }
    }

    /**
     * Initialize GUI elements with movie information.
     */
    public void initializeGUI(){
        //Elements from GUI used locally.
        ImageView ivPoster;
        TextView tvTitle;
        TextView tvReleaseDate;
        TextView tvVoteAverage;
        TextView tvPlotSynopsis;

        //Initialize elements from GUI.
        ivPoster = (ImageView) findViewById(R.id.movie_poster);
        tvTitle = (TextView) findViewById(R.id.movie_title);
        tvReleaseDate = (TextView) findViewById(R.id.movie_release_date);
        tvVoteAverage = (TextView) findViewById(R.id.movie_vote_average);
        tvPlotSynopsis = (TextView) findViewById(R.id.movie_plot_synopsis);
        this.swFavorite = (Switch) findViewById(R.id.favoriteSwitch);

        //GEt data from Intent.
        Intent intentThatStartedThisActivity = getIntent();

        //String to store data.
        String movieJSONString = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
        String poster_path = null;
        this.title = null;
        String release_date = null;
        String vote_average = null;
        String overview = null;
        this.id = null;
        //Extract data from JSON.
        try {
            JSONObject movieJSON = new JSONObject(movieJSONString);
            poster_path = movieJSON.getString("poster_path");
            this.title = movieJSON.getString("title");
            release_date = movieJSON.getString("release_date");
            vote_average = movieJSON.getString("vote_average");
            overview = movieJSON.getString("overview");
            this.id = movieJSON.getString("id");
        } catch (JSONException e) {
            Log.e("ERROR", "Failed Proccessing JSON");
            e.printStackTrace();
        }

        //Append data to GUI.
        Picasso.with(ivPoster.getContext()).load(NetworkUtils.imageURL(poster_path).toString()).into(ivPoster);
        tvTitle.setText(title);
        if (release_date != null) {
            tvReleaseDate.setText(release_date.split("-")[0]);
        }
        tvVoteAverage.setText(vote_average);
        tvPlotSynopsis.setText(overview);

        //Test in database if is favorite or not.
        boolean isFav = false;
        Cursor cursor = getContentResolver().query(MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(id).build(), null, null, null, null);
        if (cursor != null) {
            isFav = cursor.getCount() > 0;
            cursor.close();
        }

        //Set favorite checked if the movie is favorite.
        this.swFavorite.setChecked(isFav);

        //Set on click listener for switch to insert in database.
        this.swFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean clicked = swFavorite.isChecked();
                swFavorite.setChecked(clicked);
                switchFavorite(id, clicked);
            }
        });

    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == android.R.id.home){
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Set movie as favorite or delete from favorites.
     * @param id ID of the movie.
     * @param value true if the movie is a new favorite and false otherwise.
     */
    public void switchFavorite(String id, boolean value){
        ContentValues contentValues = new ContentValues();

        //If value is true, insert into database.
        if(value) {
            //Put data in Content Values.
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_MOVIEDB_ID, id);
            contentValues.put(MoviesContract.MoviesEntry.COLUMN_TITLE, title);


            //Insert data.
            Uri uriInsert = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().build();
            Uri uri = getContentResolver().insert(uriInsert, contentValues);

            //If error, show toast indicating error.
            if (uri == null) {
                Toast.makeText(this, "Error saving data into database.", Toast.LENGTH_LONG).show();
                swFavorite.setChecked(false);
            }
        }
        //If value is false, delete from database.
        else {
            //Delete data.
            Uri uriDelete = MoviesContract.MoviesEntry.CONTENT_URI.buildUpon().appendPath(id).build();
            int deleted = getContentResolver().delete(uriDelete, null, null);

            //If error, show toast indicating error.
            if(deleted == 0){
                Toast.makeText(this, "Error deleting data from database.", Toast.LENGTH_LONG).show();
                swFavorite.setChecked(true);
            }
        }

    }

    /**
     * Get ID of a movie.
     * @return ID of the movie.
     */
    private String getIdMovie(){
        return this.id;
    }


    /**
     * Extract data of trailersURL from the JSON an save in arrays.
     * @param json JSON String containing data.
     */
    public void extractJSONDataTrailers(String json){
        try {
            trailersId.clear();
            trailersName.clear();
            JSONObject object = new JSONObject(json);
            //Extract result object.
            JSONArray arrayMovies = object.getJSONArray("results");
            for(int i = 0; i < arrayMovies.length(); i++){
                JSONObject movie = arrayMovies.getJSONObject(i);
                String type = movie.getString("type");
                //Insert values in arrays if the video is a trailer.
                if(type.compareTo("Trailer") == 0) {
                    trailersId.add(movie.getString("key"));
                    trailersName.add(movie.getString("name"));
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extract data of reviewsURL from the JSON an save in arrays.
     * @param json JSON String containing data.
     */
    public void extractJSONDataReviews(String json){
        try {
            reviewsContent.clear();
            reviewsAuthor.clear();
            JSONObject object = new JSONObject(json);
            //Extract result object.
            JSONArray arrayMovies = object.getJSONArray("results");
            for(int i = 0; i < arrayMovies.length(); i++){
                JSONObject movie = arrayMovies.getJSONObject(i);
                //Insert values in arrays.
                reviewsContent.add(movie.getString("content"));
                reviewsAuthor.add(movie.getString("author"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show info of movie and not show error.
     */
    public void showInformation() {
        mErrorConnection.setVisibility(View.INVISIBLE);
        cl_movie_detail.setVisibility(View.VISIBLE);
    }

    /**
     * Show error and not show info of movie.
     */
    public void showError(){
        mErrorConnection.setVisibility(View.VISIBLE);
        cl_movie_detail.setVisibility(View.INVISIBLE);
    }


    /**
     * Show error and not show reviews.
     */
    public void showErrorReviews(){
        mErrorReviews.setVisibility(View.VISIBLE);
        recyclerViewReviews.setVisibility(View.INVISIBLE);
    }


    /**
     * Show error and not show trailers.
     */
    public void showErrorTrailers(){
        mErrorTrailers.setVisibility(View.VISIBLE);
        recyclerViewTrailers.setVisibility(View.INVISIBLE);
    }

    /**
     * Refresh the data of trailersURL in the GUI.
     */
    public void refreshTrailers(){
        numItemsAdapterTrailer = trailersName.size();

        adapterTrailers = new MovieDetailAdapterTrailers(numItemsAdapterTrailer, trailersName, this);
        recyclerViewTrailers.setAdapter(adapterTrailers);
    }

    /**
     * Refresh the data of reviewsURL in the GUI.
     */
    public void refreshReviews(){
        numItemsAdapterReviews = reviewsContent.size();

        adapterReviews = new MovieDetailAdapterReviews(numItemsAdapterReviews, reviewsAuthor, reviewsContent);
        recyclerViewReviews.setAdapter(adapterReviews);

    }


    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<String[]>(this) {

            String[] moviesJson;

            @Override
            public void deliverResult(String[] data) {
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
            public String[] loadInBackground() {
                String response[] = new String[2];

                //Get URLs to get trailers and reviews.
                URL urlTrailers = NetworkUtils.trailersURL(getIdMovie());
                URL urlReviews = NetworkUtils.reviewsURL(getIdMovie());
                //Store extracted data of trailers into array.
                try {
                    response[0] = getResponseFromHttpURL(urlTrailers);
                } catch (IOException e) {
                    response[0] = null;
                }
                //Store extracted data of reviews into array.
                try {
                    response[1] = getResponseFromHttpURL(urlReviews);
                } catch (IOException e) {
                    response[1] = null;
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
    public void onLoadFinished(Loader<String[]> loader, String[] data) {
        progressBar.setVisibility(View.INVISIBLE);
        //Extract data of trailers from JSON and show in GUI.
        if(data[0] != null && !TextUtils.isEmpty(data[0])){
            showInformation();
            extractJSONDataTrailers(data[0]);
            //Refresh with extracted data.
            refreshTrailers();
        }else{
            showErrorTrailers();
            //Error loading trailers.
        }

        //Extract data of reviews from JSON and show in GUI.
        if(data[1] != null && !TextUtils.isEmpty(data[1])){
            showInformation();
            extractJSONDataReviews(data[1]);
            //Refresh with extracted data.
            refreshReviews();
        }else{
            showErrorReviews();
            //Error loading reviews.
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        //Do nothing.
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        URL urlYoutube = NetworkUtils.youtubeURL(this.trailersId.get(clickedItemIndex));
        //Intent to youtube (or browser if not youtube existing)
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urlYoutube.toString())));

    }
}
