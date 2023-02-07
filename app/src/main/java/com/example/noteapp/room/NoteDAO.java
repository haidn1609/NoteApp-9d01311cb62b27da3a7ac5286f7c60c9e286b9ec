package com.example.noteapp.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.noteapp.model.NoteModel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface NoteDAO {
    @Insert
    Completable insert(NoteModel... noteModels);

    @Update
    Completable update(NoteModel... noteModels);

    @Delete
    Completable delete(NoteModel noteModels);

    @Query("SELECT * FROM notes")
    Single<List<NoteModel>> getAllNote();

    @Query("SELECT * FROM notes WHERE id = :id")
    Single<NoteModel> getNoteById(Long id);
}
