package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;

import com.example.noteapp.adapter.recyclerView.RcvNoteAdapter;
import com.example.noteapp.adapter.recyclerView.RcvNoteItemClick;
import com.example.noteapp.databinding.ActivityMainBinding;
import com.example.noteapp.model.NoteModel;
import com.example.noteapp.room.AppDatabase;
import com.skydoves.powermenu.MenuAnimation;
import com.skydoves.powermenu.PowerMenu;
import com.skydoves.powermenu.PowerMenuItem;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements KEY {
    private RcvNoteAdapter rcvNoteAdapter;
    private ActivityMainBinding binding;
    private Disposable mDisposable;
    private List<NoteModel> noteModelList;
    private AppDatabase appDatabase;
    private SharedPreferences sp;
    private PowerMenu powerMenu;
    private RecyclerView.LayoutManager lmNoteList;
    private RecyclerView.LayoutManager lmNoteGrid;
    private boolean isShowSearch = false;

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        switch (result.getResultCode()) {
            case RESULT_CODE_BACKGROUND_SETTING: {
                initView();
                break;
            }
            case RESULT_CODE_EDIT_NOTE: {
                getListNote();
                break;
            }
            default:
                Log.d(TAG, "onActivityResult: no code result");
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        appDatabase = AppDatabase.getInstance(this);
        lmNoteGrid = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        lmNoteList = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        sp = getBaseContext().getSharedPreferences(SP_BACKGROUND_SETTING, MODE_PRIVATE);
        rcvNoteAdapter = new RcvNoteAdapter();
        noteModelList = new ArrayList<>();
        initView();
        //↓ set event
        binding.iconMenu.setOnClickListener(v -> powerMenu.showAsAnchorCenter(v));
        binding.iconSearch.setOnClickListener(v -> {
            showSearchBar(!isShowSearch);
            binding.etSearchNote.setText("");
            if (isShowSearch) {
                setKeyboard();
            }
            isShowSearch = !isShowSearch;
        });
        binding.etSearchNote.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                rcvNoteAdapter.getFilter().filter(binding.etSearchNote.getText().toString());
                setKeyboard();
                return true;
            }
            return false;
        });
        binding.etSearchNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                rcvNoteAdapter.getFilter().filter(s.toString());
            }
        });
        binding.iconBackMode.setOnClickListener(v -> getListNote());
        binding.iconDelete.setOnClickListener(v -> {
            if (rcvNoteAdapter.getCountSelect() > 0) {
                List<Long> filter = noteModelList.stream()
                        .filter(NoteModel::isSelect)
                        .map(NoteModel::getId)
                        .collect(Collectors.toList());
                deleteMultiNote(filter);
            }
        });
        binding.iconSetting.setOnClickListener(v -> {
            Intent itBackgroundSetting = new Intent(MainActivity.this, BackgroundSettingActivity.class);
            activityResultLauncher.launch(itBackgroundSetting);
        });
        binding.btnAddNote.setOnClickListener(v -> {
            binding.floatingMenuBt.collapse();
            Intent itEditNote = new Intent(MainActivity.this, EditNoteActivity.class);
            itEditNote.putExtra(ACTION, ACTION_ADD);
            activityResultLauncher.launch(itEditNote);
        });
        rcvNoteAdapter.setOnClickItem(new RcvNoteItemClick() {
            @Override
            public void editItemClick(NoteModel noteModel) {
                updateNoteById(noteModel.getId());
            }

            @Override
            public void openSelectMode(boolean isOpen) {
                if (isOpen && isShowSearch) setKeyboard();
                showSelectBar(true);
                showSearchBar(false);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void setCountSelect(int count) {
                binding.txtCountSelect.setText(getString(R.string.hide_txt_select) + " " + count + " " + getString(R.string.hide_txt_note));
            }
        });
        binding.rcvListNote.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    binding.floatingMenuBt.collapse();
                    showBtnMenu(binding, false);
                } else {
                    binding.floatingMenuBt.collapse();
                    showBtnMenu(binding, true);
                }
            }
        });
    }

    //    ↓ edit view
    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        Picasso.get().load(sp.getString(BACKGROUND_COLOR, BLANK_BG)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                binding.mainLayout.setBackground(new BitmapDrawable(getBaseContext().getResources(), bitmap));
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                Log.d(TAG, "onBitmapFailed: ");
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                Log.d(TAG, "onPrepareLoad: ");
            }
        });
        configurationPopupMenu();
        binding.appBar.setBackgroundColor(getColor(Integer.parseInt(sp.getString(APPBAR_COLOR, String.valueOf(R.color.theme_blue)))));
        binding.appBarSelect.setBackgroundColor(getColor(Integer.parseInt(sp.getString(APPBAR_COLOR, String.valueOf(R.color.theme_blue)))));

        if (sp.getString(VIEW_TYPE, VIEW_GRID).equals(VIEW_GRID)) {
            binding.rcvListNote.setLayoutManager(lmNoteGrid);
        } else binding.rcvListNote.setLayoutManager(lmNoteList);
        binding.rcvListNote.setAdapter(rcvNoteAdapter);
        getListNote();
    }

    private void configurationPopupMenu() {
        List<PowerMenuItem> menuItemList = new ArrayList<>();
        menuItemList.add(new PowerMenuItem(getString(R.string.view_grid), false));
        menuItemList.add(new PowerMenuItem(getString(R.string.backup), false));
        powerMenu = new PowerMenu.Builder(this)
                .addItemList(menuItemList)
                .setAnimation(MenuAnimation.SHOWUP_TOP_RIGHT)
                .setTextColor(getColor(R.color.text_black))
                .setTextGravity(Gravity.CENTER)
                .setSelectedTextColor(Color.WHITE)
                .setMenuColor(Color.WHITE)
                .setSelectedMenuColor(getColor(R.color.text_black))
                .setOnMenuItemClickListener((position, item) -> {
                    switch (position) {
                        case 0:
                            assert item.title != null;
                            item.title = getString(item.title.equals(getString(R.string.view_list)) ? R.string.view_grid : R.string.view_list);
                            if (item.title.equals(getString(R.string.view_grid))) {
                                sp.edit().putString(VIEW_TYPE, VIEW_GRID).apply();
                                binding.rcvListNote.setLayoutManager(lmNoteGrid);
                            } else {
                                sp.edit().putString(VIEW_TYPE, VIEW_LIST).apply();
                                binding.rcvListNote.setLayoutManager(lmNoteList);
                            }
                            break;
                        case 1:
                            Intent itBackUpNote = new Intent(MainActivity.this, BackupNoteActivity.class);
                            activityResultLauncher.launch(itBackUpNote);
                            break;
                    }
                    powerMenu.dismiss();
                }).build();
    }

    private void showBtnMenu(ActivityMainBinding binding, boolean show) {
        Transition transitionIn = new Fade();
        transitionIn.setDuration(1000);
        transitionIn.addTarget(R.id.floating_menu_bt);
        TransitionManager.beginDelayedTransition(binding.mainLayout, transitionIn);
        binding.floatingMenuBt.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showSelectBar(boolean isShow) {
        isShowSearch = false;
        if (isShow) {
            binding.appBarSelect.setVisibility(View.VISIBLE);
            binding.appBar.setVisibility(View.INVISIBLE);
        } else {
            binding.appBarSelect.setVisibility(View.INVISIBLE);
            binding.appBar.setVisibility(View.VISIBLE);
            rcvNoteAdapter.setSelectMode(false, 0);
        }
    }

    private void showSearchBar(boolean isShowSearch) {
        binding.appBarTitle.setVisibility(isShowSearch ? View.GONE : View.VISIBLE);
        binding.etSearchNote.setVisibility(isShowSearch ? View.VISIBLE : View.GONE);
        if (isShowSearch) {
            Log.d(TAG, "showSearchBar: open");
            binding.etSearchNote.requestFocus();
            setKeyboard();
            binding.etSearchNote.clearFocus();
        }
    }

    private void setKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getBaseContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    private void openDialogDelete(){

    }
    //    ↓ room database
    private void getListNote() {
        showSelectBar(false);
        showSearchBar(false);
        appDatabase.noteDAO().getAllNoteByStatus(STATUS_PUBLIC).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<NoteModel>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<NoteModel> noteModels) {
                noteModelList = noteModels;
                rcvNoteAdapter.setDataAdapter(noteModelList, MainActivity.this);
                binding.rcvListNote.setAdapter(rcvNoteAdapter);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d("getListNote", "onError: get list note err" + e);
            }
        });
    }

    private void updateNoteById(Long id) {
        appDatabase.noteDAO().getNoteById(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<NoteModel>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull NoteModel noteModel) {
                Intent itEditNote = new Intent(MainActivity.this, EditNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(NOTE, noteModel);
                itEditNote.putExtra(ACTION, ACTION_EDIT);
                itEditNote.putExtras(bundle);
                activityResultLauncher.launch(itEditNote);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d(TAG, "onError: get note err " + e);
            }
        });
    }

    private void deleteMultiNote(List<Long> ids) {
        appDatabase.noteDAO().updateStatus(ids, STATUS_PRIVATE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                mDisposable = d;
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete() {
                showSelectBar(false);
                getListNote();
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d("delete multi note", "onError: delete multi note err" + e);
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
