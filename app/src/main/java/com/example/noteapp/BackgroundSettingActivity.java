package com.example.noteapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.adapter.recyclerView.RcvBackgroundAdapter;
import com.example.noteapp.adapter.recyclerView.RcvThemeAdapter;
import com.example.noteapp.databinding.ActivityBackgroudSettingBinding;

import java.util.ArrayList;
import java.util.List;

public class BackgroundSettingActivity extends AppCompatActivity implements KEY {
    private ActivityBackgroudSettingBinding binding;
    private RcvThemeAdapter rcvThemeAdapter;
    private RcvBackgroundAdapter rcvBackgroundAdapter;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_backgroud_setting);
        sp = getBaseContext().getSharedPreferences(SP_BACKGROUND_SETTING, MODE_PRIVATE);
        rcvThemeAdapter = new RcvThemeAdapter();
        rcvBackgroundAdapter = new RcvBackgroundAdapter();
        initView();
//      ↓set event
        rcvThemeAdapter.setRcvThemeItemClick(val -> {
            sp.edit().putString(APPBAR_COLOR, val + "").apply();
            Toast.makeText(BackgroundSettingActivity.this, getString(R.string.toast_change_theme), Toast.LENGTH_SHORT).show();
        });
        rcvBackgroundAdapter.setRcvBgItemClick(url -> {
            sp.edit().putInt(BACKGROUND_COLOR, url).apply();
            Toast.makeText(BackgroundSettingActivity.this, getString(R.string.toast_change_theme), Toast.LENGTH_SHORT).show();
        });
        binding.iconBack.setOnClickListener(v -> {
            Intent intent = new Intent();
            setResult(RESULT_CODE_BACKGROUND_SETTING, intent);
            finish();
        });
    }
//    ↓ edit view
    private void initView() {
        List<Integer> listTheme = getListTheme();
        RecyclerView.LayoutManager layoutManagerTheme = new GridLayoutManager(this, 4, RecyclerView.VERTICAL, false);
        binding.rcvListTheme.setLayoutManager(layoutManagerTheme);
        rcvThemeAdapter.setDataAdapter(listTheme, this);
        binding.rcvListTheme.setAdapter(rcvThemeAdapter);

        List<Integer> listBackground = getListBackGround();
        RecyclerView.LayoutManager layoutManagerBg = new GridLayoutManager(this, 2, RecyclerView.VERTICAL, false);
        binding.rcvListBackground.setLayoutManager(layoutManagerBg);
        rcvBackgroundAdapter.setDataAdapter(listBackground, this);
        binding.rcvListBackground.setAdapter(rcvBackgroundAdapter);
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

    private List<Integer> getListBackGround() {
        List<Integer> listBackground = new ArrayList<>();
        listBackground.add(R.drawable.bg_1);
        listBackground.add(R.drawable.bg_2);
        listBackground.add(R.drawable.bg_3);
        listBackground.add(R.drawable.bg_4);
        listBackground.add(R.drawable.bg_5);
        listBackground.add(R.drawable.bg_6);
        listBackground.add(R.drawable.bg_7);
        listBackground.add(R.drawable.bg_8);
        listBackground.add(R.drawable.bg_9);
        return listBackground;
    }
}