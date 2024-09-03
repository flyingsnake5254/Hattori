package com.example.hattori.word_listening;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.Voice;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuProvider;

import com.example.hattori.ChoiceVoice;
import com.example.hattori.GlobalVariable;
import com.example.hattori.ListenAllWord;
import com.example.hattori.R;
import com.example.hattori.Utils.SpeechUtil;
import com.example.hattori.VoiceSettingDialog;
import com.example.hattori.database.AppDatabase;
import com.example.hattori.database.AppDatabaseClient;
import com.example.hattori.database.Word;
import com.example.hattori.database.WordDao;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class WordListening extends AppCompatActivity{

    // binding
    com.example.hattori.databinding.ActivityWordListeningBinding binding;


    // database
    AppDatabase db;
    WordDao wordDao;


    // data
    List<Word> wordList;


    // 每頁顯示的單字卡數量
    private int PAGE_ITEM_SIZE = GlobalVariable.PAGE_ITEM_SIZE;


    // 目前頁面第一個單字卡
    private int currentWordIndex = 0;


    // 添加 menuProvider 成員變數
    private MenuProvider menuProvider;


    // 語音物件
    SpeechUtil speechUtil = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.hattori.databinding.ActivityWordListeningBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initDatabase();
        init();
        listener();

        // action bar
        menuProvider = new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.word_listening_bar_menu, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int menuItemId = menuItem.getItemId();
                // 單字卡播放 setting
                if (menuItemId == R.id.voice_setting) {
                    VoiceSettingDialog.showDialog(WordListening.this);
                    return true;
                }
                // 選擇人聲
                else if (menuItemId == R.id.word_listening_select_voice) {
                    Intent intent = new Intent(WordListening.this, ChoiceVoice.class);
                    startActivity(intent);
                    return true;
                }
                // 播放所有單字
                else if (menuItemId == R.id.word_listening_start) {
                    Intent intent = new Intent(WordListening.this, ListenAllWord.class);
                    startActivity(intent);
                    return true;
                }
                return false;
            }

        };
        addMenuProvider(menuProvider);
    }

    // 資料庫初始化
    private void initDatabase() {
        // 參數初始化
        db = AppDatabaseClient.getInstance(WordListening.this).getAppDatabase();
        wordDao = db.wordDao();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // 取得 Words
                wordList = wordDao.getAllWords();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Collections.shuffle(wordList);
                        initWordLayout();
                    }
                });
            }
        });
    }


    private void initWordLayout() {
        // 單字 layout 移除所有 view
        binding.wordListeningWordLayout.removeAllViews();

        // 添加單字卡到 layout
        for (int i = 0 ; (i < PAGE_ITEM_SIZE) && (currentWordIndex + i < wordList.size()) ; i ++ ) {
            // 取得單字卡 view
            View wordCardView = LayoutInflater.from(WordListening.this).inflate(R.layout.card_item_word_listening, binding.wordListeningWordLayout, false);

            // 當前單字卡
            Word word = wordList.get(currentWordIndex + i);

            // 取得單字卡 widget
            // word
            TextView tWord = (TextView) wordCardView.findViewById(R.id.word_listening_word);
            tWord.setText(word.getWord());
            // kk
            TextView tKk = (TextView) wordCardView.findViewById(R.id.word_listening_kk);
            tKk.setText(word.getKk());
            // 單字中文
            TextView tWordZh = (TextView) wordCardView.findViewById(R.id.word_listening_word_zh);
            tWordZh.setText(word.getWord_zh());
            // 例句
            TextView tSentence = (TextView) wordCardView.findViewById(R.id.word_listening_sentence);
            tSentence.setText(word.getSentence());
            // 翻譯
            TextView tSentenceZh = (TextView) wordCardView.findViewById(R.id.word_listening_sentence_zh);
            tSentenceZh.setText(word.getSentence_zh());
            // 備註
            TextView tRemark = (TextView) wordCardView.findViewById(R.id.word_listening_remart);
            tRemark.setText(word.getRemark());
            // 單字卡語音按鈕
            ShapeableImageView imgSpeak = (ShapeableImageView) wordCardView.findViewById(R.id.word_listening_speak);
            imgSpeak.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> contents = new ArrayList<String>();
                    ArrayList<Voice> voices = new ArrayList<Voice>();
                    String speakContent = "";
                    // 單字
                    if (GlobalVariable.VOICE_SETTING_CHECKED[0]) {
                        speakContent = "<speak>";
                        for (int i = 0 ; i < GlobalVariable.VOICE_SETTING_TIMES[0] ; i ++) {
                            speakContent += tWord.getText().toString();
                            speakContent += "<break time=\"500ms\"/>";
                        }
                        speakContent += "<break time=\"500ms\"/></speak>";
                        contents.add(speakContent);
                    }
                    else {
                        speakContent = "";
                        contents.add(speakContent);
                    }
                    voices.add(speechUtil.getVoiceByName(GlobalVariable.CURRENT_EN_VOICE));

                    // 中文
                    if (GlobalVariable.VOICE_SETTING_CHECKED[1]) {
                        speakContent = "<speak>";
                        for (int i = 0 ; i < GlobalVariable.VOICE_SETTING_TIMES[1] ; i ++) {
                            speakContent += tWordZh.getText().toString();
                            speakContent += "<break time=\"500ms\"/>";
                        }
                        speakContent += "<break time=\"700ms\"/></speak>";
                        contents.add(speakContent);
                    }
                    else {
                        speakContent = "";
                        contents.add(speakContent);
                    }
                    voices.add(speechUtil.getVoiceByName(GlobalVariable.CURRENT_ZH_VOICE));

                    // 例句
                    if (GlobalVariable.VOICE_SETTING_CHECKED[2]) {
                        speakContent = "<speak>";
                        for (int i = 0 ; i < GlobalVariable.VOICE_SETTING_TIMES[2] ; i ++) {
                            speakContent += tSentence.getText().toString();
                            speakContent += "<break time=\"500ms\"/>";
                        }
                        speakContent += "<break time=\"700ms\"/></speak>";
                        contents.add(speakContent);
                    }
                    else {
                        speakContent = "";
                        contents.add(speakContent);
                    }
                    voices.add(speechUtil.getVoiceByName(GlobalVariable.CURRENT_EN_VOICE));

                    // 翻譯
                    if (GlobalVariable.VOICE_SETTING_CHECKED[3]) {
                        speakContent = "<speak>";
                        for (int i = 0 ; i < GlobalVariable.VOICE_SETTING_TIMES[3] ; i ++) {
                            speakContent += tSentenceZh.getText().toString();
                            speakContent += "<break time=\"500ms\"/>";
                        }
                        speakContent += "<break time=\"700ms\"/></speak>";
                        contents.add(speakContent);
                    }
                    else {
                        speakContent = "";
                        contents.add(speakContent);
                    }
                    voices.add(speechUtil.getVoiceByName(GlobalVariable.CURRENT_ZH_VOICE));
                    speechUtil.speakWithDiffVoice(contents, voices);
                }
            });


            // 添加到 layout
            binding.wordListeningWordLayout.addView(wordCardView);
        }
    }

    private void listener() {
        // 上一頁
        binding.wordListeningPrePage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentWordIndex != 0) {
                    currentWordIndex -= PAGE_ITEM_SIZE;
                    initWordLayout();
                }
            }
        });

        // 下一頁
        binding.wordListeningNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentWordIndex + PAGE_ITEM_SIZE < wordList.size()) {
                    currentWordIndex += PAGE_ITEM_SIZE;
                    initWordLayout();
                }
            }
        });
    }

    private void init() {
        speechUtil = new SpeechUtil(WordListening.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (speechUtil != null) {
            speechUtil.shutdown();
        }
    }
}