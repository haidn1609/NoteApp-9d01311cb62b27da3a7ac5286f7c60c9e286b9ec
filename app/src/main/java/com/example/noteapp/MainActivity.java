package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.noteapp.adapter.recyclerView.RcvNoteAdapter;
import com.example.noteapp.adapter.recyclerView.RcvNoteItemClick;
import com.example.noteapp.databinding.ActivityMainBinding;
import com.example.noteapp.model.NoteModel;
import com.example.noteapp.room.AppDatabase;

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
    private boolean isSearch = false;
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
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
        sp = getBaseContext().getSharedPreferences(SP_BACKGROUND_SETTING, MODE_PRIVATE);

        rcvNoteAdapter = new RcvNoteAdapter();
        noteModelList = new ArrayList<>();
        initView();
        //set data
        getListNote();
        //add event
        binding.iconSearch.setOnClickListener(v -> {
            setViewSearch();
            //changeKeyboardState();
        });
        binding.etSearchNote.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_SEARCH:
                        Log.d(TAG, "onEditorAction: "+v.getText().toString());
                        v.setText("");
                        setViewSearch();
                }
                return false;
            }
        });
        binding.iconBackMode.setOnClickListener(v -> {
            binding.appBarSelect.setVisibility(View.INVISIBLE);
            rcvNoteAdapter.setSelectMode(false, 0);
            getListNote();
        });
        binding.iconDelete.setOnClickListener(v -> {
            List<Long> filter = noteModelList.stream().filter(noteModel -> noteModel.isSelect())
                    .map(noteModel -> noteModel.getId())
                    .collect(Collectors.toList());
            deleteMultiNote(filter);
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
            public void setSelectMode(boolean selectMode) {
                if (selectMode) {
                    binding.appBarSelect.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void setCountSelect(int count) {
                binding.txtCountSelect.setText("SELECT " + count + " NOTE");
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

    private void setViewSearch() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (isSearch) {
            binding.appBarTitle.setVisibility(View.VISIBLE);
            binding.etSearchNote.setVisibility(View.GONE);
            binding.etSearchNote.clearFocus();
            inputMethodManager.hideSoftInputFromWindow(binding.etSearchNote.getWindowToken(), 0);
            isSearch = false;
        } else {
            binding.appBarTitle.setVisibility(View.GONE);
            binding.etSearchNote.setVisibility(View.VISIBLE);
            binding.etSearchNote.requestFocus();
            inputMethodManager.showSoftInput(binding.etSearchNote, InputMethodManager.SHOW_FORCED);
            isSearch = true;
        }

    }

    private void changeKeyboardState() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            if (isSearch) {
                inputMethodManager.showSoftInput(binding.etSearchNote, InputMethodManager.SHOW_FORCED);
            } else {
                inputMethodManager.hideSoftInputFromWindow(binding.etSearchNote.getWindowToken(), 0);
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void initView() {
        if (sp.getString(BACKGROUND_COLOR, "-1").equals("-1")) {
            binding.mainLayout.setBackgroundColor(getColor(R.color.bg_white));
        } else {
            binding.mainLayout.setBackground(getDrawable(Integer.parseInt(sp.getString(BACKGROUND_COLOR, "NONE"))));
        }
        if (sp.getString(APPBAR_COLOR, "-1").equals("-1")) {
            binding.appBar.setBackgroundColor(getColor(R.color.theme_blue));
        } else {
            binding.appBar.setBackgroundColor(getColor(Integer.parseInt(sp.getString(APPBAR_COLOR, "500021"))));
        }
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        binding.rcvListNote.setLayoutManager(layoutManager);
        binding.rcvListNote.setAdapter(rcvNoteAdapter);
    }

    private void getListNote() {
        appDatabase.noteDAO().getAllNote().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<NoteModel>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<NoteModel> noteModels) {
                        noteModelList = noteModels;
                        if (noteModelList.size() > 0) {
                            binding.textNotNote.setVisibility(View.GONE);
                            binding.rcvListNote.setVisibility(View.VISIBLE);
                            rcvNoteAdapter.setDataAdapter(noteModelList, MainActivity.this);
                            binding.rcvListNote.setAdapter(rcvNoteAdapter);
                        } else {
                            binding.textNotNote.setVisibility(View.VISIBLE);
                            binding.rcvListNote.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d("getListNote", "onError: get list note err" + e);
                    }
                });
    }

    private void updateNoteById(Long id) {
        appDatabase.noteDAO().getNoteById(id).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<NoteModel>() {
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

    private static void showBtnMenu(ActivityMainBinding binding, boolean show) {
        Transition transitionIn = new Fade();
        transitionIn.setDuration(1000);
        transitionIn.addTarget(R.id.floating_menu_bt);
        TransitionManager.beginDelayedTransition(binding.mainLayout, transitionIn);
        binding.floatingMenuBt.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void deleteMultiNote(List<Long> ids) {
        appDatabase.noteDAO().deleteListItem(ids).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
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
