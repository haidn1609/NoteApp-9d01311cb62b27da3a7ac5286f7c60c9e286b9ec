package com.example.noteapp.view.fragment;

import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.noteapp.KEY;
import com.example.noteapp.R;
import com.example.noteapp.databinding.FragmentEditListNoteBinding;

public class EditListNoteFragment extends Fragment implements KEY {
    FragmentEditListNoteBinding binding;

    public static EditListNoteFragment newInstance() {
        EditListNoteFragment fragment = new EditListNoteFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_edit_list_note, container, false);
        return binding.getRoot();
    }
}