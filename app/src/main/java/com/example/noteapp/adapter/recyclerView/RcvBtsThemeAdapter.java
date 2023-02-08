package com.example.noteapp.adapter.recyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.KEY;
import com.example.noteapp.R;
import com.example.noteapp.model.OptionNoteTheme;

import java.util.List;

public class RcvBtsThemeAdapter extends RecyclerView.Adapter<RcvBtsThemeAdapter.ViewHolder> implements KEY {
    private List<OptionNoteTheme> themeList;
    private Context mContext;
    private RcvBtsItemClick rcvBtsItemClick;
    public void setDataAdapter(List<OptionNoteTheme> themeList, Context mContext){
        this.themeList = themeList;
        this.mContext = mContext;
        notifyDataSetChanged();
    }

    public void setRcvBtsItemClick(RcvBtsItemClick rcvBtsItemClick) {
        this.rcvBtsItemClick = rcvBtsItemClick;
    }

    @NonNull
    @Override
    public RcvBtsThemeAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_bts_theme,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RcvBtsThemeAdapter.ViewHolder holder, int position) {
        OptionNoteTheme optionNoteTheme = themeList.get(position);
        if(optionNoteTheme.getBgType().equals(TYPE_COLOR)){
            holder.layoutBtsItem.setBackgroundTintList(mContext.getColorStateList(optionNoteTheme.getBgValue()));
        }
        if(optionNoteTheme.getTtType().equals(TYPE_COLOR)){
            holder.ttBtsItem.setBackgroundColor(mContext.getColor(optionNoteTheme.getTtValue()));
        }
        holder.layoutBtsItem.setOnClickListener(v -> rcvBtsItemClick.setTheme(optionNoteTheme));
    }

    @Override
    public int getItemCount() {
        return themeList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView layoutBtsItem;
        LinearLayout ttBtsItem;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutBtsItem = itemView.findViewById(R.id.layout_item_note_theme);
            ttBtsItem = itemView.findViewById(R.id.tt_item_note_theme);
        }
    }
}
