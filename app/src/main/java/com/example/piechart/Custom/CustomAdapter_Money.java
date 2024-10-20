package com.example.piechart.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.piechart.R;

import java.util.ArrayList;

public class CustomAdapter_Money extends BaseAdapter {
    private Context context;
    private ArrayList<String> listItems;
    private int[] icons;  // Mảng chứa các icon


    public CustomAdapter_Money(Context context, ArrayList<String> listItems, int[] icons) {
        this.context = context;
        this.listItems = listItems;
        this.icons = icons;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int position) {
        return listItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_money, parent, false);
        }

        // Ánh xạ các view trong layout
        TextView textView = convertView.findViewById(R.id.item_text);
        ImageView imageView = convertView.findViewById(R.id.item_icon);

        // Gán giá trị cho các view
        textView.setText(listItems.get(position));
        imageView.setImageResource(icons[position]);  // Đặt icon tương ứng

        if (position % 2 == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorEven)); // Màu cho vị trí chẵn
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOdd));  // Màu cho vị trí lẻ
        }
        return convertView;
    }

}
