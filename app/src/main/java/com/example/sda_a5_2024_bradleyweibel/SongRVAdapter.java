package com.example.sda_a5_2024_bradleyweibel;

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

    // Constructor
    public SongRVAdapter(ArrayList<SongModal> songModalArrayList, Context context)
    {
        this.songModalArrayList = songModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Inflating layout file for recycler view items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_rv_item, parent, false);
        return new ViewHolder(view);
    }

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

    @Override
    public int getItemCount() { return songModalArrayList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // Creating variables for our text views
        private TextView songNameTV, songCreationDateTV, songEditDateTV;

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
