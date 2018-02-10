package com.example.ggavi.registeration.ahn3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ggavi.registeration.R;

import java.util.List;


public class open2_CourseAdapter extends RecyclerView.Adapter<open2_CourseAdapter.ViewHolder>  {

    Context context;
    List<open2_CourseItem> items;
    int item_layout;


    public open2_CourseAdapter(Context context, List<open2_CourseItem> items, int item_layout) {
        this.context = context;
        this.items = items;
        this.item_layout = item_layout;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.open2_item_cardview, null);

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final open2_CourseItem item = items.get(position);
        Drawable drawable = ContextCompat.getDrawable(context, item.getImage());
        holder.image.setBackground(drawable);
        holder.text.setText(item.getTitle());


    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        CardView cardview;
        TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.image);
            image.setScaleType(ImageView.ScaleType.FIT_START);
            text = (TextView) itemView.findViewById(R.id.title);
            cardview = (CardView) itemView.findViewById(R.id.cardview);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    int id=items.get(position).getImage();
                    Intent intent = new Intent(v.getContext(), open2_PlaceThirdActivity.class);
                    intent.putExtra("position",position);
                    intent.putExtra("imgId",id);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}