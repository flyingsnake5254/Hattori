package com.example.hattori.Utils;

import com.example.hattori.database.Word;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomItemsSelector {

    public static <T> List<T> getRandomItemsFromList(List<T> list, int n) {
        List<T> copy = new ArrayList<>(list); // 創建一個副本以避免改變原列表
        Collections.shuffle(copy); // 隨機打亂列表順序

        if (n >= list.size()) {
            return copy; // 如果 n 大於等於 list 的大小，返回打亂後的整個列表
        }

        return copy.subList(0, n); // 返回前 n 個元素
    }

    public static List<String> getRandomWords(List<Word> wordList, Word targetWord, int n) {
        // 過濾掉與目標 Word 相等的 Word
        List<Word> filteredList = new ArrayList<>();
        for (Word word : wordList) {
            if (!word.getWord().equals(targetWord.getWord())) {
                filteredList.add(word);
            }
        }

        // 如果過濾後的列表小於 n，調整 n
        if (n > filteredList.size()) {
            n = filteredList.size();
        }

        // 隨機打亂列表並選取前 n 個 Word 的 word 屬性
        Collections.shuffle(filteredList, new Random());
        List<String> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            result.add(filteredList.get(i).getAnswer());
        }

        // 添加原本的 Word 的 word
        result.add(targetWord.getAnswer());

        // 最後隨機打亂結果列表
        Collections.shuffle(result, new Random());

        return result;
    }
}
