package com.example.noteapp.view;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.EditNoteActivity;
import com.example.noteapp.KEY;
import com.example.noteapp.R;
import com.example.noteapp.adapter.recyclerView.RcvBtsItemClick;
import com.example.noteapp.adapter.recyclerView.RcvBtsThemeAdapter;
import com.example.noteapp.model.OptionNoteTheme;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.List;

public class BottomSheetThemeFragment extends BottomSheetDialogFragment implements KEY {
    private List<OptionNoteTheme> listTheme;
    private Button btnCancel;
    private RcvBtsThemeAdapter rcvBtsThemeAdapter;
    private RecyclerView rcvListTheme;
    private EditNoteActivity editNoteActivity;

    public static BottomSheetThemeFragment newInstance(List<OptionNoteTheme> listTheme) {
        Bundle args = new Bundle();
        args.putSerializable(BTS_THEME, (Serializable) listTheme);
        BottomSheetThemeFragment fragment = new BottomSheetThemeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bdReceive = getArguments();
        rcvBtsThemeAdapter = new RcvBtsThemeAdapter();
        editNoteActivity = (EditNoteActivity) getActivity();
        if (bdReceive != null) {
            listTheme = (List<OptionNoteTheme>) bdReceive.get(BTS_THEME);
            rcvBtsThemeAdapter.setDataAdapter(listTheme, getContext());
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        BottomSheetDialog btsDialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        View viewDialog = LayoutInflater.from(getContext()).inflate(R.layout.layout_bottom_sheet_theme, null);
        btsDialog.setContentView(viewDialog);

        rcvListTheme = viewDialog.findViewById(R.id.rcv_bts_theme);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rcvListTheme.setLayoutManager(layoutManager);
        rcvListTheme.setAdapter(rcvBtsThemeAdapter);
        rcvBtsThemeAdapter.setRcvBtsItemClick(new RcvBtsItemClick() {
            @Override
            public void setTheme(OptionNoteTheme optionNoteTheme) {
                editNoteActivity.setThemeNote(optionNoteTheme);
            }
        });
        btnCancel = viewDialog.findViewById(R.id.btn_cancel_bts_theme);
        btnCancel.setOnClickListener(v -> btsDialog.dismiss());
        return btsDialog;
    }
}
