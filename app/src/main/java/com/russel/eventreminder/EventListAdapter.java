package com.russel.eventreminder;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Russel on 1/9/2018.
 */

public class EventListAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Event> eventsList;

    public EventListAdapter(Context context, int layout, ArrayList<Event> eventsList) {
        this.context = context;
        this.layout = layout;
        this.eventsList = eventsList;
    }

    @Override
    public int getCount() {
        return eventsList.size();
    }

    @Override
    public Object getItem(int position) {
        return eventsList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        ImageView eventImage;
        TextView eventName, eventDate, eventReminder, eventRepeat, eventDescription;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View row = view;
        ViewHolder holder = new ViewHolder();

        if(row == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);

            holder.eventName = row.findViewById(R.id.event_name2);
            holder.eventDate = row.findViewById(R.id.event_date2);
            holder.eventReminder = row.findViewById(R.id.event_reminder2);
            holder.eventRepeat = row.findViewById(R.id.event_repeat2);
            holder.eventDescription = row.findViewById(R.id.event_description2);
            holder.eventImage = row.findViewById(R.id.event_image2);
            row.setTag(holder);
        } else {
            holder = (ViewHolder)row.getTag();
        }

        Event event = eventsList.get(position);

        holder.eventName.setText(event.getEventName());
        holder.eventDate.setText(event.getEventDate());
        holder.eventReminder.setText(event.getEventReminder());
        holder.eventRepeat.setText(event.getEventRepeat());
        holder.eventDescription.setText(event.getEventDescription());

        byte[] eventImages = event.getEventImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(eventImages, 0, eventImages.length);
        holder.eventImage.setImageBitmap(bitmap);

        return row;
    }
}
