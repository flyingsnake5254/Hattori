package com.example.hattori.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "words")
public class Word {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String word;
    private String kk;
    private String word_zh;
    private String sentence;
    private String sentence_zh;
    private String answer;
    private String remark;

    public Word(String word, String kk, String word_zh, String sentence, String sentence_zh, String answer, String remark) {
        this.word = word;
        this.kk = kk;
        this.word_zh = word_zh;
        this.sentence = sentence;
        this.sentence_zh = sentence_zh;
        this.answer = answer;
        this.remark = remark;
    }

    // getter and setter

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getKk() {
        return kk;
    }

    public void setKk(String kk) {
        this.kk = kk;
    }

    public String getWord_zh() {
        return word_zh;
    }

    public void setWord_zh(String word_zh) {
        this.word_zh = word_zh;
    }

    public String getSentence() {
        return sentence;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public String getSentence_zh() {
        return sentence_zh;
    }

    public void setSentence_zh(String sentence_zh) {
        this.sentence_zh = sentence_zh;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
