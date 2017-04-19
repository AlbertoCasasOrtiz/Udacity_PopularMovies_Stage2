package com.movies.popular.albert.popularmovies.networkutils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Alberto Casas Ortiz.
 */
public class NetworkUtils {
    //POSTER PARAMS
    private final static String PAGE_BASE_URL_POSTER = "http://image.tmdb.org/t/p/w185/";

    //JSON PARAMS
    private final static String PAGE_BASE_URL_POPULAR = "http://api.themoviedb.org/3/movie/popular";
    private final static String PAGE_BASE_URL_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated";
    private final static String PAGE_BASE_URL_ONE_MOVIE = "https://api.themoviedb.org/3/movie";

    //API KEY
    private final static String PARAM_KEY = "api_key";
    //Specify a key obtained from https://www.themoviedb.org/
    private final static String KEY = "";

    /**
     * Create URL to get JSON with data of popular movies or top rated movies.
     * @param popular True if the user has selected popular sort and false for top dated sort. Popular sort by default.
     * @return An URL well formed to get the JSON with data of movies.
     */
    public static URL moviesURL(boolean popular){
        URL url = null;
        Uri builtUri;
        //Create Uris for popular or best rated movies.
        if(popular) {
            builtUri = Uri.parse(PAGE_BASE_URL_POPULAR).buildUpon()
                    .appendQueryParameter(PARAM_KEY, KEY)
                    .build();
        }else {
            builtUri = Uri.parse(PAGE_BASE_URL_TOP_RATED).buildUpon()
                    .appendQueryParameter(PARAM_KEY, KEY)
                    .build();
        }
        //Generate URL.
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            Log.e("ERROR", "Failed creating URL.");
            e.printStackTrace();
            return null;
        }
        return url;
    }

    /**
     * Create URL to get JSON with data of popular movies or top rated movies.
     * @param id ID of the requested movie.
     * @return An URL well formed to get the JSON with data of movies.
     */
    public static URL singleMovieURL(String id){
        URL url = null;
        Uri builtUri;
        //Create Uris for popular or best rated movies.

        builtUri = Uri.parse(PAGE_BASE_URL_ONE_MOVIE).buildUpon()
                .appendPath(id)
                .appendQueryParameter(PARAM_KEY, KEY)
                .build();
        //Generate URL.
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            Log.e("ERROR", "Failed creating URL.");
            e.printStackTrace();
            return null;
        }
        return url;
    }

    /**
     * Create URL to get JSON with data of popular movies or top rated movies.
     * @param id ID of the requested movie.
     * @return An URL well formed to get the JSON with data of movies.
     */
    public static URL trailersURL(String id){
        URL url = null;
        Uri builtUri;
        //Create Uris for popular or best rated movies.

        builtUri = Uri.parse(PAGE_BASE_URL_ONE_MOVIE).buildUpon()
                .appendPath(id)
                .appendPath("videos")
                .appendQueryParameter(PARAM_KEY, KEY)
                .build();
        //Generate URL.
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            Log.e("ERROR", "Failed creating URL.");
            e.printStackTrace();
            return null;
        }
        return url;
    }

    /**
     * Create URL to get JSON with data of popular movies or top rated movies.
     * @param id ID of the requested movie.
     * @return An URL well formed to get the JSON with data of movies.
     */
    public static URL reviewsURL(String id){
        URL url = null;
        Uri builtUri;
        //Create Uris for popular or best rated movies.

        builtUri = Uri.parse(PAGE_BASE_URL_ONE_MOVIE).buildUpon()
                .appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter(PARAM_KEY, KEY)
                .build();
        //Generate URL.
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            Log.e("ERROR", "Failed creating URL.");
            e.printStackTrace();
            return null;
        }
        return url;
    }

    public static URL youtubeURL(String id){
        URL url = null;
        Uri builtUri;
        String baseURLYoutube = "https://www.youtube.com/watch";

        //Create Uris for popular or best rated movies.
        builtUri = Uri.parse(baseURLYoutube).buildUpon()
                .appendQueryParameter("v", id)
                .build();

        //Generate URL.
        try{
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e){
            Log.e("ERROR", "Failed creating URL.");
            e.printStackTrace();
            return null;
        }
        return url;
    }

    /**
     * Create a URL for a poster of a movie.
     * @param posterName Name of the poster to be inserted in the URL.
     * @return URL of the poster of the movie.
     */
    public static URL imageURL(String posterName){
        URL url;
        try{
            url = new URL(PAGE_BASE_URL_POSTER + posterName);
        } catch (MalformedURLException e){
            Log.e("ERROR", "Failed creating URL.");
            e.printStackTrace();
            return null;
        }
        return url;
    }

    /**
     * Get the response of an HTTP URL as a string.
     * @param url URL to which connect.
     * @return Response from the HTTP URL.
     * @throws IOException IOException.
     */
    public static String getResponseFromHttpURL(URL url) throws IOException {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        try{
            InputStream in = conn.getInputStream();
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            if(scanner.hasNext()){
                return scanner.next();
            }else{
                return null;
            }
        }finally{
            conn.disconnect();
        }
    }


}
