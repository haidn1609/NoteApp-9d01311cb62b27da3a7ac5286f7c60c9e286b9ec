package com.example.noteapp.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.noteapp.model.NoteModel;

@Database(entities = {NoteModel.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DB_NAME ="NOTE.DB";
    private static  AppDatabase instance;

    public static synchronized AppDatabase getInstance(Context context){
        if(instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,DB_NAME)
                    .build();
        }
        return instance;
    }
    public abstract NoteDAO noteDAO();
}
