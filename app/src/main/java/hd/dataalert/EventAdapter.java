package hd.dataalert;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
/**
 * Created by Hayde on 03-Apr-17.
 */

public class EventAdapter extends ArrayAdapter<Event> {
    private Activity activiy;
    private ArrayList<Event> eventList;
    private static LayoutInflater inflater = null;

    public EventAdapter(Activity activity, int textViewResourceId, ArrayList<Event> list) {
        super(activity, textViewResourceId, list);

        this.activiy = activity;
        this.eventList = list;

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    public int getCount() {
        return eventList.size();
    }

    public static class ViewHolder {
        public TextView display_status;
        public TextView display_date;
        public TextView display_description;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;
        try {
            if (convertView == null) {
                vi = inflater.inflate(R.layout.event_list_item, null);
                holder = new ViewHolder();

                holder.display_status = (TextView) vi.findViewById(R.id.statusTxt);
                holder.display_date = (TextView) vi.findViewById(R.id.dateTxt);
                holder.display_description = (TextView) vi.findViewById(R.id.descriptionTxt);


                vi.setTag(holder);
            } else {
                holder = (ViewHolder) vi.getTag();
            }



            holder.display_status.setText(eventList.get(position).getStatus());
            holder.display_date.setText(eventList.get(position).getDate());
            holder.display_description.setText(eventList.get(position).getDescription());


        } catch (Exception e) {


        }
        return vi;
    }
}
