package com.example.titaniumturtle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.Circle;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {
    private Context context;
    private ArrayList item_id, item_name, item_note, item_pic;
    int position;
    Activity activity;

    CustomAdapter(Activity activity,
                  Context context,
                  ArrayList item_id,ArrayList item_name, ArrayList item_note,ArrayList item_pic){
        this.activity = activity;
        this.context = context;
        this.item_id = item_id;
        this.item_name = item_name;
        this.item_note = item_note;
        this.item_pic = item_pic;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        this.position = position;

        holder.item_name_txt.setText(String.valueOf(item_name.get(position)));
        holder.item_pic_view.setImageURI(Uri.parse(String.valueOf(item_pic.get(position))));

        holder.collection_item.setOnClickListener(v -> {
                Intent intent = new Intent(context, UpdateItem.class);
                intent.putExtra("id", String.valueOf(item_id.get(position)));
                intent.putExtra("name", String.valueOf(item_name.get(position)));
                intent.putExtra("note", String.valueOf(item_note.get(position)));
                intent.putExtra("pic", String.valueOf(item_pic.get(position)));
                activity.startActivityForResult(intent, 200);
        });
    }

    @Override
    public int getItemCount() {
        return item_id.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView item_name_txt;
        CircleImageView item_pic_view;
        LinearLayout collection_item;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_name_txt = itemView.findViewById(R.id.item_name_txt);
            item_pic_view = itemView.findViewById(R.id.item_pic_view);
            collection_item = itemView.findViewById(R.id.collection_item);

        }
    }
}
