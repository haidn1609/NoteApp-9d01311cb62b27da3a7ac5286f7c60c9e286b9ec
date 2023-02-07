package com.example.noteapp.adapter.recyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.model.NoteModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class RcvNoteAdapter extends RecyclerView.Adapter<RcvNoteAdapter.ViewHolder> {

    private List<NoteModel> noteList;
    private RcvNoteItemClick rcvNoteItemClick;
    private Context mContext;

    public void setDataAdapter(List<NoteModel> datas, Context mcontext) {
        this.noteList = datas;
        this.mContext = mcontext;
        notifyDataSetChanged();
    }

    public void setOnClickItem(RcvNoteItemClick rcvNoteItemClick) {
        this.rcvNoteItemClick = rcvNoteItemClick;
    }

    @NonNull
    @Override
    public RcvNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rcv_item_note, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RcvNoteAdapter.ViewHolder holder, int position) {
        NoteModel note = noteList.get(position);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatDay = new SimpleDateFormat("dd/MM/yyyy");
        holder.noteTile.setBackgroundColor(mContext.getResources().getColor(note.getColorTitle()));
        holder.layoutNoteItem.setBackgroundTintList(mContext.getResources().getColorStateList(note.getColorBackgroud()));
        holder.tvTileNote.setText(note.getId() + "." + note.getTitle());
        holder.tvContentNote.setText(note.getContent());
        holder.tvModifyDateNote.setText(formatDay.format(note.getModifyDay()));

        holder.layoutNoteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rcvNoteItemClick.editItemClick(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList == null ? 0 : noteList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTileNote;
        TextView tvContentNote;
        TextView tvModifyDateNote;
        View noteTile;
        CardView layoutNoteItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTileNote = itemView.findViewById(R.id.tv_title_note);
            tvContentNote = itemView.findViewById(R.id.tv_note_content);
            tvModifyDateNote = itemView.findViewById(R.id.tv_note_modify);
            noteTile = itemView.findViewById(R.id.note_title);
            layoutNoteItem = itemView.findViewById(R.id.layout_item_note);
        }
    }
}
