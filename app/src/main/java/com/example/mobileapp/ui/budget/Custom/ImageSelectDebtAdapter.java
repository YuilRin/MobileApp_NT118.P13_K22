package com.example.mobileapp.ui.budget.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;

import java.util.List;


public class ImageSelectDebtAdapter extends RecyclerView.Adapter<ImageSelectDebtAdapter.ImageViewHolder> {

    public List<Integer> imageList;
    public Context context;
    private OnImageClickListener listener;

    public interface OnImageClickListener {
        void onImageClick(int imageResid);
    }

    public void setOnImageClickListener (OnImageClickListener listener ) {
        this.listener = listener;
    }

    public ImageSelectDebtAdapter(Context context, List<Integer> imageList){
        this.context = context;
        this.imageList = imageList;


    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_image_debt, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageSelectDebtAdapter.ImageViewHolder holder, int position) {
        holder.imgV.setImageResource(imageList.get(position));

        holder.imgV.setOnClickListener(view -> {
            if (listener != null) {
                listener.onImageClick(imageList.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
         return imageList.size();
    }


    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imgV;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imgV = itemView.findViewById(R.id.img_debt_select);


        }
    }
}
