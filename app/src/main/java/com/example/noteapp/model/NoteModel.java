package com.example.noteapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.io.Serializable;
import java.util.Date;

@Entity(tableName = "notes")
public class NoteModel implements Serializable {
    @PrimaryKey
    @NonNull
    private Long id;
    private String title;
    private String content;

    private int colorBackgroud;

    private int colorTitle;
    @TypeConverters(DateConverter.class)
    private Date modifyDay;

    @NonNull
    public Long getId() {
        return id;
    }

    public void setId(@NonNull Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getModifyDay() {
        return modifyDay;
    }

    public void setModifyDay(Date modifyDay) {
        this.modifyDay = modifyDay;
    }

    public int getColorBackgroud() {
        return colorBackgroud;
    }

    public void setColorBackgroud(int colorBackgroud) {
        this.colorBackgroud = colorBackgroud;
    }

    public int getColorTitle() {
        return colorTitle;
    }

    public void setColorTitle(int colorTitle) {
        this.colorTitle = colorTitle;
    }
}
