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

public class VersionRVAdapter extends RecyclerView.Adapter<VersionRVAdapter.ViewHolder>
{
    // Variable for array list and context
    private ArrayList<VersionModal> versionModalArrayList;
    private Context context;

    /**
     * Method used to populate the local variables with passed variables.
     *
     * @param versionModalArrayList passed list of versions.
     * @param context passed context needed for system to function.
     */
    // Constructor
    public VersionRVAdapter(ArrayList<VersionModal> versionModalArrayList, Context context)
    {
        this.versionModalArrayList = versionModalArrayList;
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.version_rv_item, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Method to sets UI elements of a single item in the recycle view with modal values at a specified position.
     * Sets onclick for this single recycle view item (version) to open version on other screen.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
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
            /**
             * Executes after click on single version in list.
             * Opens version on other screen.
             *
             * @param v The view that was clicked.
             */
            @Override
            public void onClick(View v)
            {
                // Calling an intent
                Intent i = new Intent(context, ViewVersionActivity.class);
                // Passing version name through intent
                i.putExtra(StringHelper.VersionData_Intent_ID, modal.getId());
                context.startActivity(i);
            }
        });
    }

    /**
     * @return returns int size of collection of version objects from the DB.
     */
    // Get size of array list
    @Override
    public int getItemCount() { return versionModalArrayList.size(); }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        // Creating variables for our text views
        private TextView versionNameTV, versionCreationDateTV, versionEditDateTV;

        /**
         * Assigns UI elements to local variables to be given UI values in 'onBindViewHolder'.
         *
         * @param itemView
         */
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
