package com.example.noteapp.adapter.recyclerView;

import com.example.noteapp.model.NoteModel;

public interface RcvNoteItemClick {
    void editItemClick(NoteModel noteModel);

    void openSelectMode(boolean isOpen);

    void setCountSelect(int count);
}
