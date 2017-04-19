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
class MovieDetailAdapterTrailers extends RecyclerView.Adapter<MovieDetailAdapterTrailers.ViewHolder>{

    //Number of items in the adapter.
    private int numItems;

    //Arrays with information of trailers.
    private ArrayList<String> trailersName;

    //OnClickListener
    final private ItemListenerClick clickListener;

    /**
     * Constructor of the adapter.
     * @param numItems Number of items in the adapter.
     * @param trailersName Trailer names.
     * @param listener OnClickListener
     */
    MovieDetailAdapterTrailers(int numItems, ArrayList<String> trailersName, ItemListenerClick listener){
        this.numItems = numItems;
        this.trailersName = trailersName;
        this.clickListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int id = R.layout.grid_trailers;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(id, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(trailersName.get(position));
    }

    @Override
    public int getItemCount() {
        return this.numItems;
    }


    /**
     * ViewHolder class.
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        //TextView with the name.
        private TextView name;

        /**
         * Constructor of ViewHolder.
         * @param itemView Item.
         */
        ViewHolder(View itemView){
            super(itemView);
            name =  (TextView) itemView.findViewById(R.id.trailer_name);
            itemView.setOnClickListener(this);
        }

        /**
         * Bind data to the image.
         * @param nameTrailer Name of the path of the poster.
         */
        void bind(String nameTrailer){
            name.setText(nameTrailer);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            clickListener.onListItemClick(clickedPosition);
        }
    }

    /**
     * Interface to handle clicks.
     */
    interface ItemListenerClick{
        void onListItemClick(int clickedItemIndex);
    }
}
