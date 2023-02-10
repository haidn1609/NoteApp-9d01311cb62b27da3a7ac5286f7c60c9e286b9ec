package com.example.noteapp;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.noteapp.adapter.recyclerView.RcvBackupNoteAdapter;
import com.example.noteapp.adapter.recyclerView.RcvBackupNoteClick;
import com.example.noteapp.databinding.ActivityBackupNoteBinding;
import com.example.noteapp.model.NoteModel;
import com.example.noteapp.modul.DialogController;
import com.example.noteapp.room.AppDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BackupNoteActivity extends AppCompatActivity implements KEY {
    private ActivityBackupNoteBinding binding;
    private RcvBackupNoteAdapter rcvBackupNoteAdapter;
    private AppDatabase appDatabase;
    private Disposable mDisposable;
    private List<NoteModel> noteModelList;
    private DialogController dialogController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_backup_note);
        appDatabase = AppDatabase.getInstance(this);
        dialogController = new DialogController(this);
        rcvBackupNoteAdapter = new RcvBackupNoteAdapter();
        noteModelList = new ArrayList<>();
        initView();
//      ↓ set event
        binding.iconBack.setOnClickListener(v -> {
            setResult(RESULT_CODE_EDIT_NOTE, new Intent());
            finish();
        });
        binding.iconBackMode.setOnClickListener(v -> getListNote());
        binding.iconBackup.setOnClickListener(v -> {
            if (rcvBackupNoteAdapter.getCountSelect() > 0) {
                backupMultiNote(getListSelect());
            }
        });
        binding.iconDelete.setOnClickListener(v -> {
            if (rcvBackupNoteAdapter.getCountSelect() > 0) {
                dialogController.openDialogDelete(rcvBackupNoteAdapter.getCountSelect(), noteModelList);
            }
        });
        rcvBackupNoteAdapter.setRcvBackupNoteClick(new RcvBackupNoteClick() {
            @Override
            public void viewNote(Long id) {
                viewNoteById(id);
            }

            @Override
            public void openSelectMode(boolean isOpen) {
                showSelectBar(true);
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void setCountSelect(int count) {
                binding.txtCountSelect.setText(getString(R.string.hide_txt_select) + " " + count + " " + getString(R.string.hide_txt_note));
            }
        });
        dialogController.setDialogOnEventListener(this::deleteMultiNote);
    }

    //    ↓ set vỉew
    private void initView() {
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        binding.rcvListBackupNote.setLayoutManager(layoutManager);
        getListNote();
    }

    private void showSelectBar(boolean isShow) {
        if (isShow) {
            binding.appBarSelect.setVisibility(View.VISIBLE);
            binding.appBar.setVisibility(View.INVISIBLE);
        } else {
            binding.appBarSelect.setVisibility(View.INVISIBLE);
            binding.appBar.setVisibility(View.VISIBLE);
            rcvBackupNoteAdapter.setSelectMode(false, 0);
        }
    }

    //  ↓ roomData
    private void getListNote() {
        showSelectBar(false);
        appDatabase.noteDAO().getAllNoteByStatus(STATUS_PRIVATE).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<List<NoteModel>>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull List<NoteModel> noteModels) {
                noteModelList = noteModels;
                rcvBackupNoteAdapter.setDataAdapter(noteModelList, BackupNoteActivity.this);
                binding.rcvListBackupNote.setAdapter(rcvBackupNoteAdapter);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d("getListNote", "onError: get back up err" + e);
            }
        });
    }

    private void viewNoteById(Long id) {
        appDatabase.noteDAO().getNoteById(id).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<NoteModel>() {
            @Override
            public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {
                mDisposable = d;
            }

            @Override
            public void onSuccess(@io.reactivex.rxjava3.annotations.NonNull NoteModel noteModel) {
                Intent itEditNote = new Intent(BackupNoteActivity.this, EditNoteActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable(NOTE, noteModel);
                itEditNote.putExtra(ACTION, ACTION_VIEW);
                itEditNote.putExtras(bundle);
                startActivity(itEditNote);
            }

            @Override
            public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                Log.d(TAG, "onError: get note err " + e);
            }
        });
    }

    private void backupMultiNote(List<Long> ids) {
        appDatabase.noteDAO().updateStatus(ids, STATUS_PUBLIC).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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
                Log.d("backup multi note", "onError: backup multi note err" + e);
            }
        });
    }

    private void deleteMultiNote(List<Long> ids) {
        appDatabase.noteDAO().deleteListItem(ids).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new CompletableObserver() {
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

    //  ↓logic fun
    private List<Long> getListSelect() {
        return noteModelList.stream()
                .filter(NoteModel::isSelect)
                .map(NoteModel::getId)
                .collect(Collectors.toList());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.dispose();
        }
    }
}