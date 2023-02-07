package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.noteapp.databinding.ActivityEditNoteBinding;
import com.example.noteapp.model.NoteModel;
import com.example.noteapp.room.AppDatabase;

import java.util.Calendar;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import jp.wasabeef.richeditor.RichEditor;

public class EditNoteActivity extends AppCompatActivity implements KEY {
    private ActivityEditNoteBinding binding;
    private String nowAction;
    private Disposable mDisposable;
    private AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_note);
        appDatabase = AppDatabase.getInstance(this);
        nowAction = getIntent().getStringExtra(ACTION);
        initView();
        //set event
        configurationRichEdit();

        binding.iconBackEditNote.setOnClickListener(v -> {
            String title = getString(R.string.no_title);
            String content = "";
            if (binding.etNoteTitle.getText().toString().equals("null")) {
                title = binding.etNoteTitle.getText().toString();
            }
            if (binding.etNoteContent.getHtml() != null) {
                content = binding.etNoteContent.getHtml();
            }
            if (nowAction.equals(ACTION_ADD)) {
                addNoteItem(title, content, R.color.bg_note_blue, R.color.tt_note_blue);
            } else {
                updateNoteItem(title, content, R.color.bg_note_orange, R.color.tt_note_orange);
            }

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
        //
        if (nowAction.equals(ACTION_EDIT)) {
            NoteModel noteModel = (NoteModel) getIntent().getExtras().get(NOTE);
            binding.etNoteTitle.setText(noteModel.getTitle());
            binding.etNoteContent.setHtml(noteModel.getContent());
            binding.appBarEditNote.setBackgroundColor(getColor(noteModel.getColorTitle()));
            binding.editNoteLayout.setBackgroundColor(getColor(noteModel.getColorBackgroud()));
        }
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

    private void addNoteItem(String title, String content, int bgColor, int ttColor) {
        NoteModel noteModel = new NoteModel();
        Calendar calendar = Calendar.getInstance();
        noteModel.setTitle(title);
        noteModel.setContent(content);
        noteModel.setModifyDay(calendar.getTime());
        noteModel.setColorBackgroud(bgColor);
        noteModel.setColorTitle(ttColor);

        appDatabase.noteDAO().insert(noteModel).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        setResult(RESULT_CODE_EDIT_NOTE, new Intent());
                        finish();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e);
                    }
                });
    }

    private void updateNoteItem(String title, String content, int bgColor, int ttColor) {
        NoteModel noteModel = (NoteModel) getIntent().getExtras().get(NOTE);
        Calendar calendar = Calendar.getInstance();
        noteModel.setTitle(title);
        noteModel.setContent(content);
        noteModel.setColorBackgroud(bgColor);
        noteModel.setColorTitle(ttColor);
        noteModel.setModifyDay(calendar.getTime());

        appDatabase.noteDAO().update(noteModel).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        setResult(RESULT_CODE_EDIT_NOTE, new Intent());
                        finish();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.d(TAG, "onError: " + e);
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}