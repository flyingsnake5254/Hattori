package com.example.hattori.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface WordDao {
    // 插入單個單字
    @Insert
    void insertWord(Word word);

    // 查詢所有單字
    @Query("SELECT * FROM words")
    List<Word> getAllWords();

    // 根據英文單字查詢
    @Query("SELECT * FROM words WHERE word = :targetWord")
    Word getWordByWord(String targetWord);

    // 更新 Word
    @Update
    void updateWord(Word word);

    // 刪除 Word
    @Delete
    void deleteWord(Word word);

    // 根據英文單字，刪除 Word
    @Query("DELETE FROM words WHERE word = :targetWord")
    void deleteWord(String targetWord);


}
