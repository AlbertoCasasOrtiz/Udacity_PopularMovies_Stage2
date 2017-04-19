package com.movies.popular.albert.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * Created by Alberto Casas Ortiz.
 */
class MovieDetailAdapterReviews extends RecyclerView.Adapter<MovieDetailAdapterReviews.ViewHolder>{

    //Number of items in the adapter.
    private int numItems;

    //Arrays with information of reviews.
    private ArrayList<String> userNames;
    private ArrayList<String> reviews;


    /**
     * Constructor of the adapter.
     * @param numItems Number of items in the adapter.
     * @param userNames User names of reviews.
     * @param reviews Text of reviews.
     */
    MovieDetailAdapterReviews(int numItems, ArrayList<String> userNames, ArrayList<String> reviews){
        this.numItems = numItems;
        this.userNames = userNames;
        this.reviews = reviews;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();

        int id = R.layout.grid_reviews;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(this.userNames.get(position), this.reviews.get(position));
    }

    @Override
    public int getItemCount() {
        return this.numItems;
    }


    /**
     * ViewHolder class.
     */
    class ViewHolder extends RecyclerView.ViewHolder{
        //TextViews with username and review.
        private TextView tvUserName;
        private TextView tvReview;

        /**
         * Constructor of ViewHolder.
         * @param itemView Item.
         */
        ViewHolder(View itemView){
            super(itemView);
            this.tvUserName = (TextView) itemView.findViewById(R.id.review_author);
            this.tvReview = (TextView) itemView.findViewById(R.id.review_content);
        }

        /**
         * Bind data to GUI.
         * @param userName User name.
         * @param review Review text.
         */
        void bind(String userName, String review){
            this.tvUserName.setText(userName);
            this.tvReview.setText(review);
        }
    }

}
