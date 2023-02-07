package com.example.noteapp.adapter.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;

import java.util.List;


public class RcvThemeAdapter extends RecyclerView.Adapter<RcvThemeAdapter.ViewHolder> {
    private List<Integer> listTheme;
    private Context mContext;
    private RcvThemeItemClick rcvThemeItemClick;
    public void setDataAdapter(List<Integer> listTheme,Context mContext){
        this.listTheme = listTheme;
        this.mContext=mContext;
        notifyDataSetChanged();
    }
    public void setRcvThemeItemClick(RcvThemeItemClick rcvThemeItemClick){
        this.rcvThemeItemClick = rcvThemeItemClick;
    }
    @NonNull
    @Override
    public RcvThemeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_theme, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RcvThemeAdapter.ViewHolder holder, int position) {
        int themeValue = listTheme.get(position);
        holder.linearLayout.setBackgroundTintList(mContext.getResources().getColorStateList(themeValue));
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcvThemeItemClick.selectThemeItem(themeValue);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listTheme.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.layout_item_theme);
        }
    }
}
