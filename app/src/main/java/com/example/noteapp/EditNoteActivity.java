package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.example.noteapp.databinding.ActivityEditNoteBinding;
import com.example.noteapp.model.NoteModel;
import com.example.noteapp.model.OptionNoteTheme;
import com.example.noteapp.room.AppDatabase;
import com.example.noteapp.view.BottomSheetThemeFragment;
import com.example.noteapp.view.fragment.EditTextNoteFragment;
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

public class EditNoteActivity extends AppCompatActivity implements KEY {
    private ActivityEditNoteBinding binding;
    private String nowAction;
    private String noteType;
    private Disposable mDisposable;
    private AppDatabase appDatabase;
    private PowerMenu powerMenu;
    private OptionNoteTheme optionNoteTheme;
    private EditTextNoteFragment editTextNoteFragment;
    private Window window;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_note);
        appDatabase = AppDatabase.getInstance(this);
        nowAction = getIntent().getStringExtra(ACTION);
        noteType = getIntent().getStringExtra(NOTE_TYPE);
        window = getWindow();
        initView();
        //  ↓set event
        binding.iconMenu.setOnClickListener(v -> powerMenu.showAsAnchorCenter(v));
        binding.iconBackEditNote.setOnClickListener(v -> {
            String title = "";
            String content = "";
            if (!binding.etNoteTitle.getText().toString().trim().equalsIgnoreCase("")) {
                title = binding.etNoteTitle.getText().toString();
            }
            if (editTextNoteFragment.getContentHtml() != null) {
                content = editTextNoteFragment.getContentHtml();
            }
            switch (nowAction) {
                case ACTION_ADD:
                    addNoteItem(title, content, optionNoteTheme.getBgValue(), optionNoteTheme.getTtValue());
                    break;
                case ACTION_EDIT:
                    updateNoteItem(title, content, optionNoteTheme.getBgValue(), optionNoteTheme.getTtValue());
                    break;
                case ACTION_VIEW:
                    finish();
                    break;
            }
        });
    }

    //     ↓ edit view
    private void initView() {
        NoteModel noteModel = (NoteModel) getIntent().getExtras().get(NOTE);
        editTextNoteFragment = EditTextNoteFragment.newInstance(nowAction, noteType, noteModel != null ? noteModel.getContent() : "");
        getFragment(editTextNoteFragment);
        if (nowAction.equals(ACTION_EDIT) || nowAction.equals(ACTION_VIEW)) {
            assert noteModel != null;
            binding.etNoteTitle.setText(noteModel.getTitle());
            binding.appBarEditNote.setBackgroundColor(getColor(noteModel.getColorTitle()));
            binding.editNoteLayout.setBackgroundColor(getColor(noteModel.getColorBackground()));
            setOptionNoteTheme(new OptionNoteTheme(noteModel.getColorBackground(), TYPE_COLOR, noteModel.getColorTitle(), TYPE_COLOR));
        } else if (nowAction.equals(ACTION_ADD)) {
            setOptionNoteTheme(new OptionNoteTheme(R.color.bg_note_blue, TYPE_COLOR, R.color.tt_note_blue, TYPE_COLOR));
        }
        if (nowAction.equals(ACTION_VIEW)) {
            binding.etNoteTitle.setEnabled(false);
            binding.iconMenu.setVisibility(View.GONE);
        }
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getColor(optionNoteTheme.getTtValue()));
        configurationPopupMenu();
    }

    public void getFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction().replace(binding.editNoteFragment.getId(), fragment).commit();
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
        noteModel.setTitle(title);
        noteModel.setContent(content);
        noteModel.setColorBackground(bgColor);
        noteModel.setColorTitle(ttColor);
        noteModel.setType(getIntent().getStringExtra(NOTE_TYPE));
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
        listTheme.add(new OptionNoteTheme(R.color.bg_note_malibu_bleach, TYPE_COLOR, R.color.tt_note_malibu_bleach, TYPE_COLOR));
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
        window.setStatusBarColor(getColor(optionNoteTheme.getTtValue()));
    }
}