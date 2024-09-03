package com.example.hattori.sentence_choice;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hattori.MainActivity;
import com.example.hattori.R;
import com.example.hattori.Utils.RandomItemsSelector;
import com.example.hattori.database.AppDatabase;
import com.example.hattori.database.AppDatabaseClient;
import com.example.hattori.database.Word;
import com.example.hattori.database.WordDao;
import com.example.hattori.databinding.ActivityMainBinding;
import com.example.hattori.databinding.ActivitySentenceChoiceBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class SentenceChoice extends AppCompatActivity {

    private ActivitySentenceChoiceBinding binding;

    // 題目數量
    private int numberOfQuestion;

    // 題庫
    private AppDatabase db;
    private List<Word> wordList;
    private List<Word> testWord;
    private List<Question> questionList;
    private WordDao wordDao;
    private int currentQuestionIndex = 0;
    private Question currentQuestion;

    List<Integer> icons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySentenceChoiceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        listener();
    }
    private void init() {
        questionList = new ArrayList<>();
        icons  = Arrays.asList(
                R.drawable.ai,
                R.drawable.akashichi,
                R.drawable.ari,
                R.drawable.conan,
                R.drawable.gin,
                R.drawable.hottori,
                R.drawable.kazha,
                R.drawable.kid,
                R.drawable.kisaki_eri,
                R.drawable.kogoro,
                R.drawable.mori,
                R.drawable.sera,
                R.drawable.shinichi,
                R.drawable.zero
        );
    }


    private void listener() {
        // 輸入題數 edittext 監聽器
        binding.testSentenceChoiceNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.testSentenceChoiceNumber.getText().toString().length() > 0) {
                    binding.blood.setVisibility(View.VISIBLE);
                }
                else {
                    binding.blood.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.blood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.sentenceChoiceLayout.removeAllViews();

                // 題目數量
                numberOfQuestion = Integer.parseInt(binding.testSentenceChoiceNumber.getText().toString());

                // initial database
                initDatabase();
            }
        });
    }

    // 資料庫初始化
    private void initDatabase() {
        // 參數初始化
        db = AppDatabaseClient.getInstance(SentenceChoice.this).getAppDatabase();
        wordDao = db.wordDao();

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                // 取得 Words
                wordList = wordDao.getAllWords();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        makeQuestions();
                    }
                });
            }
        });
    }

    private void makeQuestions() {
        // 選取要測試的單字
        if (numberOfQuestion > wordList.size()) {
            numberOfQuestion = wordList.size();
        }
        testWord = RandomItemsSelector.getRandomItemsFromList(wordList, numberOfQuestion);

        // 製作題目
        for (Word targetWord : testWord) {
            questionList.add(new Question(targetWord, RandomItemsSelector.getRandomItemsFromList(icons, 4), RandomItemsSelector.getRandomWords(wordList, targetWord, 3)));
        }
        initSentenceChoiceQuestionLayout();
    }

    private void initSentenceChoiceQuestionLayout() {
        System.out.println("current index : " + currentQuestionIndex + " / question list size : " + questionList.size());
        if (currentQuestionIndex >= questionList.size()) {
            System.out.println("finish");
            Intent intent = new Intent(SentenceChoice.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {
            binding.sentenceChoiceLayout.removeAllViews();

            // 取得題目 layout
            View view = LayoutInflater.from(SentenceChoice.this).inflate(R.layout.sentence_choice_question, binding.sentenceChoiceLayout, false);

            // current question
            currentQuestion = questionList.get(currentQuestionIndex);

            // 取得 widget
            // 題目
            TextView tTitle = view.findViewById(R.id.sentence_choice_title);
            tTitle.setText(currentQuestion.question);

            // ans1
            ImageView imgAns1 = view.findViewById(R.id.sentence_choice_ans1_icon);
            imgAns1.setImageResource(currentQuestion.icons.get(0));
            TextView tAns1 = view.findViewById(R.id.sentence_choice_ans1);
            tAns1.setText(currentQuestion.choices.get(0));

            // ans2
            ImageView imgAns2 = view.findViewById(R.id.sentence_choice_ans2_icon);
            imgAns2.setImageResource(currentQuestion.icons.get(1));
            TextView tAns2 = view.findViewById(R.id.sentence_choice_ans2);
            tAns2.setText(currentQuestion.choices.get(1));

            // ans3
            ImageView imgAns3 = view.findViewById(R.id.sentence_choice_ans3_icon);
            imgAns3.setImageResource(currentQuestion.icons.get(2));
            TextView tAns3 = view.findViewById(R.id.sentence_choice_ans3);
            tAns3.setText(currentQuestion.choices.get(2));

            // ans4
            ImageView imgAns4 = view.findViewById(R.id.sentence_choice_ans4_icon);
            imgAns4.setImageResource(currentQuestion.icons.get(3));
            TextView tAns4 = view.findViewById(R.id.sentence_choice_ans4);
            tAns4.setText(currentQuestion.choices.get(3));

            // continue layout
            LinearLayout continueLayout = view.findViewById(R.id.sentence_choice_continue_layout);
            ImageView imgContinueImage = view.findViewById(R.id.sentence_choice_continue_image);
            ImageView imgContinueWord = view.findViewById(R.id.sentence_choice_continue_word);
            continueLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    System.out.println("next");
                    currentQuestionIndex += 1;
                    initSentenceChoiceQuestionLayout();
                }
            });


            // text listener
            tAns1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tAns1.setClickable(false);
                    tAns2.setClickable(false);
                    tAns3.setClickable(false);
                    tAns4.setClickable(false);
                    imgContinueImage.setImageResource(R.drawable.karushifa);
                    imgContinueWord.setImageResource(R.drawable.curse);
                    tAns1.setBackgroundResource(R.drawable.text_rounded_border_wrong);
                    if (tAns1.getText().toString().equals(currentQuestion.answer)) {
                        tAns1.setBackgroundResource(R.drawable.text_rounded_border_correct);
                        imgContinueImage.setImageResource(R.drawable.karoshifa2);
                        imgContinueWord.setImageResource(R.drawable.dock);
                    }
                    else if (tAns2.getText().toString().equals(currentQuestion.answer))
                        tAns2.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns3.getText().toString().equals(currentQuestion.answer))
                        tAns3.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns4.getText().toString().equals(currentQuestion.answer))
                        tAns4.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    showAnswer(tTitle, currentQuestion.question, currentQuestion.answer, currentQuestion.transSentence);
                    continueLayout.setVisibility(View.VISIBLE);
                }
            });
            tAns2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tAns1.setClickable(false);
                    tAns2.setClickable(false);
                    tAns3.setClickable(false);
                    tAns4.setClickable(false);
                    imgContinueImage.setImageResource(R.drawable.karushifa);
                    imgContinueWord.setImageResource(R.drawable.curse);
                    tAns2.setBackgroundResource(R.drawable.text_rounded_border_wrong);
                    if (tAns1.getText().toString().equals(currentQuestion.answer))
                        tAns1.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns2.getText().toString().equals(currentQuestion.answer)) {
                        tAns2.setBackgroundResource(R.drawable.text_rounded_border_correct);
                        imgContinueImage.setImageResource(R.drawable.karoshifa2);
                        imgContinueWord.setImageResource(R.drawable.dock);
                    }
                    else if (tAns3.getText().toString().equals(currentQuestion.answer))
                        tAns3.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns4.getText().toString().equals(currentQuestion.answer))
                        tAns4.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    showAnswer(tTitle, currentQuestion.question, currentQuestion.answer, currentQuestion.transSentence);
                    continueLayout.setVisibility(View.VISIBLE);
                }
            });
            tAns3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tAns1.setClickable(false);
                    tAns2.setClickable(false);
                    tAns3.setClickable(false);
                    tAns4.setClickable(false);
                    imgContinueImage.setImageResource(R.drawable.karushifa);
                    imgContinueWord.setImageResource(R.drawable.curse);
                    tAns3.setBackgroundResource(R.drawable.text_rounded_border_wrong);
                    if (tAns1.getText().toString().equals(currentQuestion.answer))
                        tAns1.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns2.getText().toString().equals(currentQuestion.answer))
                        tAns2.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns3.getText().toString().equals(currentQuestion.answer)) {
                        tAns3.setBackgroundResource(R.drawable.text_rounded_border_correct);
                        imgContinueImage.setImageResource(R.drawable.karoshifa2);
                        imgContinueWord.setImageResource(R.drawable.dock);
                    }
                    else if (tAns4.getText().toString().equals(currentQuestion.answer))
                        tAns4.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    showAnswer(tTitle, currentQuestion.question, currentQuestion.answer, currentQuestion.transSentence);
                    continueLayout.setVisibility(View.VISIBLE);
                }
            });
            tAns4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    tAns1.setClickable(false);
                    tAns2.setClickable(false);
                    tAns3.setClickable(false);
                    tAns4.setClickable(false);
                    imgContinueImage.setImageResource(R.drawable.karushifa);
                    imgContinueWord.setImageResource(R.drawable.curse);
                    tAns4.setBackgroundResource(R.drawable.text_rounded_border_wrong);
                    if (tAns1.getText().toString().equals(currentQuestion.answer))
                        tAns1.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns2.getText().toString().equals(currentQuestion.answer))
                        tAns2.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns3.getText().toString().equals(currentQuestion.answer))
                        tAns3.setBackgroundResource(R.drawable.text_rounded_border_correct);
                    else if (tAns4.getText().toString().equals(currentQuestion.answer)) {
                        tAns4.setBackgroundResource(R.drawable.text_rounded_border_correct);
                        imgContinueImage.setImageResource(R.drawable.karoshifa2);
                        imgContinueWord.setImageResource(R.drawable.dock);
                    }
                    showAnswer(tTitle, currentQuestion.question, currentQuestion.answer, currentQuestion.transSentence);
                    continueLayout.setVisibility(View.VISIBLE);
                }
            });
            binding.sentenceChoiceLayout.addView(view);
        }
    }

    private void showAnswer(TextView title, String question, String ans, String trans) {
        // 將 "____" 替換為 ans
        String formattedQuestion = question.replace("____", ans);

        // 創建一個新的文本，將 formattedQuestion 和 trans 組合在一起
        String fullText = formattedQuestion + " " + trans;

        // 創建 SpannableString
        SpannableString spannableString = new SpannableString(fullText);

        // 找到 ans 在整個字符串中的起始位置和結束位置
        int ansStart = formattedQuestion.indexOf(ans);
        int ansEnd = ansStart + ans.length();

        // 找到 trans 在整個字符串中的起始位置
        int transStart = formattedQuestion.length() + 1; // 加 1 是因為中間加了個空格

        // 將 question 設置為黑色
        spannableString.setSpan(new ForegroundColorSpan(Color.BLACK), 0, formattedQuestion.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 將 ans 部分設置為綠色
        spannableString.setSpan(new ForegroundColorSpan(Color.GREEN), ansStart, ansEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 將 trans 部分設置為 #003377 顏色
        spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#003377")), transStart, fullText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 將格式化後的文本設置到 TextView 中
        title.setText(spannableString);
    }


}