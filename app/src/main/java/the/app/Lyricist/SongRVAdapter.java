package the.app.Lyricist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class SongRVAdapter extends RecyclerView.Adapter<SongRVAdapter.ViewHolder>
{
    // Variable for array list and context
    private ArrayList<SongModal> songModalArrayList;
    private Context context;

    /**
     * Method used to create song adapter by assigning passed values to internal variables.
     *
     * @param songModalArrayList passed list of songs.
     * @param context passed context.
     */
    // Constructor
    public SongRVAdapter(ArrayList<SongModal> songModalArrayList, Context context)
    {
        this.songModalArrayList = songModalArrayList;
        this.context = context;
    }

    /**
     * Method to return the populated ViewHolder.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return populated ViewHolder.
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Inflating layout file for recycler view items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_rv_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Method to sets UI elements of a single item in the recycle view with modal values at a specified position.
     * Sets onclick for this single recycle view item (song) to open song on other screen.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // Setting data to views of recycler view item
        SongModal modal = songModalArrayList.get(position);
        holder.songNameTV.setText(modal.getSongName());
        holder.songCreationDateTV.setText(modal.getSongCreationDate());
        holder.songEditDateTV.setText(modal.getSongEditDate());

        // Add on click listener for recycler view item
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            /**
             * Onclick for single recycle view item to open this song on other screen.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                // Calling an intent
                Intent i = new Intent(context, ViewSongAndVersionsActivity.class);
                // Passing song name through intent
                i.putExtra(StringHelper.SongData_Intent_Name, modal.getSongName());
                context.startActivity(i);
            }
        });
    }

    /**
     * @return returns int size of collection of song objects from the DB.
     */
    @Override
    public int getItemCount() { return songModalArrayList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // Creating variables for our text views
        private TextView songNameTV, songCreationDateTV, songEditDateTV;

        /**
         * Assigns UI elements to local variables to be given UI values in 'onBindViewHolder'.
         *
         * @param itemView
         */
        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            // Initializing our text views
            songNameTV = itemView.findViewById(R.id.idTVSongName);
            songCreationDateTV = itemView.findViewById(R.id.idTVSongCreationDate);
            songEditDateTV = itemView.findViewById(R.id.idTVSongEditDate);
        }
    }
}
