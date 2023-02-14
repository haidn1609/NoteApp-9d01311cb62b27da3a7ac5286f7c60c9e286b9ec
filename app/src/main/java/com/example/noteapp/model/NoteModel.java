package com.example.noteapp.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.noteapp.KEY;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Entity(tableName = "notes")
public class NoteModel implements Serializable, KEY {
    @PrimaryKey
    @NonNull
    private Long id;
    private String title;
    private String content;
    private int colorBackground;
    private int colorTitle;
    private boolean isSelect;
    private String status;
    private String type;

    public NoteModel() {
        Calendar calendar = Calendar.getInstance();
        this.status = STATUS_PUBLIC;
        this.isSelect = false;
        this.modifyDay = calendar.getTime();
    }

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

    public int getColorBackground() {
        return colorBackground;
    }

    public void setColorBackground(int colorBackground) {
        this.colorBackground = colorBackground;
    }

    public int getColorTitle() {
        return colorTitle;
    }

    public void setColorTitle(int colorTitle) {
        this.colorTitle = colorTitle;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        this.isSelect = select;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "NoteModel{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", isSelect=" + isSelect +
                ", status='" + status + '\'' +
                '}';
    }
}
