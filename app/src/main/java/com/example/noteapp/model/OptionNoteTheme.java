package com.example.noteapp.model;

import com.example.noteapp.KEY;

import java.io.Serializable;
import java.security.Key;

public class OptionNoteTheme implements Serializable {

    private int bgValue;
    private String bgType;
    private int ttValue;
    private String ttType;

    public OptionNoteTheme(int bgValue, String bgType, int ttValue, String ttType) {
        this.bgValue = bgValue;
        this.bgType = bgType;
        this.ttValue = ttValue;
        this.ttType = ttType;
    }

    public int getBgValue() {
        return bgValue;
    }

    public void setBgValue(int bgValue) {
        this.bgValue = bgValue;
    }

    public String getBgType() {
        return bgType;
    }

    public void setBgType(String bgType) {
        this.bgType = bgType;
    }

    public int getTtValue() {
        return ttValue;
    }

    public void setTtValue(int ttValue) {
        this.ttValue = ttValue;
    }

    public String getTtType() {
        return ttType;
    }

    public void setTtType(String ttType) {
        this.ttType = ttType;
    }
}
