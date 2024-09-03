package com.example.hattori;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class VoiceSettingDialog {
    public static void showDialog(Context context) {
        // 1. 創建一個 LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(context);

        // 2. 載入自定義的佈局
        View dialogView = inflater.inflate(R.layout.voice_setting_dialog, null);

        // 3. 使用 AlertDialog.Builder 並設置自定義的佈局
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);

        // 4. 取得佈局中的元件
        // 單字
        CheckBox cWord = (CheckBox) dialogView.findViewById(R.id.voice_setting_word_checkbox);
        cWord.setChecked(GlobalVariable.VOICE_SETTING_CHECKED[0]);

        EditText eWord = (EditText) dialogView.findViewById(R.id.voice_setting_word_edittext);
        eWord.setText(String.valueOf(GlobalVariable.VOICE_SETTING_TIMES[0]));


        // 中文
        CheckBox cWordZh = (CheckBox) dialogView.findViewById(R.id.voice_setting_word_zh_checkbox);
        cWordZh.setChecked(GlobalVariable.VOICE_SETTING_CHECKED[1]);

        EditText eWordZh = (EditText) dialogView.findViewById(R.id.voice_setting_word_zh_edittext);
        eWordZh.setText(String.valueOf(GlobalVariable.VOICE_SETTING_TIMES[1]));


        // 例句
        CheckBox cSentence = (CheckBox) dialogView.findViewById(R.id.voice_setting_sentence_checkbox);
        cSentence.setChecked(GlobalVariable.VOICE_SETTING_CHECKED[2]);

        EditText eSentence = (EditText) dialogView.findViewById(R.id.voice_setting_sentence_edittext);
        eSentence.setText(String.valueOf(GlobalVariable.VOICE_SETTING_TIMES[2]));


        // 翻譯
        CheckBox cSentenceZh = (CheckBox) dialogView.findViewById(R.id.voice_setting_sentence_zh_checkbox);
        cSentenceZh.setChecked(GlobalVariable.VOICE_SETTING_CHECKED[3]);

        EditText eSentenceZh = (EditText) dialogView.findViewById(R.id.voice_setting_sentence_zh_edittext);
        eSentenceZh.setText(String.valueOf(GlobalVariable.VOICE_SETTING_TIMES[3]));


        // dialog 按鈕
        Button bOk = (Button) dialogView.findViewById(R.id.voice_setting_ok);
        Button bCancel = (Button) dialogView.findViewById(R.id.voice_setting_cancel);

        // 5. 創建對話框
        AlertDialog dialog = builder.create();

        // 6. 設定按鈕點擊事件
        bOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 儲存 checkbox 狀態
                GlobalVariable.VOICE_SETTING_CHECKED[0] = cWord.isChecked();
                GlobalVariable.VOICE_SETTING_CHECKED[1] = cWordZh.isChecked();
                GlobalVariable.VOICE_SETTING_CHECKED[2] = cSentence.isChecked();
                GlobalVariable.VOICE_SETTING_CHECKED[3] = cSentenceZh.isChecked();

                // 儲存播放次數
                GlobalVariable.VOICE_SETTING_TIMES[0] = Integer.parseInt(eWord.getText().toString());
                GlobalVariable.VOICE_SETTING_TIMES[1] = Integer.parseInt(eWordZh.getText().toString());
                GlobalVariable.VOICE_SETTING_TIMES[2] = Integer.parseInt(eSentence.getText().toString());
                GlobalVariable.VOICE_SETTING_TIMES[3] = Integer.parseInt(eSentenceZh.getText().toString());

                dialog.dismiss();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        // 7. 顯示對話框
        dialog.show();
    }
}
