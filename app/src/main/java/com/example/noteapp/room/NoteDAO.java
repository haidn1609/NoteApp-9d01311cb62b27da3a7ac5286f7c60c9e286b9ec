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

    @Query("update notes set status=:status where id in(:ids)")
    Completable updateStatus(List<Long> ids, String status);

    @Delete
    Completable delete(NoteModel noteModels);

    @Query("delete from notes where id in (:ids)")
    Completable deleteListItem(List<Long> ids);

    @Query("select * from notes")
    Single<List<NoteModel>> getAllNote();

    @Query("select * from notes where status = :status")
    Single<List<NoteModel>> getAllNoteByStatus(String status);

    @Query("select * from notes WHERE id = :id")
    Single<NoteModel> getNoteById(Long id);
}
