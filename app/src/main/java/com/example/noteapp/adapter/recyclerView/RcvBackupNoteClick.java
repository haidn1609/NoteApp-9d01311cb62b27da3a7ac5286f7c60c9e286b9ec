package com.example.noteapp.adapter.recyclerView;

public interface RcvBackupNoteClick {
    void viewNote(Long id);

    void openSelectMode(boolean isOpen);

    void setCountSelect(int count);
}
