/**
 * The Search Adapter is the adapter which allows for functionality of the RecyclerView on the SearchActivity
 * It defines a custom layout for view items, then populates those UI items with meaningful information
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.uhi22.FamilyMapClient.Activities.EventActivity;
import com.uhi22.FamilyMapClient.Activities.PersonActivity;
import com.uhi22.FamilyMapClient.R;

import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> {

//    Declare variables that will be used throughout the adapter
    private Context context;
    private List<String> eventIDs, personIDs, titles, descriptions, genders;
    private String authToken, host, port, rootId;
    private boolean lifeStoryEnabled, familyTreeEnabled, spouseLineEnabled, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled;

    /**
     * Constructor to build the MainAdapter and populate its variables with passed data
     *
     * @param context the adapter context from parent activity
     * @param authToken the authToken for the session
     * @param host host connection to server
     * @param port port connection to server
     * @param eventIDs suggested searchIDs
     * @param personIDs suggested personIDs
     * @param titles suggested title texts
     * @param descriptions suggested descriptions
     * @param genders suggested genders
     * @param lifeStoryEnabled boolean for filter
     * @param familyTreeEnabled boolean for filter
     * @param spouseLineEnabled boolean for filter
     * @param fatherEnabled boolean for filter
     * @param motherEnabled boolean for filter
     * @param maleEnabled boolean for filter
     * @param femaleEnabled boolean for filter
     * @param rootId id for root user's person
     */
    public SearchAdapter(Context context, String authToken, String host, String port, List<String> eventIDs, List<String> personIDs, List<String> titles, List<String> descriptions, List<String> genders,
                         boolean lifeStoryEnabled, boolean familyTreeEnabled, boolean spouseLineEnabled, boolean fatherEnabled,
                         boolean motherEnabled, boolean maleEnabled, boolean femaleEnabled, String rootId) {
        this.authToken = authToken;
        this.host = host;
        this.port = port;
        this.context = context;
        this.eventIDs = eventIDs;
        this.personIDs = personIDs;
        this.titles = titles;
        this.descriptions = descriptions;
        this.genders = genders;
        this.lifeStoryEnabled = lifeStoryEnabled;
        this.familyTreeEnabled = familyTreeEnabled;
        this.spouseLineEnabled = spouseLineEnabled;
        this.fatherEnabled = fatherEnabled;
        this.motherEnabled = motherEnabled;
        this.maleEnabled = maleEnabled;
        this.femaleEnabled = femaleEnabled;
        this.rootId = rootId;
    }

    /**
     * This method inflates the custom layout for the recyclerview list item
     * @param parent n/a
     * @param viewType n/a
     * @return the new SearchViewHolder
     */
    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item, parent, false);
        return new SearchViewHolder(view);
    }

    /**
     * Get the view type based on position
     * @param position position for item to be evaluated
     * @return the view type
     */
    @Override
    public int getItemViewType(int position) {
        return position;
    }

    /**
     * This method runs to bind the view holder with information
     * @param holder the holder that was built
     * @param position the position for the evaluated view
     */
    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
//        Populate the item's text with meaningful information
        holder.title.setText(titles.get(position));
        holder.description.setText(descriptions.get(position));
        if(genders.get(position) == null) {
            holder.imageView.setImageResource(R.drawable.location);
        } else if(genders.get(position).equals("m")) {
            holder.imageView.setImageResource(R.drawable.man);
        } else {
            holder.imageView.setImageResource(R.drawable.woman);
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            When the item is clicked, start an intent to a person/event activity
            @Override
            public void onClick(View v) {
                if(genders.get(position) == null) {
                    Intent eventIntent = new Intent(context, EventActivity.class);
                    eventIntent.putExtra("authToken", authToken);
                    eventIntent.putExtra("EventID", eventIDs.get(position));
                    eventIntent.putExtra("PersonID", personIDs.get(position));
                    eventIntent.putExtra("host", host);
                    eventIntent.putExtra("port", port);
                    eventIntent.putExtra("lifeStoryEnabled", lifeStoryEnabled);
                    eventIntent.putExtra("familyTreeEnabled", familyTreeEnabled);
                    eventIntent.putExtra("spouseLineEnabled", spouseLineEnabled);
                    eventIntent.putExtra("fatherEnabled", fatherEnabled);
                    eventIntent.putExtra("motherEnabled", motherEnabled);
                    eventIntent.putExtra("maleEnabled", maleEnabled);
                    eventIntent.putExtra("femaleEnabled", femaleEnabled);
                    eventIntent.putExtra("RootID", rootId);
                    context.startActivity(eventIntent);
                } else {
                    Intent personIntent = new Intent(context, PersonActivity.class);
                    personIntent.putExtra("authToken", authToken);
                    personIntent.putExtra("PersonID", personIDs.get(position));
                    personIntent.putExtra("host", host);
                    personIntent.putExtra("port", port);
                    personIntent.putExtra("lifeStoryEnabled", lifeStoryEnabled);
                    personIntent.putExtra("familyTreeEnabled", familyTreeEnabled);
                    personIntent.putExtra("spouseLineEnabled", spouseLineEnabled);
                    personIntent.putExtra("fatherEnabled", fatherEnabled);
                    personIntent.putExtra("motherEnabled", motherEnabled);
                    personIntent.putExtra("maleEnabled", maleEnabled);
                    personIntent.putExtra("femaleEnabled", femaleEnabled);
                    personIntent.putExtra("RootID", rootId);
                    context.startActivity(personIntent);
                }
            }
        });
    }

    /**
     * returns the number of items to be added to the recycler view
     * @return the number of personIDs
     */
    @Override
    public int getItemCount() {
        return personIDs.size();
    }

    /**
     * a subclass which binds the item layout with the recycler view viewholder
     */
    public class SearchViewHolder extends RecyclerView.ViewHolder {

        private TextView title, description;
        private ImageView imageView;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.list_child1);
            description = itemView.findViewById(R.id.list_child2);
            imageView = itemView.findViewById(R.id.imageView2);
        }
    }
}
