package com.example.noteapp.view.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.noteapp.KEY;
import com.example.noteapp.R;
import com.example.noteapp.databinding.FragmentEditTextNoteBinding;

import java.util.Objects;

import jp.wasabeef.richeditor.RichEditor;

public class EditTextNoteFragment extends Fragment implements KEY {

    private FragmentEditTextNoteBinding binding;

    private String contentHtml;
    private String action;
    public static EditTextNoteFragment newInstance(String action,String contentHtml) {
        EditTextNoteFragment fragment = new EditTextNoteFragment();
        Bundle args = new Bundle();
        args.putString(NOTE_CONTENT, contentHtml);
        args.putString(ACTION, action);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bdReceive = getArguments();
        if (bdReceive != null) {
            contentHtml = bdReceive.getString(NOTE_CONTENT);
            action = bdReceive.getString(ACTION);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_text_note, container, false);
        configurationRichEdit();
        return binding.getRoot();
    }

    private void configurationRichEdit() {
        RichEditor editor = binding.etNoteContent;
        editor.setEditorFontSize(18);
        editor.setBackgroundColor(requireContext().getColor(R.color.bg_opacity));
        editor.setEditorBackgroundColor(requireContext().getColor(R.color.bg_opacity));
        editor.setPadding(10, 10, 10, 10);
        editor.setPlaceholder(requireContext().getString(R.string.hide_et_content));
        if (contentHtml != null) editor.setHtml(contentHtml);
        if(action.equals(ACTION_VIEW)){
            editor.setEnabled(false);
            binding.richEditorBar.setVisibility(View.GONE);
        }
        binding.actionUndo.setOnClickListener(v -> editor.undo());
        binding.actionRedo.setOnClickListener(v -> editor.redo());
        binding.actionBold.setOnClickListener(v -> editor.setBold());
        binding.actionItalic.setOnClickListener(v -> editor.setItalic());
        binding.actionStrikethroug.setOnClickListener(v -> editor.setStrikeThrough());
        binding.actionUnderline.setOnClickListener(v -> editor.setUnderline());
        binding.actionBlockquote.setOnClickListener(v -> editor.setBlockquote());
        binding.actionInsertBullets.setOnClickListener(v -> editor.setBullets());
        binding.actionInsertNumber.setOnClickListener(v -> editor.setNumbers());
        binding.actionInsertCheckbox.setOnClickListener(v -> editor.insertTodo());
    }

    public String getContentHtml(){
        return binding.etNoteContent.getHtml();
    }
}