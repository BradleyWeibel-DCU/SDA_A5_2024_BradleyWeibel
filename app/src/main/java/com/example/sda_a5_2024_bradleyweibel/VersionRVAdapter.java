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

public class VersionRVAdapter extends RecyclerView.Adapter<VersionRVAdapter.ViewHolder>
{
    // Variable for array list and context
    private ArrayList<VersionModal> versionModalArrayList;
    private Context context;

    // Constructor
    public VersionRVAdapter(ArrayList<VersionModal> versionModalArrayList, Context context)
    {
        this.versionModalArrayList = versionModalArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        // Inflating layout file for recycler view items
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.version_rv_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position)
    {
        // Setting data to views of recycler view item
        VersionModal modal = versionModalArrayList.get(position);
        holder.versionNameTV.setText(modal.getVersionName());
        holder.versionCreationDateTV.setText(modal.getVersionCreationDate());
        holder.versionEditDateTV.setText(modal.getVersionEditDate());

        // Add on click listener for recycler view item
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // Calling an intent
                Intent i = new Intent(context, ViewVersionActivity.class);
                // Passing version name through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, modal.getId());
                i.putExtra(StringHelper.VersionData_Intent_Song_ID, modal.getVersionSongId());
                i.putExtra(StringHelper.VersionData_Intent_Name, modal.getVersionName());
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() { return versionModalArrayList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // Creating variables for our text views
        private TextView versionNameTV, versionCreationDateTV, versionEditDateTV;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);
            // Initializing our text views
            versionNameTV = itemView.findViewById(R.id.idTVVersionName);
            versionCreationDateTV = itemView.findViewById(R.id.idTVVersionCreationDate);
            versionEditDateTV = itemView.findViewById(R.id.idTVVersionEditDate);
        }
    }
}
