package com.example.noteapp;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.noteapp.databinding.ActivityEditNoteBinding;

import jp.wasabeef.richeditor.RichEditor;

public class EditNoteActivity extends AppCompatActivity implements KEY {
    private ActivityEditNoteBinding binding;
    private String nowAction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_note);
        initView();
        Intent intent = getIntent();
        nowAction = intent.getStringExtra(ACTION);
        //set event
        configurationRichEdit();

        binding.iconBackEditNote.setOnClickListener(v -> {
            Intent itNote = new Intent();
            if (binding.etNoteTitle.getText().toString().equals("null")) {
                itNote.putExtra(NOTE_TITLE, binding.etNoteTitle.getText().toString());
            } else itNote.putExtra(NOTE_TITLE, getString(R.string.no_title));
            if (binding.etNoteContent.getHtml() != null) {
                itNote.putExtra(NOTE_CONTENT, binding.etNoteContent.getHtml());
            } else itNote.putExtra(NOTE_CONTENT, "");
            itNote.putExtra(NOTE_THEME_BG, R.color.bg_note_blue);
            itNote.putExtra(NOTE_THEME_TT, R.color.tt_note_blue);
            if (nowAction.equals(ACTION_ADD)){
                setResult(RESULT_CODE_ADD_NOTE, itNote);
            }
            finish();
        });
    }

    private void initView() {
        binding.etNoteContent.setEditorFontSize(18);
        binding.etNoteContent.setBackgroundColor(getColor(R.color.bg_opacity));
        binding.etNoteContent.setEditorBackgroundColor(getColor(R.color.bg_opacity));
        binding.etNoteContent.setPadding(10, 10, 10, 10);
        binding.etNoteContent.setPlaceholder(getString(R.string.hide_et_content));
        //hide function don't use
        binding.actionInsertImage.setVisibility(View.GONE);
        binding.actionInsertYoutube.setVisibility(View.GONE);
        binding.actionInsertAudio.setVisibility(View.GONE);
        binding.actionInsertVideo.setVisibility(View.GONE);
        binding.actionInsertLink.setVisibility(View.GONE);
    }

    private void configurationRichEdit() {
        RichEditor editor = binding.etNoteContent;
        binding.actionUndo.setOnClickListener(v -> editor.undo());
        binding.actionRedo.setOnClickListener(v -> editor.redo());
        binding.actionBold.setOnClickListener(v -> editor.setBold());
        binding.actionItalic.setOnClickListener(v -> editor.setItalic());
        binding.actionSubscript.setOnClickListener(v -> editor.setSubscript());
        binding.actionSuperscript.setOnClickListener(v -> editor.setSuperscript());
        binding.actionStrikethroug.setOnClickListener(v -> editor.setStrikeThrough());
        binding.actionUnderline.setOnClickListener(v -> editor.setUnderline());
        binding.actionHeading1.setOnClickListener(v -> editor.setHeading(1));
        binding.actionHeading2.setOnClickListener(v -> editor.setHeading(2));
        binding.actionHeading3.setOnClickListener(v -> editor.setHeading(3));
        binding.actionHeading4.setOnClickListener(v -> editor.setHeading(4));
        binding.actionHeading5.setOnClickListener(v -> editor.setHeading(5));
        binding.actionHeading6.setOnClickListener(v -> editor.setHeading(6));
        binding.actionIndent.setOnClickListener(v -> editor.setIndent());
        binding.actionOutdent.setOnClickListener(v -> editor.setOutdent());
        binding.actionAlignLeft.setOnClickListener(v -> editor.setAlignLeft());
        binding.actionAlignCenter.setOnClickListener(v -> editor.setAlignCenter());
        binding.actionAlignRight.setOnClickListener(v -> editor.setAlignRight());
        binding.actionBlockquote.setOnClickListener(v -> editor.setBlockquote());
        binding.actionInsertBullets.setOnClickListener(v -> editor.setBullets());
        binding.actionInsertNumber.setOnClickListener(v -> editor.setNumbers());
        binding.actionInsertCheckbock.setOnClickListener(v -> editor.insertTodo());
    }
}