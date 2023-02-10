package com.example.noteapp.adapter.recyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.noteapp.R;
import com.example.noteapp.model.NoteModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class RcvBackupNoteAdapter extends RecyclerView.Adapter<RcvBackupNoteAdapter.ViewHolder> {
    private List<NoteModel> noteModelList;
    private Context mContext;
    private RcvBackupNoteClick rcvBackupNoteClick;
    private boolean selectMode = false;
    private int countSelect = 0;

    @SuppressLint("NotifyDataSetChanged")
    public void setDataAdapter(List<NoteModel> noteModelList, Context mContext) {
        this.noteModelList = noteModelList;
        this.mContext = mContext;
        notifyDataSetChanged();
    }

    public void setRcvBackupNoteClick(RcvBackupNoteClick rcvBackupNoteClick) {
        this.rcvBackupNoteClick = rcvBackupNoteClick;
    }

    public void setSelectMode(boolean selectMode, int countSelect) {
        this.selectMode = selectMode;
        this.countSelect = countSelect;
    }

    public int getCountSelect() {
        return countSelect;
    }

    @NonNull
    @Override
    public RcvBackupNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_item_note, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RcvBackupNoteAdapter.ViewHolder holder, int position) {
        NoteModel note = noteModelList.get(position);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatDay = new SimpleDateFormat("dd/MM/yyyy");
        holder.noteTile.setBackgroundColor(mContext.getColor(note.getColorTitle()));
        holder.layoutNoteItem.setBackgroundTintList(mContext.getColorStateList(note.getColorBackground()));
        holder.tvTileNote.setText(note.getTitle());
        holder.tvContentNote.setText(Html.fromHtml(note.getContent(), Html.FROM_HTML_MODE_COMPACT));
        holder.tvModifyDateNote.setText(formatDay.format(note.getModifyDay()));
//        ↓set event
        holder.layoutNoteItem.setOnClickListener(v -> {
            if (!selectMode) {
                rcvBackupNoteClick.viewNote(note.getId());
            } else {
                if (note.isSelect()) {
                    countSelect--;
                    rcvBackupNoteClick.setCountSelect(countSelect);
                    holder.lottieAnimationView.setSpeed(-2);
                    note.setSelect(false);
                } else {
                    countSelect++;
                    rcvBackupNoteClick.setCountSelect(countSelect);
                    holder.lottieAnimationView.setSpeed(1);
                    note.setSelect(true);
                }
                holder.lottieAnimationView.playAnimation();
            }
        });
        holder.layoutNoteItem.setOnLongClickListener(v -> {
            rcvBackupNoteClick.openSelectMode(countSelect == 0);
            selectMode = true;
            if (note.isSelect()) {
                holder.lottieAnimationView.setSpeed(-2);
                countSelect--;
                rcvBackupNoteClick.setCountSelect(countSelect);
                note.setSelect(false);
            } else {
                countSelect++;
                rcvBackupNoteClick.setCountSelect(countSelect);
                holder.lottieAnimationView.setSpeed(1);
                note.setSelect(true);
            }
            holder.lottieAnimationView.playAnimation();
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return noteModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTileNote;
        TextView tvContentNote;
        TextView tvModifyDateNote;
        View noteTile;
        CardView layoutNoteItem;
        LottieAnimationView lottieAnimationView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTileNote = itemView.findViewById(R.id.tv_title_note);
            tvContentNote = itemView.findViewById(R.id.tv_note_content);
            tvModifyDateNote = itemView.findViewById(R.id.tv_note_modify);
            noteTile = itemView.findViewById(R.id.note_title);
            layoutNoteItem = itemView.findViewById(R.id.layout_item_note);
            lottieAnimationView = itemView.findViewById(R.id.lottie_checked);
        }
    }
}
