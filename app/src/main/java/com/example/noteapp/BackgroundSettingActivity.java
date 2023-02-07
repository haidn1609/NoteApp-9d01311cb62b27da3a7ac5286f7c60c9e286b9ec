package com.example.noteapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.adapter.recyclerView.RcvThemeAdapter;
import com.example.noteapp.adapter.recyclerView.RcvThemeItemClick;
import com.example.noteapp.databinding.ActivityBackgroudSettingBinding;

import java.util.ArrayList;
import java.util.List;

public class BackgroundSettingActivity extends AppCompatActivity implements KEY {
    private ActivityBackgroudSettingBinding binding;
    private RcvThemeAdapter rcvThemeAdapter;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_backgroud_setting);
        sp = getBaseContext().getSharedPreferences(SP_BACKGROUND_SETTING, MODE_PRIVATE);
        rcvThemeAdapter = new RcvThemeAdapter();
        initView();
//      set event
        rcvThemeAdapter.setRcvThemeItemClick(new RcvThemeItemClick() {
            @Override
            public void selectThemeItem(int val) {
                sp.edit().putString(APPBAR_COLOR, val + "").commit();
                Toast.makeText(BackgroundSettingActivity.this, getString(R.string.toast_change_theme), Toast.LENGTH_SHORT).show();
            }
        });
        binding.iconBack.setOnClickListener(v -> {
            Intent intent = new Intent();
//            sp.edit().putString(BACKGROUND_COLOR, R.drawable.bg_main_layout_1+"").commit();
            setResult(RESULT_CODE_BACKGROUND_SETTING, intent);
            finish();
        });
    }

    private void initView() {
        List<Integer> listTheme = getListTheme();
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
        binding.rcvListTheme.setLayoutManager(layoutManager);
        rcvThemeAdapter.setDataAdapter(listTheme, this);
        binding.rcvListTheme.setAdapter(rcvThemeAdapter);
    }

    private List<Integer> getListTheme() {
        List<Integer> listTheme = new ArrayList<>();
        listTheme.add(R.color.theme_blue);
        listTheme.add(R.color.theme_orange);
        listTheme.add(R.color.theme_purple);
        listTheme.add(R.color.theme_green);
        listTheme.add(R.color.theme_red);
        return listTheme;
    }
}