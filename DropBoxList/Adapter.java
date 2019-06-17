package com.isaacson.josie.jisaacsonlab9;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends BaseExpandableListAdapter implements View.OnClickListener {

    Activity activity;
    ArrayList<Manufacturer> manufacturers;

    public Adapter(Activity act, ArrayList<Manufacturer> manufacturers){
        activity = act;
        this.manufacturers = manufacturers;
    }

    //Return the size of your list of manufacturers.
    @Override
    public int getGroupCount() {
        return manufacturers.size();
    }

    //Return the size of the (inner) list of models for the passed group.
    @Override
    public int getChildrenCount(int groupPosition) {
        return manufacturers.get(groupPosition).getTotalModels();
    }

    //Return a group object from your list of manufacturers, using the passed group index.
    @Override
    public Object getGroup(int groupPosition) {
        return manufacturers.get(groupPosition);
    }

    //Return the child object (in this case a String representing the model) from your collection,
    // using the passed group and child position indices.
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return manufacturers.get(groupPosition).getModelName(childPosition);
    }

    //Return a unique identifier for the given group.
    // In this case you can just return the group position as an identifier.
    @Override
    public long getGroupId(int groupPosition) { //// ??????????????????????????????????????????????????????????????????
        return groupPosition;
    }

    //Return a unique identifier for the given child in the given group.
    // In this case you can just return the child position as an identifier.
    @Override
    public long getChildId(int groupPosition, int childPosition) { //// ??????????????????????????????????????????????????????????????????
        return childPosition;
    }

    /*
    The expandable list view calls this to determine whether your IDs
    remain the same when the list data changes. We’re going to lie here and say that they do,
    (by returning true) because it won’t matter in this example that the ID for a child might
    change as a result of deleting some other child. The reason we can get away with this is
    that we won’t ever need a unique ID when we respond to a click on a child, and we won’t
    need it to query the underlying list for IDs at the various indexes when we delete a child
    (which it WILL do if we return false).
     */
    @Override
    public boolean hasStableIds() {
        return true;
    }

    //Here you need to provide a View for a group at the given position.
    // The necessary code is similar to getChildView(). Return the view.
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null){
            //obtain layout inflater from activity
            LayoutInflater inflater = activity.getLayoutInflater();
            //inflate group layout
            convertView = inflater.inflate(R.layout.group_layout, null);
        }

        TextView textView = convertView.findViewById(R.id.group_textview);
        textView.setText(manufacturers.get(groupPosition).getName());
        return convertView ;
    }

    /*
    Here you need to provide a View for a child at the given position.
    If the passed convertView is null, inflate a child layout, which first requires obtaining
    a LayoutInflater from the Activity. Do not set the “root” argument for inflate() to the
    “parent” argument passed into getChildView(). Read the API docs and try something else.
    If it is not null, you’ll be populating it with a child view. Whether or not you had to
    inflate a layout, set the TextView widget on the layout to the correct model string,
    using the passed group and child positions. We’ll worry about handling onClick for the
    delete image later, so do not create a handler for that yet. Return the inflated view.
     */
    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null){
            //obtain layout inflater from activity
            LayoutInflater inflater = activity.getLayoutInflater();
            //inflate child layout
            convertView = inflater.inflate(R.layout.child_layout, null);
        }

        TextView textView = convertView.findViewById(R.id.textView_child);
        textView.setText(manufacturers.get(groupPosition).getModelName(childPosition));
        ImageView imageView = convertView.findViewById(R.id.imageView_deleteButton);

        imageView.setTag(R.id.group_num, groupPosition);
        imageView.setTag(R.id.posn_num, childPosition);

        imageView.setOnClickListener(this);

        return convertView ;

    }

    //The list calls this to ask if a particular child is selectable.
    // The answer for any child is true, as we will be taking an action for such selections.
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void onClick(View v) { //when delete button is clicked
        //snackbar to ask if want to delete
        final View info = v;
        View parentView = activity.findViewById(android.R.id.content);
        Snackbar.make(parentView, "Confirm delete of " +
                        manufacturers.get((int)info.getTag(R.id.group_num)).getModelName((int)info.getTag(R.id.posn_num)),
                Snackbar.LENGTH_LONG)
                .setAction(R.string.confirm_button, new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        int groupNum = (int)info.getTag(R.id.group_num);
                        int childNum = (int)info.getTag(R.id.posn_num);
                        manufacturers.get(groupNum).removeModel(childNum);
                        notifyDataSetChanged();
                    }
                })
                .show();

    }


}
