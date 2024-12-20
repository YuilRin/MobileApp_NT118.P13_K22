package com.example.mobileapp.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import com.example.mobileapp.R;
import java.util.ArrayList;

public class CustomAdapter extends ArrayAdapter<String> {

    public CustomAdapter(Context context, ArrayList<String> items) {
        super(context, 0, items);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        // Kiểm tra xem View có tồn tại không, nếu không, inflating
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        // Lấy item trong danh sách
        String item = getItem(position);

        // Ánh xạ View
        TextView textViewItem = convertView.findViewById(R.id.text_view_item);
        ImageView imageViewIcon = convertView.findViewById(R.id.image_view_icon);

        // Đặt dữ liệu
        textViewItem.setText(item);
        // Nếu cần có thể thêm xử lý để thay đổi biểu tượng ở đây
        if (position % 2 == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorEven)); // Màu cho vị trí chẵn
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorOdd));  // Màu cho vị trí lẻ
        }

        return convertView;
    }
}
