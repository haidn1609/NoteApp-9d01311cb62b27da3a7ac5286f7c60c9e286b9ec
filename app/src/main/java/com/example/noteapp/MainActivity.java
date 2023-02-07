package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
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

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.noteapp.adapter.recyclerView.RcvNoteAdapter;
import com.example.noteapp.adapter.recyclerView.RcvNoteItemClick;
import com.example.noteapp.databinding.ActivityMainBinding;
import com.example.noteapp.model.NoteModel;
import com.example.noteapp.room.AppDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_CODE_BACKGROUND_SETTING) {
                        initView();
                    }
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
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        binding.rcvListNote.setLayoutManager(layoutManager);
        binding.rcvListNote.setAdapter(rcvNoteAdapter);
        getListNote();
        //add event
        rcvNoteAdapter.setOnClickItem(new RcvNoteItemClick() {
            @Override
            public void editItemClick(NoteModel noteModel) {
                Toast.makeText(MainActivity.this, noteModel.getId() + "." + noteModel.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });
        binding.iconSetting.setOnClickListener(v -> {
            Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
            Intent itBackgroundSetting = new Intent(MainActivity.this, BackgroundSettingActivity.class);
            activityResultLauncher.launch(itBackgroundSetting);
        });
        binding.btnAddNote.setOnClickListener(v -> {
            //onClickAddNote();
            //getListNote();
            binding.floatingMenuBt.collapse();
            Intent itEditNote = new Intent(MainActivity.this, EditNoteActivity.class);
            itEditNote.putExtra(ACTION,ACTION_ADD);
            activityResultLauncher.launch(itEditNote);

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

    private void initView() {
        if (sp.getString(BACKGROUND_COLOR, "-1").equals("-1")) {
            binding.mainLayout.setBackgroundColor(getResources().getColor(R.color.bg_white));
        } else {
            binding.mainLayout.setBackground(getResources().getDrawable(Integer.parseInt(sp.getString(BACKGROUND_COLOR, "NONE"))));
        }
        if (sp.getString(APPBAR_COLOR, "-1").equals("-1")) {
            binding.appBar.setBackgroundColor(getResources().getColor(R.color.theme_blue));
        } else {
            binding.appBar.setBackgroundColor(getResources().getColor(Integer.parseInt(sp.getString(APPBAR_COLOR, "500021"))));

        }
    }

    private void getListNote() {
        appDatabase.noteDAO().getAllNote().subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<NoteModel>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d("getListNote", "subscribe");
                        mDisposable = d;
                    }

                    @Override
                    public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<NoteModel> noteModels) {
                        noteModelList = noteModels;
                        Log.d("getListNote", "on next: " + noteModelList.size() + " item");
                        setDataRcvNote();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        Log.d("getListNote", "onError: get list note err"+e);
                    }
                });
    }

    private void onClickAddNote() {
        NoteModel noteModel = new NoteModel();
        Calendar calendar = Calendar.getInstance();
        noteModel.setTitle("title note");
        noteModel.setContent("content note");
        noteModel.setModifyDay(calendar.getTime());
        noteModel.setColorBackgroud(R.color.bg_note_blue);
        noteModel.setColorTitle(R.color.tt_note_blue);

        appDatabase.noteDAO().insert(noteModel).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                        Log.d("add note", "subscribe");
                        mDisposable = d;
                    }

                    @Override
                    public void onComplete() {
                        Log.d("Add note", "complete");
                        getListNote();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {

                    }
                });
    }

    private void setDataRcvNote() {
        if (noteModelList.size() > 0) {
            binding.textNotNote.setVisibility(View.GONE);
            binding.rcvListNote.setVisibility(View.VISIBLE);
            rcvNoteAdapter.setDataAdapter(noteModelList, MainActivity.this);
        } else {
            binding.textNotNote.setVisibility(View.VISIBLE);
            binding.rcvListNote.setVisibility(View.GONE);
        }
    }

    private static void showBtnMenu(ActivityMainBinding binding, boolean show) {
        Transition transitionIn = new Fade();
        transitionIn.setDuration(1000);
        transitionIn.addTarget(R.id.floating_menu_bt);
        TransitionManager.beginDelayedTransition(binding.mainLayout, transitionIn);
        binding.floatingMenuBt.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }

}
