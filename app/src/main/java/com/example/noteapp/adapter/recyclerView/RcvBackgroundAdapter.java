package com.example.noteapp.adapter.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.KEY;
import com.example.noteapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RcvBackgroundAdapter extends RecyclerView.Adapter<RcvBackgroundAdapter.ViewHolder> implements KEY {
    private List<String> listBackground;
    private Context mContext;
    private RcvBgItemClick rcvBgItemClick;

    public void setDataAdapter(List<String> listBackground, Context mContext) {
        this.listBackground = listBackground;
        this.mContext = mContext;
        notifyDataSetChanged();
    }

    public void setRcvBgItemClick(RcvBgItemClick rcvBgItemClick) {
        this.rcvBgItemClick = rcvBgItemClick;
    }

    @NonNull
    @Override
    public RcvBackgroundAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_background, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RcvBackgroundAdapter.ViewHolder holder, int position) {
        String url = listBackground.get(position);
        Picasso.get().load(url)
                .error(R.drawable.baseline_error_24)
                .into(holder.imgBgItem);
        holder.imgBgItem.setOnClickListener(v -> rcvBgItemClick.selectBgItem(url));
    }

    @Override
    public int getItemCount() {
        return listBackground.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBgItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBgItem = itemView.findViewById(R.id.img_bg_item);
        }
    }
}
