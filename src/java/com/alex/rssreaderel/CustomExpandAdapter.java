package com.alex.rssreaderel;

/**
 * Created by Alex on 06.12.2016.
 */import java.util.HashMap;
import java.util.List;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class CustomExpandAdapter extends BaseExpandableListAdapter {

    private List<SampleTO> parentRecord;
    private HashMap<String, List<SampleTO>> childRecord;
    private LayoutInflater inflater = null;
    private Activity mContext;

    public CustomExpandAdapter(Activity context, List<SampleTO> parentList, HashMap<String, List<SampleTO>> childList) {
        this.parentRecord = parentList;
        this.childRecord = childList;
        mContext = context;
        inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public SampleTO getChild(int groupPosition, int childPosition) {
        return this.childRecord.get(((SampleTO) getGroup(groupPosition)).getTitle()).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {


        SampleTO childSampleTo = getChild(groupPosition, childPosition);
        ViewHolder holder;
        try {
            if (convertView == null) {
                holder = new ViewHolder();

                convertView = inflater.inflate(R.layout.custom_list_view_child, null);
                holder.childIcon= (ImageView) convertView.findViewById(R.id.childIcon);
                holder.childCounter = (TextView) convertView.findViewById(R.id.childCounter);
                holder.childUpgrade = (TextView) convertView.findViewById(R.id.childUpgrade);
                holder.childTitle = (TextView) convertView.findViewById(R.id.childTitle);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

                holder.childCounter.setText(childSampleTo.getCounter());

            holder.childUpgrade.setText("Обновлено: "+ childSampleTo.getDateUpdate());
            holder.childTitle.setText(childSampleTo.getTitle());

            Picasso.with(mContext) //передаем контекст приложения
                    .load(childSampleTo.getIcon())
                    .into(holder.childIcon); //ссылка на ImageView

        } catch (Exception e) {
        }
        return convertView;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        SampleTO parentSampleTo = parentRecord.get(groupPosition);

        ViewHolder holder;
        int itemType = getGroupType(groupPosition);
        switch (itemType){
            case 0:
                try {
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.custom_list_view, null);
                        holder = new ViewHolder();
                        holder.parentCounter = (TextView) convertView.findViewById(R.id.parentCounter);
                        holder.parentTitle = (TextView) convertView.findViewById(R.id.parentTitle);
                        holder.textUpgrade = (TextView) convertView.findViewById(R.id.textUpgrade);
                        holder.parentIcon = (ImageView) convertView.findViewById(R.id.parentIcon);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }


                    holder.parentCounter.setText(parentSampleTo.getCounter());

                    holder.textUpgrade.setText("Обновлено: " + parentSampleTo.getDateUpdate());


                    holder.parentTitle.setText(parentSampleTo.getTitle());
                    if(parentSampleTo.getIcon()!=null) {
                        Picasso.with(mContext) //передаем контекст приложения
                                .load(parentSampleTo.getIcon())

                                .into(holder.parentIcon); //ссылка на ImageView
                    } else{
                        holder.parentIcon.setImageResource(R.drawable.launcher);
                    }

                } catch (Exception e) {
                }
                break;
            case 1:
                try {
                    if (convertView == null) {
                        convertView = inflater.inflate(R.layout.custom_group_view, null);
                        holder = new ViewHolder();

                        holder.parentTitle = (TextView) convertView.findViewById(R.id.parentTitle);

                        holder.parentIcon = (ImageView) convertView.findViewById(R.id.parentIcon);
                        convertView.setTag(holder);
                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }





                    holder.parentTitle.setText(parentSampleTo.getTitle());

                    holder.parentIcon.setImageResource(R.drawable.group);


                } catch (Exception e) {
                }
                break;
        }


        return convertView;
    }

    public static class ViewHolder {
        private ImageView childIcon;
        private TextView childUpgrade;

        private TextView childCounter;
        private TextView childTitle;
        private TextView textUpgrade;
        private TextView parentTitle;
        private TextView parentCounter;
        private ImageView parentIcon;

    }

    @Override
    public int getGroupTypeCount() {
        return 2;
    }

    @Override
    public int getGroupType (int groupPosition)
    {
        int result = 1;
        if (getChildrenCount(groupPosition)==0)
            result = 0;

        return result;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.childRecord.get(((SampleTO) getGroup(groupPosition)).getTitle()).size();
    }

    @Override
    public SampleTO getGroup(int groupPosition) {
        return this.parentRecord.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.parentRecord.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}

