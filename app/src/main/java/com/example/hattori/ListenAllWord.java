package com.example.hattori;

import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hattori.database.AppDatabase;
import com.example.hattori.database.AppDatabaseClient;
import com.example.hattori.database.Word;
import com.example.hattori.database.WordDao;
import com.example.hattori.databinding.ActivityListenAllWordBinding;
import com.example.hattori.word_listening.WordListening;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class ListenAllWord extends AppCompatActivity {
    private ActivityListenAllWordBinding binding;

    private TextView tWord, tKk, tWordZh, tSentence, tSentenceZh;


    // database
    AppDatabase db;
    WordDao wordDao;


    // data
    List<Word> wordList;


    // current word
    private int currentWordIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListenAllWordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        initDatabase();

    }


    private void init() {
        tWord = binding.listenAllWordWord;
        tKk = binding.listenAllWordKk;
        tWordZh = binding.listenAllWordWordZh;
        tSentence = binding.listenAllWordSentence;
        tSentenceZh = binding.listenAllWordSentenceZh;
    }

    // 資料庫初始化
    private void initDatabase() {
        // 參數初始化
        db = AppDatabaseClient.getInstance(ListenAllWord.this).getAppDatabase();
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
                        startListening();
                    }
                });
            }
        });
    }


    private void startListening() {
        tWord.setText(wordList.get(currentWordIndex).getWord());
        tWordZh.setText(wordList.get(currentWordIndex).getWord_zh());
        tKk.setText(wordList.get(currentWordIndex).getKk());
        tSentence.setText(wordList.get(currentWordIndex).getSentence());
        tSentenceZh.setText(wordList.get(currentWordIndex).getSentence_zh());
    }
}