package com.example.workmanagersample.ui;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.workmanagersample.R;

import java.util.ArrayList;

public class PicturesAdapter extends RecyclerView.Adapter<PicturesAdapter.IViewHolder> {

    ArrayList<Bitmap> pictures = new ArrayList<>();

    @NonNull
    @Override
    public IViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_listview, null);
        return new IViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IViewHolder holder, int position) {
        holder.bind(pictures.get(position));
    }

    public void addItem(Bitmap bitmap) {
        pictures.add(bitmap);
        notifyDataSetChanged();
    }

    public void setItems(ArrayList<Bitmap> pictures) {
        this.pictures = pictures;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return pictures.size();
    }

    class IViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public IViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageveiw);
        }

        public void bind(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
            imageView.invalidate();
        }
    }
}
