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
            sp.edit().putString(BACKGROUND_COLOR, url).apply();
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

        List<String> listBackground = getListBackGround();
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

    private List<String> getListBackGround() {
        List<String> listBackground = new ArrayList<>();
        listBackground.add("https://www.whitescreen.online/image/white-background.png");
        listBackground.add("https://didongviet.vn/dchannel/wp-content/uploads/2022/12/14hinh-nen-cute-hinh-nen-4k-cho-dien-thoai-didongviet@2x-576x1024.jpg");
        listBackground.add("https://didongviet.vn/dchannel/wp-content/uploads/2022/12/hinh-nen-cute-hinh-nen-4k-cho-dien-thoai-didongviet@2x-576x1024.jpg");
        listBackground.add("https://nhaxinhplaza.vn/wp-content/uploads/nhung-hinh-nen-dep-cute.jpg");
        listBackground.add("https://img.lovepik.com/background/20211029/medium/lovepik-mobile-phone-wallpaper-background-image_400326387.jpg");
        listBackground.add("https://noithatbinhminh.com.vn/wp-content/uploads/2022/08/hinh-nen-dien-thoai-15.jpg");
        listBackground.add("https://nhattop.com/wp-content/uploads/2017/07/hinh-nen-dien-thoai-1.jpg");
        listBackground.add("https://o.vdoc.vn/data/image/2022/08/25/anh-nen-dien-thoai-gau-cute.jpg");
        listBackground.add("https://img.lovepik.com/background/20211101/medium/lovepik-scenery-city-mobile-phone-wallpaper-background-image_400521810.jpg");
        listBackground.add("https://luv.vn/wp-content/uploads/2021/09/hinh-nen-dien-thoai-dep-30.jpg");
        listBackground.add("https://i.bloganchoi.com/bloganchoi.com/wp-content/uploads/2022/05/hinh-nen-cute-dien-thoai-desktop-10-696x1237.jpg?fit=700%2C20000&quality=95&ssl=1");
        listBackground.add("https://toanthaydinh.com/wp-content/uploads/2020/04/hinh-nen-dien-thoai-phong-canh-thien-nhien-cuc-dep-chat-luong-hd-25-576x1024-2.jpg");
        return listBackground;
    }
}