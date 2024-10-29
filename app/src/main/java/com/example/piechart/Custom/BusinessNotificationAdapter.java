package com.example.piechart.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.piechart.Class.BusinessNotification;
import com.example.piechart.R;

import java.util.List;

public class BusinessNotificationAdapter extends ArrayAdapter<BusinessNotification> {

    public BusinessNotificationAdapter(Context context, List<BusinessNotification> notifications) {
        super(context, 0, notifications);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.business_notification_item, parent, false);
        }

        BusinessNotification notification = getItem(position);

        TextView notificationTitle = convertView.findViewById(R.id.notification_title);
        TextView notificationDate = convertView.findViewById(R.id.notification_date);
        TextView notificationContent = convertView.findViewById(R.id.notification_total);

        notificationTitle.setText(notification.getNotificationTitle());
        notificationDate.setText(notification.getNotificationDate());
        notificationContent.setText(notification.getNotificationContent());

        return convertView;
    }
}
