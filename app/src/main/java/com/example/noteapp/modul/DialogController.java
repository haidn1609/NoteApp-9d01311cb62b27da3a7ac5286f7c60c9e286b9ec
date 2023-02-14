package com.example.noteapp.modul;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.noteapp.R;
import com.example.noteapp.model.NoteModel;

import java.util.List;
import java.util.stream.Collectors;

public class DialogController {
    private final Context mContext;
    private DialogOnEventListener dialogOnEventListener;
    public DialogController(Context mContext) {
        this.mContext = mContext;
    }
    public void setDialogOnEventListener(DialogOnEventListener dialogOnEventListener){
        this.dialogOnEventListener =dialogOnEventListener;
    }
    public void openDialogDelete(int count,List<NoteModel> noteModelList) {
        Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_dialog_delete);
        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttr = window.getAttributes();
        windowAttr.gravity = Gravity.CENTER;
        window.setAttributes(windowAttr);
        dialog.setCancelable(true);
        TextView dial_mess = dialog.findViewById(R.id.txt_dial_mess);
        ImageView icon_close = dialog.findViewById(R.id.icon_close_dial);
        Button btn_cancel = dialog.findViewById(R.id.btn_dial_cancel);
        Button btn_delete = dialog.findViewById(R.id.btn_dial_delete);
        if (count > 1) {
            dial_mess.setText(mContext.getString(R.string.dial_delete_mess2));
        } else dial_mess.setText(mContext.getString(R.string.dial_delete_mess1));
        icon_close.setOnClickListener(v -> dialog.dismiss());
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
        btn_delete.setOnClickListener(v -> {
            List<Long> filter = noteModelList.stream()
                    .filter(NoteModel::isSelect)
                    .map(NoteModel::getId)
                    .collect(Collectors.toList());
            dialogOnEventListener.clickDeleteDial(filter);
            dialog.dismiss();
        });
        dialog.show();
    }
}
