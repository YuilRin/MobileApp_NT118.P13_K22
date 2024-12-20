package com.example.mobileapp.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.mobileapp.R;

public class CustomAdapter_Grid extends BaseAdapter {

    private Context context;
    private String[] data;

    public CustomAdapter_Grid(Context context, String[] data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public int getCount() {
        return data.length;
    }

    @Override
    public Object getItem(int position) {
        return data[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.grid_item, parent, false);
        }

        // Tìm TextView trong layout grid_item và gán dữ liệu
        TextView textView = convertView.findViewById(R.id.textView);
        textView.setText(data[position]);

        return convertView;
    }
}
