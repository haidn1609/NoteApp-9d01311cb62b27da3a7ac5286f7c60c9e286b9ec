package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.example.noteapp.databinding.ActivityEditNoteBinding;
import com.example.noteapp.model.NoteModel;
import com.example.noteapp.model.OptionNoteTheme;
import com.example.noteapp.room.AppDatabase;
import com.example.noteapp.view.BottomSheetThemeFragment;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
    private PowerMenu powerMenu;
    private OptionNoteTheme optionNoteTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_note);
        appDatabase = AppDatabase.getInstance(this);
        nowAction = getIntent().getStringExtra(ACTION);
        initView();
        //  ↓set event
        binding.iconMenu.setOnClickListener(v -> powerMenu.showAsAnchorCenter(v));
        binding.iconBackEditNote.setOnClickListener(v -> {
            String title = getString(R.string.no_title);
            String content = "";
            if (!binding.etNoteTitle.getText().toString().trim().equalsIgnoreCase("")) {
                title = binding.etNoteTitle.getText().toString();
            }
            if (binding.etNoteContent.getHtml() != null) {
                content = binding.etNoteContent.getHtml();
            }
            if (nowAction.equals(ACTION_ADD)) {
                addNoteItem(title, content, optionNoteTheme.getBgValue(), optionNoteTheme.getTtValue());
            } else if (nowAction.equals(ACTION_EDIT)) {
                updateNoteItem(title, content, optionNoteTheme.getBgValue(), optionNoteTheme.getTtValue());
            } else if (nowAction.equals(ACTION_VIEW)) finish();
        });
    }

    //     ↓ edit view
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
        if (nowAction.equals(ACTION_EDIT) || nowAction.equals(ACTION_VIEW)) {
            NoteModel noteModel = (NoteModel) getIntent().getExtras().get(NOTE);
            binding.etNoteTitle.setText(noteModel.getTitle());
            binding.etNoteContent.setHtml(noteModel.getContent());
            binding.appBarEditNote.setBackgroundColor(getColor(noteModel.getColorTitle()));
            binding.editNoteLayout.setBackgroundColor(getColor(noteModel.getColorBackground()));
            setOptionNoteTheme(new OptionNoteTheme(noteModel.getColorBackground(), TYPE_COLOR, noteModel.getColorTitle(), TYPE_COLOR));
        } else if (nowAction.equals(ACTION_ADD)) {
            setOptionNoteTheme(new OptionNoteTheme(R.color.bg_note_blue, TYPE_COLOR, R.color.tt_note_blue, TYPE_COLOR));
        }
        if (nowAction.equals(ACTION_VIEW)) {
            binding.etNoteTitle.setEnabled(false);
            binding.etNoteContent.setEnabled(false);
            binding.iconMenu.setVisibility(View.GONE);
            binding.richEdittorBar.setVisibility(View.GONE);
        }
        configurationRichEdit();
        configurationPopupMenu();
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

    private void configurationPopupMenu() {
        List<PowerMenuItem> menuItemList = new ArrayList<>();
        menuItemList.add(new PowerMenuItem(getString(R.string.change_theme), false));
        powerMenu = new PowerMenu.Builder(this).addItemList(menuItemList).setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT).setTextColor(getColor(R.color.text_black)).setTextGravity(Gravity.CENTER).setSelectedTextColor(Color.WHITE).setMenuColor(Color.WHITE).setSelectedMenuColor(getColor(R.color.text_black)).setOnMenuItemClickListener((position, item) -> {
            Log.d(TAG, "onItemClick: " + item.title);
            powerMenu.dismiss();
            openBtSheetTheme();
        }).build();
    }

    private void openBtSheetTheme() {
        List<OptionNoteTheme> listTheme = getListTheme();
        BottomSheetThemeFragment btsThemeFragment = BottomSheetThemeFragment.newInstance(listTheme);
        btsThemeFragment.show(getSupportFragmentManager(), btsThemeFragment.getTag());

    }

    //  ↓ room database
    private void addNoteItem(String title, String content, int bgColor, int ttColor) {
        NoteModel noteModel = new NoteModel();
        Calendar calendar = Calendar.getInstance();
        noteModel.setTitle(title);
        noteModel.setContent(content);
        noteModel.setModifyDay(calendar.getTime());
        noteModel.setColorBackground(bgColor);
        noteModel.setColorTitle(ttColor);
        noteModel.setStatus(STATUS_PUBLIC);
        appDatabase.noteDAO().insert(noteModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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
        noteModel.setColorBackground(bgColor);
        noteModel.setColorTitle(ttColor);
        noteModel.setModifyDay(calendar.getTime());

        appDatabase.noteDAO().update(noteModel).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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


    private List<OptionNoteTheme> getListTheme() {
        List<OptionNoteTheme> listTheme = new ArrayList<>();
        listTheme.add(new OptionNoteTheme(R.color.bg_note_grey, TYPE_COLOR, R.color.tt_note_grey, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_orange, TYPE_COLOR, R.color.tt_note_orange, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_surf_turf, TYPE_COLOR, R.color.tt_note_surf_turf, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_red_org, TYPE_COLOR, R.color.tt_note_red_org, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_late, TYPE_COLOR, R.color.tt_note_coffee, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_green, TYPE_COLOR, R.color.tt_note_green, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_blue, TYPE_COLOR, R.color.tt_note_blue, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_blue_green, TYPE_COLOR, R.color.tt_note_blue_green, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_aquamarine, TYPE_COLOR, R.color.tt_note_turquoise, TYPE_COLOR));
        listTheme.add(new OptionNoteTheme(R.color.bg_note_pink, TYPE_COLOR, R.color.tt_note_pink, TYPE_COLOR));
        return listTheme;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

    //↓ communicate fragment
    public void setOptionNoteTheme(OptionNoteTheme optionNoteTheme) {
        this.optionNoteTheme = optionNoteTheme;
    }

    public void setThemeNote(OptionNoteTheme optionNoteTheme) {
        setOptionNoteTheme(optionNoteTheme);
        binding.appBarEditNote.setBackgroundColor(getColor(optionNoteTheme.getTtValue()));
        binding.editNoteLayout.setBackgroundColor(getColor(optionNoteTheme.getBgValue()));
    }
}