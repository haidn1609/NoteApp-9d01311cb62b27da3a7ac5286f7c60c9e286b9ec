package com.example.noteapp.adapter.recyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.webkit.WebView;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.noteapp.R;
import com.example.noteapp.model.NoteModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

import jp.wasabeef.richeditor.RichEditor;

public class RcvNoteAdapter extends RecyclerView.Adapter<RcvNoteAdapter.ViewHolder> implements Filterable {

    private List<NoteModel> noteList;
    private List<NoteModel> noteListOld;
    private RcvNoteItemClick rcvNoteItemClick;
    private Context mContext;
    private boolean selectMode = false;
    private int countSelect = 0;

    public void setDataAdapter(List<NoteModel> datas, Context mcontext) {
        this.noteList = datas;
        this.noteListOld = datas;
        this.mContext = mcontext;
        notifyDataSetChanged();
    }

    public void setOnClickItem(RcvNoteItemClick rcvNoteItemClick) {
        this.rcvNoteItemClick = rcvNoteItemClick;
    }

    public void setSelectMode(boolean selectMode, int countSelect) {
        this.selectMode = selectMode;
        this.countSelect = countSelect;
    }

    @NonNull
    @Override
    public RcvNoteAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rcv_item_note, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onBindViewHolder(@NonNull RcvNoteAdapter.ViewHolder holder, int position) {
        NoteModel note = noteList.get(position);
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat formatDay = new SimpleDateFormat("dd/MM/yyyy");
        holder.noteTile.setBackgroundColor(mContext.getColor(note.getColorTitle()));
        holder.layoutNoteItem.setBackgroundTintList(mContext.getColorStateList(note.getColorBackground()));
        holder.tvTileNote.setText(note.getTitle().trim().equals("") ? mContext.getString(R.string.no_title) : note.getTitle());
        holder.tvContentNote.setText(Html.fromHtml(note.getContent(), Html.FROM_HTML_MODE_COMPACT));
        holder.tvModifyDateNote.setText(formatDay.format(note.getModifyDay()));

        configurationRichEditor(holder.richViewContent,note.getContent());

        holder.layoutNoteItem.setOnClickListener(v -> {
            if (!selectMode) {
                rcvNoteItemClick.editItemClick(note);
            } else {
                if (note.isSelect()) {
                    countSelect--;
                    rcvNoteItemClick.setCountSelect(countSelect);
                    holder.lottieAnimationView.setSpeed(-2);
                    note.setSelect(false);
                } else {
                    countSelect++;
                    rcvNoteItemClick.setCountSelect(countSelect);
                    holder.lottieAnimationView.setSpeed(1);
                    note.setSelect(true);
                }
                holder.lottieAnimationView.playAnimation();
            }
        });
        holder.layoutNoteItem.setOnLongClickListener(v -> {
            rcvNoteItemClick.openSelectMode(countSelect == 0);
            selectMode = true;
            if (note.isSelect()) {
                holder.lottieAnimationView.setSpeed(-2);
                countSelect--;
                rcvNoteItemClick.setCountSelect(countSelect);
                note.setSelect(false);
            } else {
                countSelect++;
                rcvNoteItemClick.setCountSelect(countSelect);
                holder.lottieAnimationView.setSpeed(1);
                note.setSelect(true);
            }
            holder.lottieAnimationView.playAnimation();
            return true;
        });
    }
    private void configurationRichEditor(RichEditor editor, String html){
        editor.setEditorFontSize(12);
        editor.setBackgroundColor(mContext.getColor(R.color.bg_opacity));
        editor.setEditorBackgroundColor(mContext.getColor(R.color.bg_opacity));
        editor.setPadding(10, 10, 10, 10);
        editor.setHtml(html);
        editor.setEnabled(false);
    }
    @Override
    public int getItemCount() {
        return noteList == null ? 0 : noteList.size();
    }

    public int getCountSelect() {
        return countSelect;
    }

    public boolean isSelectMode() {
        return selectMode;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTileNote;
        TextView tvContentNote;
        TextView tvModifyDateNote;
        RichEditor richViewContent;
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
            richViewContent = itemView.findViewById(R.id.rich_view_content);
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()) {
                    noteList = noteListOld;
                } else {
                    noteList = noteListOld.stream().filter(noteModel -> noteModel.getTitle().toLowerCase().contains(strSearch.toLowerCase())
                                    || noteModel.getContent().toLowerCase().contains(strSearch.toLowerCase()))
                            .collect(Collectors.toList());
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = noteList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                noteList = (List<NoteModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
