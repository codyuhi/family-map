/**
 * The Main Adapter is the adapter which allows for functionality of the ExpandableListView on the PersonActivity
 * It defines a custom layout for both group items and child items, then populates those UI items with meaningful information
 * @author Cody Uhi
 */

package com.uhi22.FamilyMapClient.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uhi22.FamilyMapClient.Activities.EventActivity;
import com.uhi22.FamilyMapClient.Activities.PersonActivity;
import com.uhi22.FamilyMapClient.R;

import java.util.HashMap;
import java.util.List;

import Models.ListItem;

public class MainAdapter extends BaseExpandableListAdapter {

//    Declare variables that will be used throughout the adapter
    private Context context;
    private List<String> listGroup;
    private HashMap<String, List<ListItem>> listItem;
    private boolean lifeStoryEnabled, familyTreeEnabled, spouseLineEnabled, fatherEnabled, motherEnabled, maleEnabled, femaleEnabled;
    private String rootId;

    /**
     * Constructor to build the MainAdapter and populate its variables with passed data
     *
     * @param context the adapter context from parent activity
     * @param listGroup the list group ("Family" or "Events")
     * @param listItem a hashmap mapping items to their correct group
     * @param lifeStoryEnabled boolean for filter
     * @param familyTreeEnabled boolean for filter
     * @param spouseLineEnabled boolean for filter
     * @param fatherEnabled boolean for filter
     * @param motherEnabled boolean for filter
     * @param maleEnabled boolean for filter
     * @param femaleEnabled boolean for filter
     * @param rootId id for root user's person
     */
    public MainAdapter(Context context, List<String> listGroup, HashMap<String, List<ListItem>>
            listItem, boolean lifeStoryEnabled, boolean familyTreeEnabled, boolean spouseLineEnabled,
                       boolean fatherEnabled, boolean motherEnabled, boolean maleEnabled, boolean femaleEnabled, String rootId) {
        this.context = context;
        this.listGroup = listGroup;
        this.listItem = listItem;
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
     * Returns the number of groups (should just be 2)
     * @return number of groups
     */
    @Override
    public int getGroupCount() {
        return listGroup.size();
    }

    /**
     * Returns the number of children in a group
     * @param groupPosition Denotes whether the evaluated group is an event or person
     * @return the number of children for given group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return this.listItem.get(this.listGroup.get(groupPosition)).size();
    }

    /**
     * Returns the group for group at given position
     * @param groupPosition denotes which group is going to be obtained
     * @return the group
     */
    @Override
    public Object getGroup(int groupPosition) {
        return this.listGroup.get(groupPosition);
    }

    /**
     * Returns the child for child at given group and child positions
     * @param groupPosition denotes which group is going to be obtained
     * @param childPosition denotes which child is going to be obtained
     * @return the child
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.listItem.get(this.listGroup.get(groupPosition))
                .get(childPosition);
    }

    /**
     * Returns the groupId for the given group position (generated on the fly on group creation)
     * @param groupPosition denotes which group's id should be obtained
     * @return the group id
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Returns the childId for the given child and group position
     * @param groupPosition denotes which group the child is in
     * @param childPosition denotes which child's id should be obtained
     * @return the child id
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * I did not change this from the default value
     * @return n/a
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * This method gets the groupview for the given group position
     * In this case, the method builds the group layout for it to be populated to the page
     * @param groupPosition the desired group position to be "gotten"
     * @param isExpanded determines whether the group is expanded
     * @param convertView the convertview
     * @param parent n/a
     * @return
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
//        Get the group and if the view hasn't been populated, populate it
        String group = (String) getGroup(groupPosition);
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_group, null);
        }

//        Fill in the values in the layout based on the group info
        TextView textView = convertView.findViewById(R.id.list_parent);
        textView.setText(group);
        return convertView;
    }

    /**
     *
     * @param groupPosition This is which position the group is in (event or person)
     * @param childPosition This is which position the group's child item is (which person or which event)
     * @param isLastChild Denotes whether this is the last child
     * @param convertView Allows for a layout for the child item
     * @param parent n/a
     * @return returns the childview
     */
    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
//        get the ListItem for the ChildView that is being evaluated
        ListItem child = (ListItem) getChild(groupPosition, childPosition);
//        If there is no convertview, build one
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert layoutInflater != null;
            convertView = layoutInflater.inflate(R.layout.list_item, null);
        }

//        Populate the info on the convert view (person/event info, gender, etc.)
        TextView textView1 = convertView.findViewById(R.id.list_child1);
        TextView textView2 = convertView.findViewById(R.id.list_child2);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView2);
//        Populate the layout based on whether it was an event or person
        if(child.getItemType().equals("event")) {
            textView1.setText((child.getEventType() + ": " + child.getLocation() + "(" + child.getYear() + ")"));
            textView2.setText(child.getName());
            imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.location));
        } else {
            textView1.setText(child.getName());
            textView2.setText(child.getRelationship());
            if(child.getGender().equals("m")) {
                imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.man));
            } else {
                imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.woman));
            }
        }

//        When the convert view is clicked,
//        if the clicked item is an event, start an event activity and pass the event data
//        If the clicked item is a person, start a person activity and pass the person data
        convertView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(child.getItemType().equals("event")) {
                    Intent eventIntent = new Intent(context, EventActivity.class);
                    eventIntent.putExtra("authToken", child.getAuthToken());
                    eventIntent.putExtra("EventID", child.getEventId());
                    eventIntent.putExtra("PersonID", child.getPersonId());
                    eventIntent.putExtra("host", child.getHost());
                    eventIntent.putExtra("port", child.getPort());
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
                    personIntent.putExtra("authToken", child.getAuthToken());
                    personIntent.putExtra("PersonID", child.getPersonId());
                    personIntent.putExtra("host", child.getHost());
                    personIntent.putExtra("port", child.getPort());
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
        return convertView;
    }

    /**
     * This method allows the menu item to be selectable
     * @param groupPosition determines whether the user is selecting an event or a person
     * @param childPosition determines which person/event the user is selecting
     * @return true denotes that menu items should be selectable
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
