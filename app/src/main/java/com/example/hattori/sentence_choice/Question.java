package com.example.hattori.sentence_choice;

import com.example.hattori.R;
import com.example.hattori.database.Word;

import java.util.Arrays;
import java.util.List;

public class Question {
    String question = "";
    String transSentence = "";
    String answer = "";
    List<Integer> icons;
    List<String> choices;

    public Question(Word word, List<Integer> icons, List<String> choices) {
        this.question = word.getSentence().replace(word.getAnswer(), "____");
        this.transSentence = "\n" + word.getSentence_zh();
        this.answer = word.getAnswer();
        this.icons = icons;
        this.choices = choices;
    }
}
