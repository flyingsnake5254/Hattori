package com.example.hattori.Utils;

import android.content.Context;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;

import com.example.hattori.GlobalVariable;

import java.util.ArrayList;
import java.util.Locale;

public class SpeechUtil {

    private TextToSpeech textToSpeech;

    public SpeechUtil(Context context) {
        textToSpeech = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS) {
                    int result = textToSpeech.setLanguage(Locale.getDefault());
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("TTS", "語言不支援");
                    }
                    setVoice(GlobalVariable.CURRENT_ZH_VOICE);
                } else {
                    Log.e("TTS", "初始化失敗");
                }
            }
        });
    }

    public void speak(String text) {
        if (textToSpeech != null) {

            for (Voice voice : textToSpeech.getVoices()) {
                Locale locale = voice.getLocale();
                if (locale.getLanguage().equals("en")) {
                    Log.d("sherlock", voice.getName());
                }
            }
            int result = textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
            if (result == TextToSpeech.ERROR) {
                Log.e("TTS", "語音播放失敗");
            }
        }
    }

    public void speakWithDiffVoice(ArrayList<String> contents, ArrayList<Voice> voices) {
        System.out.println("in speakWithDiffVoice");
        if (contents.isEmpty()) {
            System.out.println("out speakWithDiffVoice");
            return;
        }
        String content = contents.remove(0);
        Voice voice = voices.remove(0);

        if (voice != null) {
            textToSpeech.setVoice(voice);
        }
        textToSpeech.speak(content, TextToSpeech.QUEUE_FLUSH, null, "utteranceId");
        textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String s) {
                System.out.println("start : " + s);
            }

            @Override
            public void onDone(String s) {
                System.out.println("done : " + s);
                speakWithDiffVoice(contents, voices);
            }

            @Override
            public void onError(String s) {

            }
        });
    }

    public void shutdown() {
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    public void setVoice(String voiceName) {
        for (Voice voice : textToSpeech.getVoices()) {
            if (voice.getName().equals(voiceName)) {
                textToSpeech.setVoice(voice);
                break;
            }
        }
    }

    public Voice getVoiceByName(String voiceName) {
        for (Voice voice : textToSpeech.getVoices()) {
            if (voice.getName().equals(voiceName)) {
                return voice;
            }
        }
        return null;
    }
}
