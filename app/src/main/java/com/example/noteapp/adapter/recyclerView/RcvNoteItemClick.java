package com.example.noteapp.adapter.recyclerView;

import com.example.noteapp.model.NoteModel;

public interface RcvNoteItemClick {
    void editItemClick(NoteModel noteModel);

    void setSelectMode(boolean selectMode);

    void setCountSelect(int count);
}
