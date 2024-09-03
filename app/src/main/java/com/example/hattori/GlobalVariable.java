package com.example.hattori;

import android.speech.tts.Voice;

import java.util.ArrayList;

public class GlobalVariable {
    // 單字卡單頁顯示的數量
    public static int PAGE_ITEM_SIZE = 3;



    // 英聽播放設定：單字、中文、例句、翻譯的 checkbox state, 播放次數
    public static boolean[] VOICE_SETTING_CHECKED = {true, true, false, false};
    public static int[] VOICE_SETTING_TIMES = {1, 1, 0, 0};


    // 中文語音
    public static String[] VOICE_ZH = {
            "zh-TW-language",
            "cmn-tw-x-ctd-network",
            "cmn-tw-x-cte-network",
            "cmn-tw-x-ctc-network",
            "cmn-tw-x-ctc-local",
            "cmn-tw-x-ctd-local",
            "cmn-tw-x-cte-local"
    };
    public static String CURRENT_ZH_VOICE = "zh-TW-language";


    // 英文語音
    public static String[] VOICE_EN = {
            "en-AU-language",
            "en-us-x-tpf-local",
            "en-in-x-end-network",
            "en-au-x-aua-network",
            "en-GB-language",
            "en-gb-x-gba-network",
            "en-us-x-sfg-network",
            "en-au-x-aud-local",
            "en-au-x-aua-local",
            "en-gb-x-gbc-network",
            "en-us-x-sfg-local",
            "en-in-x-ene-local",
            "en-us-x-iob-local",
            "en-au-x-auc-network",
            "en-gb-x-gbc-local",
            "en-us-x-tpd-network",
            "en-au-x-aub-local",
            "en-in-x-end-local",
            "en-gb-x-gbb-local",
            "en-us-x-iob-network",
            "en-us-x-iol-network",
            "en-us-x-iom-network",
            "en-NG-language",
            "en-in-x-enc-local",
            "en-gb-x-gbd-local",
            "en-gb-x-gbg-local",
            "en-gb-x-rjs-local",
            "en-us-x-iom-local",
            "en-in-x-enc-network",
            "en-au-x-aub-network",
            "en-in-x-ene-network",
            "en-ng-x-tfn-network",
            "en-US-language",
            "en-gb-x-gba-local",
            "en-ng-x-tfn-local",
            "en-au-x-aud-network",
            "en-us-x-tpd-local",
            "en-gb-x-rjs-network",
            "en-in-x-ena-network",
            "en-au-x-auc-local",
            "en-gb-x-gbb-network",
            "en-us-x-iog-network",
            "en-gb-x-gbg-network",
            "en-us-x-tpf-network",
            "en-gb-x-gbd-network",
            "en-IN-language",
            "en-us-x-iog-local",
            "en-in-x-ena-local",
            "en-us-x-tpc-local",
            "en-us-x-iol-local"
    };
    public static String CURRENT_EN_VOICE = "en-AU-language";
}
